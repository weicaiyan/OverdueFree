package com.overduefree.module.lead.service.impl;

import com.alibaba.excel.EasyExcel;
import com.overduefree.auth.AdminPrincipal;
import com.overduefree.auth.CurrentAdminContext;
import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.module.lead.dto.AdminLeadQuery;
import com.overduefree.module.lead.dto.LeadExportRequest;
import com.overduefree.module.lead.dto.LeadListItem;
import com.overduefree.module.lead.service.AdminLeadService;
import com.overduefree.module.lead.service.LeadExportService;
import com.overduefree.module.operationlog.entity.OperationLog;
import com.overduefree.module.operationlog.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LeadExportServiceImpl implements LeadExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Map<String, String> DEBT_TYPE_DISPLAY_MAP = createDisplayMap(new String[][] {
        {"ONLINE_LOAN", "网贷"},
        {"CREDIT_CARD", "信用卡"},
        {"CREDIT_LOAN", "信用贷"},
        {"CAR_LOAN", "车贷"},
        {"OTHER", "其他"}
    });
    private static final Map<String, String> SOURCE_DISPLAY_MAP = createDisplayMap(new String[][] {
        {"AI_CHAT", "AI聊天"},
        {"PLAN_ASSESSMENT", "规划表单"},
        {"DEBT_PLAN", "规划表单"},
        {"HOME_CTA", "首页按钮"}
    });
    private static final Map<String, String> EVENT_TYPE_DISPLAY_MAP = createDisplayMap(new String[][] {
        {"VIEW_WECHAT_QR", "查看顾问二维码"},
        {"CLICK_HOME_ENTRY", "点击首页入口"},
        {"HOME_CTA", "点击首页咨询按钮"},
        {"PROFILE_CTA", "点击个人中心咨询"},
        {"CASE_APPLY", "点击案例申请"},
        {"ARTICLE_CTA", "点击资讯咨询按钮"},
        {"VIEW_HOME_VIDEO", "查看首页视频"},
        {"VIEW_CASE_DETAIL", "查看案例详情"},
        {"VIEW_ARTICLE_DETAIL", "查看资讯详情"},
        {"USE_CALCULATOR", "使用利率计算器"}
    });
    private static final List<LeadExportField> DEFAULT_FIELDS = Arrays.asList(
        new LeadExportField("rowType", "线索类型", item -> displayRowType(item.getRowType())),
        new LeadExportField("customerId", "客户ID", LeadListItem::getCustomerId),
        new LeadExportField("leadId", "线索ID", LeadListItem::getLeadId),
        new LeadExportField("phone", "手机号", LeadListItem::getPhone),
        new LeadExportField("surname", "称呼", LeadListItem::getSurname),
        new LeadExportField("region", "地区", LeadListItem::getRegion),
        new LeadExportField("debtAmount", "债务金额", LeadExportServiceImpl::displayAmount),
        new LeadExportField("debtType", "债务类型", item -> displayMappedValue(item.getDebtType(), DEBT_TYPE_DISPLAY_MAP)),
        new LeadExportField("source", "来源", item -> displayMappedValue(item.getSource(), SOURCE_DISPLAY_MAP)),
        new LeadExportField("firstLoginAt", "首次登录时间", item -> displayDateTime(item.getFirstLoginAt())),
        new LeadExportField("leadCreatedAt", "提交时间", item -> displayDateTime(item.getLeadCreatedAt())),
        new LeadExportField("viewedWechatQr", "是否查看企微二维码", item -> displayBoolean(item.getViewedWechatQr())),
        new LeadExportField("lastWechatQrViewAt", "最近查看企微二维码时间", item -> displayDateTime(item.getLastWechatQrViewAt())),
        new LeadExportField("latestEventType", "最近行为", item -> displayMappedValue(item.getLatestEventType(), EVENT_TYPE_DISPLAY_MAP)),
        new LeadExportField("latestEventAt", "最近行为时间", item -> displayDateTime(item.getLatestEventAt())),
        new LeadExportField("historyCount", "历史线索数", LeadListItem::getHistoryCount)
    );
    private static final Map<String, LeadExportField> FIELD_MAP = DEFAULT_FIELDS.stream()
        .collect(Collectors.toMap(LeadExportField::getName, Function.identity(), (left, right) -> left, LinkedHashMap::new));

    private final AdminLeadService adminLeadService;
    private final OperationLogService operationLogService;

    public LeadExportServiceImpl(AdminLeadService adminLeadService, OperationLogService operationLogService) {
        this.adminLeadService = adminLeadService;
        this.operationLogService = operationLogService;
    }

    @Override
    public void exportLeads(LeadExportRequest request, HttpServletRequest servletRequest, HttpServletResponse response) {
        LeadExportRequest exportRequest = request == null ? new LeadExportRequest() : request;
        AdminLeadQuery filters = exportRequest.getFilters() == null ? new AdminLeadQuery() : exportRequest.getFilters();
        List<LeadListItem> rows = adminLeadService.listLeadsForExport(filters);
        List<LeadExportField> fields = resolveFields(exportRequest.getFields());

        writeExcel(response, fields, rows);
        AdminPrincipal principal = CurrentAdminContext.getRequired();
        operationLogService.record(
            principal.getAdminId(),
            OperationLog.ACTION_EXPORT_LEADS,
            OperationLog.TARGET_TYPE_LEAD,
            null,
            resolveIp(servletRequest),
            servletRequest.getHeader("User-Agent"),
            buildLogDetail(filters, fields, rows.size())
        );
    }

    private void writeExcel(HttpServletResponse response, List<LeadExportField> fields, List<LeadListItem> rows) {
        String fileName = "线索导出-" + LocalDate.now().format(DATE_FORMATTER) + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        try {
            EasyExcel.write(response.getOutputStream())
                .head(buildHead(fields))
                .sheet("线索")
                .doWrite(buildData(fields, rows));
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR, "导出文件生成失败");
        }
    }

    private List<LeadExportField> resolveFields(List<String> fieldNames) {
        if (CollectionUtils.isEmpty(fieldNames)) {
            return DEFAULT_FIELDS;
        }
        List<LeadExportField> fields = new ArrayList<>();
        for (String fieldName : fieldNames) {
            if (!StringUtils.hasText(fieldName)) {
                continue;
            }
            LeadExportField field = FIELD_MAP.get(fieldName);
            if (field == null) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "不支持的导出字段：" + fieldName);
            }
            fields.add(field);
        }
        if (fields.isEmpty()) {
            return DEFAULT_FIELDS;
        }
        return fields;
    }

    private List<List<String>> buildHead(List<LeadExportField> fields) {
        return fields.stream()
            .map(field -> Arrays.asList(field.getTitle()))
            .collect(Collectors.toList());
    }

    private List<List<Object>> buildData(List<LeadExportField> fields, List<LeadListItem> rows) {
        return rows.stream().map(row -> fields.stream()
            .map(field -> field.getValueGetter().apply(row))
            .collect(Collectors.toList())).collect(Collectors.toList());
    }

    private Map<String, Object> buildLogDetail(AdminLeadQuery filters, List<LeadExportField> fields, int rowCount) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("filters", filters);
        detail.put("fields", fields.stream().map(LeadExportField::getName).collect(Collectors.toList()));
        detail.put("rowCount", rowCount);
        return detail;
    }

    private String resolveIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwardedFor)) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static String displayRowType(String rowType) {
        if (AdminLeadQuery.TYPE_SUBMITTED.equals(rowType)) {
            return "已提交线索";
        }
        if (AdminLeadQuery.TYPE_LOGIN_ONLY.equals(rowType)) {
            return "仅登录客户";
        }
        return rowType;
    }

    private static String displayMappedValue(String value, Map<String, String> displayMap) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return displayMap.getOrDefault(value, value);
    }

    private static String displayBoolean(Boolean value) {
        if (value == null) {
            return "";
        }
        return Boolean.TRUE.equals(value) ? "是" : "否";
    }

    private static String displayDateTime(LocalDateTime value) {
        if (value == null) {
            return "";
        }
        return value.format(DATE_TIME_FORMATTER);
    }

    private static Object displayAmount(LeadListItem item) {
        BigDecimal debtAmount = item.getDebtAmount();
        if (debtAmount == null) {
            return "";
        }
        return debtAmount;
    }

    private static Map<String, String> createDisplayMap(String[][] entries) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String[] entry : entries) {
            map.put(entry[0], entry[1]);
        }
        return Collections.unmodifiableMap(map);
    }

    private static class LeadExportField {

        private final String name;
        private final String title;
        private final Function<LeadListItem, Object> valueGetter;

        LeadExportField(String name, String title, Function<LeadListItem, Object> valueGetter) {
            this.name = name;
            this.title = title;
            this.valueGetter = valueGetter;
        }

        String getName() {
            return name;
        }

        String getTitle() {
            return title;
        }

        Function<LeadListItem, Object> getValueGetter() {
            return valueGetter;
        }
    }
}
