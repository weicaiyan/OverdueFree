package com.overduefree.module.asset.service;

import com.overduefree.module.asset.dto.FileUploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    FileUploadResult upload(MultipartFile file, String category);
}
