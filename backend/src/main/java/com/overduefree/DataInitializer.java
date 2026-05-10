package com.overduefree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String[] FIXED_ASSET_KEYS = {
        "HOME_VIDEO", "HOME_VIDEO_COVER", "AI_CONSULT_BANNER",
        "LOAN_CALCULATOR_BANNER", "DEBT_PLAN_BANNER", "WECHAT_QR"
    };
    private static final String[] FIXED_ASSET_TITLES = {
        "首页视频", "视频封面", "AI债务咨询图",
        "网贷利率计算器图", "规划优化债务图", "企业微信二维码"
    };

    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.init.boss-password:boss123456}")
    private String bossPassword;

    public DataInitializer(JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initSysConfig();
        initBossAdmin();
        initAssets();
        initDemoArticles();
        initDemoCases();
    }

    private void initSysConfig() {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM sys_config WHERE config_key = ?", Integer.class, "customer_login_expire_days");
        if (count != null && count == 0) {
            jdbc.update("INSERT INTO sys_config (config_key, config_value, description) VALUES (?, ?, ?)",
                "customer_login_expire_days", "7", "客户登录token有效期（天）");
            log.info("已初始化系统配置: customer_login_expire_days=7");
        }
    }

    private void initBossAdmin() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM admin_user", Integer.class);
        if (count != null && count == 0) {
            String passwordHash = passwordEncoder.encode(bossPassword);
            jdbc.update(
                "INSERT INTO admin_user (username, password_hash, display_name, role, status) VALUES (?, ?, ?, ?, ?)",
                "boss", passwordHash, "老板", "BOSS", "ACTIVE");
            log.info("已创建默认老板账号: boss");
        }
    }

    private void initAssets() {
        for (int i = 0; i < FIXED_ASSET_KEYS.length; i++) {
            String assetKey = FIXED_ASSET_KEYS[i];
            Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM asset_resource WHERE asset_key = ?", Integer.class, assetKey);
            if (count != null && count == 0) {
                jdbc.update(
                    "INSERT INTO asset_resource (asset_key, title, status) VALUES (?, ?, ?)",
                    assetKey, FIXED_ASSET_TITLES[i], "ACTIVE");
                log.info("已初始化素材位: {}", assetKey);
            }
        }
    }

    private void initDemoArticles() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM article", Integer.class);
        if (count != null && count == 0) {
            LocalDateTime now = LocalDateTime.now();
            String[][] articles = {
                {"债务逾期后如何与平台协商", "了解协商的基本策略和注意事项，避免踩坑", "债务逾期后，很多借款人会感到焦虑和无助。实际上，大多数平台都有协商还款的渠道。第一步是主动联系平台客服，表明还款意愿..."},
                {"信用卡逾期对征信的影响", "详细解读征信报告中的逾期记录", "信用卡逾期会在个人征信报告中留下不良记录。根据规定，逾期记录会在还清欠款后保留5年..."},
                {"网贷逾期的法律风险分析", "从法律角度看网贷逾期的后果", "网贷逾期属于民事纠纷范畴，但并不意味着可以忽视。长期逾期可能面临起诉、强制执行等后果..."},
                {"如何制定合理的还款计划", "根据收入和支出合理安排还款", "制定还款计划的第一步是全面梳理自己的债务情况，包括各平台的欠款金额、利率、逾期天数等..."}
            };
            for (int i = 0; i < articles.length; i++) {
                jdbc.update(
                    "INSERT INTO article (title, summary, content_text, publish_time, sort_order, status) VALUES (?, ?, ?, ?, ?, ?)",
                    articles[i][0], articles[i][1], articles[i][2], now, i, "DRAFT");
            }
            log.info("已初始化{}条演示资讯", articles.length);
        }
    }

    private void initDemoCases() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM success_case", Integer.class);
        if (count != null && count == 0) {
            String[][] cases = {
                {"李先生", "138****1234", "网贷、信用卡", "85000.00", "协商分期60期，减免部分罚息", "张先生因失业导致多平台逾期，通过协商最终达成分期方案..."},
                {"王女士", "139****5678", "信用卡", "120000.00", "停息挂账，分48期还款", "王女士因家庭变故无力偿还信用卡，经过与银行协商..."},
                {"赵先生", "137****9012", "网贷", "35000.00", "减免违约金，一次性结清", "赵先生的网贷逾期半年后，通过与平台协商..."},
                {"刘女士", "136****3456", "信用贷、车贷", "250000.00", "重组债务，分36期还款", "刘女士有两笔贷款同时逾期，通过债务重组..."}
            };
            for (int i = 0; i < cases.length; i++) {
                jdbc.update(
                    "INSERT INTO success_case (display_name, masked_phone, overdue_platforms, overdue_amount, handling_plan, detail_text, sort_order, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    cases[i][0], cases[i][1], cases[i][2],
                    new java.math.BigDecimal(cases[i][3]),
                    cases[i][4], cases[i][5], i, "DRAFT");
            }
            log.info("已初始化{}条演示案例", cases.length);
        }
    }
}
