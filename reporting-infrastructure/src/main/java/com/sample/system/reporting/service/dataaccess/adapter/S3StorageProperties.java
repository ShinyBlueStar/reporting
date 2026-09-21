package com.sample.system.reporting.service.dataaccess.adapter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * S3 API settings. Works with AWS S3 and any S3-compatible store (SeaweedFS, Ceph RGW, Garage, MinIO...).
 * Leave {@code endpoint} empty for AWS; set it and {@code pathStyleAccess=true} for self-hosted stores.
 */
@ConfigurationProperties(prefix = "reporting.storage.s3")
public record S3StorageProperties(
        String endpoint,
        String region,
        String accessKey,
        String secretKey,
        String bucket,
        boolean pathStyleAccess,
        boolean createBucketIfMissing,
        boolean serverSideEncryption,
        int retentionDays) {

    public S3StorageProperties {
        if (region == null || region.isBlank()) {
            region = "us-east-1";
        }
        if (bucket == null || bucket.isBlank()) {
            bucket = "reporting-service";
        }
        if (retentionDays <= 0) {
            retentionDays = 2;
        }
    }
}
