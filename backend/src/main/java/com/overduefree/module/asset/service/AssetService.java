package com.overduefree.module.asset.service;

import com.overduefree.module.asset.dto.AssetResourceResult;
import com.overduefree.module.asset.dto.AssetUpdateRequest;

import java.util.List;
import java.util.Map;

public interface AssetService {

    Map<String, AssetResourceResult> getHomeAssetMap();

    List<AssetResourceResult> listAssets();

    AssetResourceResult updateAsset(String assetKey, AssetUpdateRequest request);
}
