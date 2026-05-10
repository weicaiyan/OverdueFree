package com.overduefree.module.caseitem.service;

import com.overduefree.common.PageResult;
import com.overduefree.module.caseitem.dto.SuccessCaseQuery;
import com.overduefree.module.caseitem.dto.SuccessCaseResult;
import com.overduefree.module.caseitem.dto.SuccessCaseSaveRequest;

public interface SuccessCaseService {

    PageResult<SuccessCaseResult> listPublished(Long page, Long pageSize);

    SuccessCaseResult getPublished(Long id);

    PageResult<SuccessCaseResult> listAdmin(SuccessCaseQuery query);

    SuccessCaseResult create(SuccessCaseSaveRequest request);

    SuccessCaseResult update(Long id, SuccessCaseSaveRequest request);

    void delete(Long id);

    SuccessCaseResult publish(Long id);

    SuccessCaseResult offline(Long id);
}
