package com.overduefree.module.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.overduefree.auth.CurrentAdminContext;
import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.module.asset.dto.AssetResourceResult;
import com.overduefree.module.asset.dto.AssetUpdateRequest;
import com.overduefree.module.asset.entity.AssetResource;
import com.overduefree.module.asset.mapper.AssetResourceMapper;
import com.overduefree.module.asset.service.AssetService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {

    private static final Map<String, String> APP_ASSET_KEYS = new LinkedHashMap<>();
    private static final List<String> FIXED_ASSET_KEYS = Arrays.asList(
        AssetResource.KEY_HOME_VIDEO,
        AssetResource.KEY_HOME_VIDEO_COVER,
        AssetResource.KEY_AI_CONSULT_BANNER,
        AssetResource.KEY_LOAN_CALCULATOR_BANNER,
        AssetResource.KEY_DEBT_PLAN_BANNER,
        AssetResource.KEY_WECHAT_QR
    );

    static {
        APP_ASSET_KEYS.put("homeVideo", AssetResource.KEY_HOME_VIDEO);
        APP_ASSET_KEYS.put("homeVideoCover", AssetResource.KEY_HOME_VIDEO_COVER);
        APP_ASSET_KEYS.put("aiConsultBanner", AssetResource.KEY_AI_CONSULT_BANNER);
        APP_ASSET_KEYS.put("loanCalculatorBanner", AssetResource.KEY_LOAN_CALCULATOR_BANNER);
        APP_ASSET_KEYS.put("debtPlanBanner", AssetResource.KEY_DEBT_PLAN_BANNER);
        APP_ASSET_KEYS.put("wechatQr", AssetResource.KEY_WECHAT_QR);
    }

    private final AssetResourceMapper assetResourceMapper;

    public AssetServiceImpl(AssetResourceMapper assetResourceMapper) {
        this.assetResourceMapper = assetResourceMapper;
    }

    @Override
    public Map<String, AssetResourceResult> getHomeAssetMap() {
        List<AssetResource> assets = selectFixedAssets();
        Map<String, AssetResource> assetMap = assets.stream()
            .collect(Collectors.toMap(AssetResource::getAssetKey, asset -> asset));
        Map<String, AssetResourceResult> result = new LinkedHashMap<>();
        APP_ASSET_KEYS.forEach((name, assetKey) -> result.put(name, toResult(assetMap.get(assetKey))));
        return result;
    }

    @Override
    public List<AssetResourceResult> listAssets() {
        return selectFixedAssets().stream().map(this::toResult).collect(Collectors.toList());
    }

    @Override
    public AssetResourceResult updateAsset(String assetKey, AssetUpdateRequest request) {
        ensureFixedAssetKey(assetKey);
        AssetResource asset = assetResourceMapper.selectOne(new LambdaQueryWrapper<AssetResource>()
            .eq(AssetResource::getAssetKey, assetKey));
        if (asset == null) {
            asset = new AssetResource();
            asset.setAssetKey(assetKey);
            asset.setStatus(AssetResource.STATUS_ACTIVE);
        }
        asset.setTitle(request.getTitle());
        asset.setFileUrl(request.getFileUrl());
        asset.setOriginalFileName(request.getOriginalFileName());
        asset.setMimeType(request.getMimeType());
        asset.setFileSize(request.getFileSize());
        asset.setUpdatedBy(CurrentAdminContext.getRequired().getAdminId());
        if (asset.getId() == null) {
            assetResourceMapper.insert(asset);
        } else {
            assetResourceMapper.update(null, new LambdaUpdateWrapper<AssetResource>()
                .eq(AssetResource::getId, asset.getId())
                .set(AssetResource::getTitle, asset.getTitle())
                .set(AssetResource::getFileUrl, asset.getFileUrl())
                .set(AssetResource::getOriginalFileName, asset.getOriginalFileName())
                .set(AssetResource::getMimeType, asset.getMimeType())
                .set(AssetResource::getFileSize, asset.getFileSize())
                .set(AssetResource::getUpdatedBy, asset.getUpdatedBy()));
        }
        return toResult(asset);
    }

    private List<AssetResource> selectFixedAssets() {
        return assetResourceMapper.selectList(new LambdaQueryWrapper<AssetResource>()
            .in(AssetResource::getAssetKey, FIXED_ASSET_KEYS)
            .orderByAsc(AssetResource::getId));
    }

    private void ensureFixedAssetKey(String assetKey) {
        if (!FIXED_ASSET_KEYS.contains(assetKey)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "素材位不存在");
        }
    }

    private AssetResourceResult toResult(AssetResource asset) {
        if (asset == null) {
            return null;
        }
        AssetResourceResult result = new AssetResourceResult();
        BeanUtils.copyProperties(asset, result);
        return result;
    }
}
