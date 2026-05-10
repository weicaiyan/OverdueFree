package com.overduefree.module.home.service.impl;

import com.overduefree.module.asset.service.AssetService;
import com.overduefree.module.home.dto.HomeResult;
import com.overduefree.module.home.dto.ServiceStepResult;
import com.overduefree.module.home.service.HomeService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements HomeService {

    private static final List<String[]> SERVICE_STEPS = Arrays.asList(
        new String[]{"提交信息", "信息绝对安全加密处理"},
        new String[]{"初步评估", "结合情况进行初步判断"},
        new String[]{"人工沟通", "专人回访进一步确认"},
        new String[]{"制定建议", "根据情况给出处理建议"}
    );

    private final AssetService assetService;

    public HomeServiceImpl(AssetService assetService) {
        this.assetService = assetService;
    }

    @Override
    public HomeResult getHome() {
        HomeResult result = new HomeResult();
        result.setAssets(assetService.getHomeAssetMap());
        result.setServiceSteps(SERVICE_STEPS.stream().map(this::toStepResult).collect(Collectors.toList()));
        return result;
    }

    private ServiceStepResult toStepResult(String[] step) {
        ServiceStepResult result = new ServiceStepResult();
        result.setTitle(step[0]);
        result.setDescription(step[1]);
        return result;
    }
}
