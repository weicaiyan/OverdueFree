package com.overduefree.module.home.dto;

import com.overduefree.module.asset.dto.AssetResourceResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class HomeResult {

    private Map<String, AssetResourceResult> assets = new LinkedHashMap<>();
    private List<ServiceStepResult> serviceSteps = new ArrayList<>();
}
