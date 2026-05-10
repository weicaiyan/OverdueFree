package com.overduefree.module.caseitem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.overduefree.auth.CurrentAdminContext;
import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.common.PageResult;
import com.overduefree.module.caseitem.dto.SuccessCaseQuery;
import com.overduefree.module.caseitem.dto.SuccessCaseResult;
import com.overduefree.module.caseitem.dto.SuccessCaseSaveRequest;
import com.overduefree.module.caseitem.entity.SuccessCase;
import com.overduefree.module.caseitem.mapper.SuccessCaseMapper;
import com.overduefree.module.caseitem.service.SuccessCaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

@Service
public class SuccessCaseServiceImpl implements SuccessCaseService {

    private static final long DEFAULT_PAGE = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 100L;

    private final SuccessCaseMapper successCaseMapper;

    public SuccessCaseServiceImpl(SuccessCaseMapper successCaseMapper) {
        this.successCaseMapper = successCaseMapper;
    }

    @Override
    public PageResult<SuccessCaseResult> listPublished(Long page, Long pageSize) {
        Page<SuccessCase> pageRequest = new Page<>(normalizePage(page), normalizePageSize(pageSize));
        Page<SuccessCase> resultPage = successCaseMapper.selectPage(pageRequest, new LambdaQueryWrapper<SuccessCase>()
            .eq(SuccessCase::getStatus, SuccessCase.STATUS_PUBLISHED)
            .eq(SuccessCase::getDeleted, SuccessCase.DELETED_NO)
            .orderByDesc(SuccessCase::getSortOrder)
            .orderByDesc(SuccessCase::getId));
        return toPageResult(resultPage);
    }

    @Override
    public SuccessCaseResult getPublished(Long id) {
        SuccessCase successCase = successCaseMapper.selectOne(new LambdaQueryWrapper<SuccessCase>()
            .eq(SuccessCase::getId, id)
            .eq(SuccessCase::getStatus, SuccessCase.STATUS_PUBLISHED)
            .eq(SuccessCase::getDeleted, SuccessCase.DELETED_NO));
        if (successCase == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "成功案例不存在");
        }
        return toResult(successCase);
    }

    @Override
    public PageResult<SuccessCaseResult> listAdmin(SuccessCaseQuery query) {
        SuccessCaseQuery normalizedQuery = query == null ? new SuccessCaseQuery() : query;
        Page<SuccessCase> pageRequest = new Page<>(
            normalizePage(normalizedQuery.getPage()),
            normalizePageSize(normalizedQuery.getPageSize())
        );
        LambdaQueryWrapper<SuccessCase> wrapper = new LambdaQueryWrapper<SuccessCase>()
            .eq(SuccessCase::getDeleted, SuccessCase.DELETED_NO)
            .eq(StringUtils.hasText(normalizedQuery.getStatus()), SuccessCase::getStatus, normalizedQuery.getStatus())
            .and(StringUtils.hasText(normalizedQuery.getKeyword()), item -> item
                .like(SuccessCase::getDisplayName, normalizedQuery.getKeyword())
                .or()
                .like(SuccessCase::getOverduePlatforms, normalizedQuery.getKeyword()))
            .orderByDesc(SuccessCase::getSortOrder)
            .orderByDesc(SuccessCase::getUpdatedAt)
            .orderByDesc(SuccessCase::getId);
        return toPageResult(successCaseMapper.selectPage(pageRequest, wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SuccessCaseResult create(SuccessCaseSaveRequest request) {
        Long adminId = CurrentAdminContext.getRequired().getAdminId();
        SuccessCase successCase = new SuccessCase();
        fillCase(successCase, request);
        successCase.setStatus(SuccessCase.STATUS_DRAFT);
        successCase.setDeleted(SuccessCase.DELETED_NO);
        successCase.setCreatedBy(adminId);
        successCase.setUpdatedBy(adminId);
        successCaseMapper.insert(successCase);
        return toResult(successCase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SuccessCaseResult update(Long id, SuccessCaseSaveRequest request) {
        SuccessCase successCase = getAdminCase(id);
        fillCase(successCase, request);
        successCase.setUpdatedBy(CurrentAdminContext.getRequired().getAdminId());
        successCaseMapper.updateById(successCase);
        return toResult(successCase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SuccessCase successCase = getAdminCase(id);
        successCase.setDeleted(SuccessCase.DELETED_YES);
        successCase.setUpdatedBy(CurrentAdminContext.getRequired().getAdminId());
        successCaseMapper.updateById(successCase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SuccessCaseResult publish(Long id) {
        SuccessCase successCase = getAdminCase(id);
        successCase.setStatus(SuccessCase.STATUS_PUBLISHED);
        successCase.setUpdatedBy(CurrentAdminContext.getRequired().getAdminId());
        successCaseMapper.updateById(successCase);
        return toResult(successCase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SuccessCaseResult offline(Long id) {
        SuccessCase successCase = getAdminCase(id);
        successCase.setStatus(SuccessCase.STATUS_DRAFT);
        successCase.setUpdatedBy(CurrentAdminContext.getRequired().getAdminId());
        successCaseMapper.updateById(successCase);
        return toResult(successCase);
    }

    private SuccessCase getAdminCase(Long id) {
        SuccessCase successCase = successCaseMapper.selectOne(new LambdaQueryWrapper<SuccessCase>()
            .eq(SuccessCase::getId, id)
            .eq(SuccessCase::getDeleted, SuccessCase.DELETED_NO));
        if (successCase == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "成功案例不存在");
        }
        return successCase;
    }

    private void fillCase(SuccessCase successCase, SuccessCaseSaveRequest request) {
        successCase.setDisplayName(request.getDisplayName());
        successCase.setMaskedPhone(request.getMaskedPhone());
        successCase.setOverduePlatforms(request.getOverduePlatforms());
        successCase.setOverdueAmount(request.getOverdueAmount());
        successCase.setHandlingPlan(request.getHandlingPlan());
        successCase.setAvatarUrl(request.getAvatarUrl());
        successCase.setDetailText(request.getDetailText());
        successCase.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
    }

    private PageResult<SuccessCaseResult> toPageResult(Page<SuccessCase> page) {
        return new PageResult<>(
            page.getRecords().stream().map(this::toResult).collect(Collectors.toList()),
            page.getCurrent(),
            page.getSize(),
            page.getTotal()
        );
    }

    private SuccessCaseResult toResult(SuccessCase successCase) {
        SuccessCaseResult result = new SuccessCaseResult();
        BeanUtils.copyProperties(successCase, result);
        return result;
    }

    private long normalizePage(Long page) {
        return page == null || page < DEFAULT_PAGE ? DEFAULT_PAGE : page;
    }

    private long normalizePageSize(Long pageSize) {
        if (pageSize == null || pageSize < DEFAULT_PAGE) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }
}
