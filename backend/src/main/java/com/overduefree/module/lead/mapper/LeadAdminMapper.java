package com.overduefree.module.lead.mapper;

import com.overduefree.module.lead.dto.AdminLeadQuery;
import com.overduefree.module.lead.dto.LeadListItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LeadAdminMapper {

    List<LeadListItem> selectSubmittedRows(@Param("query") AdminLeadQuery query);

    List<LeadListItem> selectLoginOnlyRows(@Param("query") AdminLeadQuery query);
}
