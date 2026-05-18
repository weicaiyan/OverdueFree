package com.overduefree.module.asset.service.impl;

import com.overduefree.common.BusinessException;
import com.overduefree.config.UploadProperties;
import com.overduefree.module.asset.dto.FileUploadResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileUploadServiceImplTest {

    @TempDir
    private Path tempDir;

    @Test
    void shouldAcceptMp4WithOctetStreamMimeType() {
        FileUploadServiceImpl service = new FileUploadServiceImpl(uploadProperties());
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "demo.mp4",
            "application/octet-stream",
            new byte[] {1, 2, 3}
        );

        FileUploadResult result = service.upload(file, "VIDEO");

        assertThat(result.getUrl()).startsWith("/uploads/");
        assertThat(result.getUrl()).endsWith(".mp4");
        assertThat(result.getMimeType()).isEqualTo("application/octet-stream");
    }

    @Test
    void shouldRejectNonMp4VideoFile() {
        FileUploadServiceImpl service = new FileUploadServiceImpl(uploadProperties());
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "demo.mov",
            "application/octet-stream",
            new byte[] {1, 2, 3}
        );

        assertThatThrownBy(() -> service.upload(file, "VIDEO"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("视频仅支持 mp4");
    }

    private UploadProperties uploadProperties() {
        UploadProperties uploadProperties = new UploadProperties();
        uploadProperties.setBaseDir(tempDir.toString());
        uploadProperties.setPublicPrefix("/uploads");
        return uploadProperties;
    }
}
