package com.overduefree.module.caseitem.dto;

import lombok.Data;

@Data
public class SuccessCaseQuery {

    private String keyword;
    private String status;
    private Long page = 1L;
    private Long pageSize = 10L;
}
