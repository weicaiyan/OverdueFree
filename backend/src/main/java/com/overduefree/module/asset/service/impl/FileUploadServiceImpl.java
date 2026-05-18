package com.overduefree.module.asset.service.impl;

import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;
import com.overduefree.config.UploadProperties;
import com.overduefree.module.asset.dto.FileUploadResult;
import com.overduefree.module.asset.service.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private static final String CATEGORY_IMAGE = "IMAGE";
    private static final String CATEGORY_VIDEO = "VIDEO";
    private static final long IMAGE_MAX_SIZE = 20L * 1024L * 1024L;
    private static final long VIDEO_MAX_SIZE = 500L * 1024L * 1024L;
    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");
    private static final List<String> IMAGE_MIME_TYPES = Arrays.asList("image/jpeg", "image/png", "image/webp");
    private static final List<String> VIDEO_MIME_TYPES = Arrays.asList("video/mp4", "application/octet-stream");

    private final UploadProperties uploadProperties;

    public FileUploadServiceImpl(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    @Override
    public FileUploadResult upload(MultipartFile file, String category) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "请选择上传文件");
        }
        String normalizedCategory = normalizeCategory(category);
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() == null
            ? "unknown" : file.getOriginalFilename());
        String extension = resolveExtension(originalFileName);
        String mimeType = file.getContentType();
        validateFile(normalizedCategory, extension, mimeType, file.getSize());

        String datePath = LocalDate.now().format(DATE_PATH_FORMATTER);
        String savedFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path baseDir = Paths.get(uploadProperties.getBaseDir()).toAbsolutePath().normalize();
        Path targetDir = baseDir.resolve(datePath).normalize();
        Path targetFile = targetDir.resolve(savedFileName).normalize();
        if (!targetFile.startsWith(baseDir)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "上传文件路径不正确");
        }
        try {
            Files.createDirectories(targetDir);
            file.transferTo(targetFile.toFile());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR, "文件保存失败");
        }

        String url = uploadProperties.getPublicPrefix() + "/" + datePath + "/" + savedFileName;
        FileUploadResult result = new FileUploadResult();
        result.setUrl(url.replace("\\", "/"));
        result.setOriginalFileName(originalFileName);
        result.setMimeType(mimeType);
        result.setFileSize(file.getSize());
        return result;
    }

    private String normalizeCategory(String category) {
        if (!StringUtils.hasText(category)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件分类不能为空");
        }
        String normalizedCategory = category.trim().toUpperCase(Locale.ROOT);
        if (!CATEGORY_IMAGE.equals(normalizedCategory) && !CATEGORY_VIDEO.equals(normalizedCategory)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件分类不支持");
        }
        return normalizedCategory;
    }

    private String resolveExtension(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        if (!StringUtils.hasText(extension)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件扩展名不能为空");
        }
        return extension.toLowerCase(Locale.ROOT);
    }

    private void validateFile(String category, String extension, String mimeType, long fileSize) {
        if (!StringUtils.hasText(mimeType)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件类型不能为空");
        }
        if (CATEGORY_IMAGE.equals(category)) {
            if (!IMAGE_EXTENSIONS.contains(extension) || !IMAGE_MIME_TYPES.contains(mimeType)) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "图片仅支持 jpg、jpeg、png、webp");
            }
            if (fileSize > IMAGE_MAX_SIZE) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "图片大小不能超过20MB");
            }
            return;
        }
        if (!"mp4".equals(extension) || !VIDEO_MIME_TYPES.contains(mimeType)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "视频仅支持 mp4");
        }
        if (fileSize > VIDEO_MAX_SIZE) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "视频大小不能超过500MB");
        }
    }
}
