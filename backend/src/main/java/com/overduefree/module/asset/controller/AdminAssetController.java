package com.overduefree.module.asset.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.module.asset.dto.AssetResourceResult;
import com.overduefree.module.asset.dto.AssetUpdateRequest;
import com.overduefree.module.asset.dto.FileUploadResult;
import com.overduefree.module.asset.service.AssetService;
import com.overduefree.module.asset.service.FileUploadService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/admin")
public class AdminAssetController {

    private final AssetService assetService;
    private final FileUploadService fileUploadService;

    public AdminAssetController(AssetService assetService, FileUploadService fileUploadService) {
        this.assetService = assetService;
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/files/upload")
    public ApiResponse<FileUploadResult> upload(@RequestPart("file") MultipartFile file,
                                                @RequestParam String category) {
        return ApiResponse.ok(fileUploadService.upload(file, category));
    }

    @GetMapping("/assets")
    public ApiResponse<List<AssetResourceResult>> listAssets() {
        return ApiResponse.ok(assetService.listAssets());
    }

    @PutMapping("/assets/{assetKey}")
    public ApiResponse<AssetResourceResult> updateAsset(@PathVariable String assetKey,
                                                        @Valid @RequestBody AssetUpdateRequest request) {
        return ApiResponse.ok(assetService.updateAsset(assetKey, request));
    }
}
