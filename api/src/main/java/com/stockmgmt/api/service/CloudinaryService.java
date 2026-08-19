package com.stockmgmt.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Value("${cloudinary.upload-folder:stockpulse}")
    private String uploadFolder;

    public String uploadImage(MultipartFile file, String folder) {
        try {
            Uploader uploader = cloudinary.uploader();
            Map<?, ?> uploadResult = uploader.upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", String.format("%s/%s", uploadFolder, folder != null ? folder : "general"),
                            "resource_type", "image",
                            "quality", "auto",
                            "fetch_format", "auto",
                            "width", 800,
                            "crop", "limit",
                            "use_filename", true,
                            "unique_filename", true
                    )
            );

            String secureUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            log.info("Uploaded image to Cloudinary: publicId={}, url={}", publicId, secureUrl);
            return secureUrl;
        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {
            String publicId = extractPublicId(imageUrl);
            if (publicId == null) {
                return;
            }

            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Deleted image from Cloudinary: publicId={}, result={}", publicId, result);
        } catch (Exception e) {
            log.error("Failed to delete image from Cloudinary: url={}", imageUrl, e);
        }
    }

    private String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        try {
            String uploadPrefix = String.format("https://res.cloudinary.com/%s/image/upload/", cloudName);
            if (imageUrl.startsWith(uploadPrefix)) {
                String afterUpload = imageUrl.substring(uploadPrefix.length());
                int versionSlashIndex = afterUpload.indexOf("/v");
                if (versionSlashIndex != -1) {
                    return afterUpload.substring(versionSlashIndex + 1);
                }
                return afterUpload;
            }
            return null;
        } catch (Exception e) {
            log.warn("Could not extract public ID from URL: {}", imageUrl, e);
            return null;
        }
    }
}
