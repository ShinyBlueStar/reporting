package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.application.ports.output.ReportFileStoragePort;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketLifecycleConfiguration;
import software.amazon.awssdk.services.s3.model.ExpirationStatus;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.LifecycleExpiration;
import software.amazon.awssdk.services.s3.model.LifecycleRule;
import software.amazon.awssdk.services.s3.model.LifecycleRuleFilter;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Stores generated reports in an S3-compatible object store. Reports are staged on local disk by the batch
 * writer, uploaded here and the staged copy is removed. Expired objects are removed by a bucket lifecycle
 * rule (prefix {@value #PREFIX}) instead of an application job, so it also works with several instances.
 */
@Slf4j
public class S3ReportFileStorageAdapter implements ReportFileStoragePort {

    static final String PREFIX = "report-executions/";

    private final S3Client s3;
    private final S3StorageProperties properties;
    private volatile boolean bucketReady;

    public S3ReportFileStorageAdapter(S3Client s3, S3StorageProperties properties) {
        this.s3 = s3;
        this.properties = properties;
    }

    @Override
    public void store(String objectName, Path stagedFile, String contentType) {
        requireSafeKey(objectName);
        ensureBucket();
        PutObjectRequest.Builder request = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectName)
                .contentType(contentType);
        if (properties.serverSideEncryption()) {
            request.serverSideEncryption(ServerSideEncryption.AES256);
        }
        try {
            s3.putObject(request.build(), RequestBody.fromFile(stagedFile));
        } catch (RuntimeException ex) {
            throw failure("Cannot upload report file to object storage", ex);
        }
        try {
            Files.deleteIfExists(stagedFile);
        } catch (IOException ex) {
            log.warn("Uploaded report but could not delete staged file. file={}, message={}", stagedFile, ex.getMessage());
        }
        log.info("Report file uploaded. bucket={}, objectName={}", properties.bucket(), objectName);
    }

    @Override
    public Resource readContent(ReportFile reportFile) {
        String key = reportFile.getObjectName();
        if (key == null || key.isBlank()) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(), "Report file object name is missing");
        }
        requireSafeKey(key);
        try {
            String bucket = reportFile.getBucketName() != null ? reportFile.getBucketName() : properties.bucket();
            return new InputStreamResource(s3.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build()));
        } catch (NoSuchKeyException | NoSuchBucketException ex) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(), "Report file no longer exists in storage");
        } catch (RuntimeException ex) {
            throw failure("Cannot read report file from object storage", ex);
        }
    }

    @Override
    public String providerName() {
        return "S3";
    }

    @Override
    public String bucketName() {
        return properties.bucket();
    }

    /** Creates the bucket and its expiry rule on first use (idempotent), so startup never depends on S3. */
    private void ensureBucket() {
        if (bucketReady) {
            return;
        }
        synchronized (this) {
            if (bucketReady) {
                return;
            }
            try {
                try {
                    s3.headBucket(b -> b.bucket(properties.bucket()));
                } catch (NoSuchBucketException ex) {
                    if (!properties.createBucketIfMissing()) {
                        throw ex;
                    }
                    s3.createBucket(b -> b.bucket(properties.bucket()));
                    log.info("Created storage bucket {}", properties.bucket());
                }
                applyLifecycle();
                bucketReady = true;
            } catch (RuntimeException ex) {
                throw failure("Object storage bucket is not available", ex);
            }
        }
    }

    private void applyLifecycle() {
        try {
            s3.putBucketLifecycleConfiguration(b -> b.bucket(properties.bucket())
                    .lifecycleConfiguration(BucketLifecycleConfiguration.builder().rules(LifecycleRule.builder()
                            .id("expire-report-files")
                            .status(ExpirationStatus.ENABLED)
                            .filter(LifecycleRuleFilter.builder().prefix(PREFIX).build())
                            .expiration(LifecycleExpiration.builder().days(properties.retentionDays()).build())
                            .build()).build()));
        } catch (S3Exception ex) {
            // Some S3-compatible stores do not implement lifecycle; reports then need external cleanup.
            log.warn("Could not apply bucket lifecycle rule; expired reports will not be removed automatically. "
                    + "status={}, message={}", ex.statusCode(), ex.getMessage());
        }
    }

    private void requireSafeKey(String key) {
        if (key == null || key.startsWith("/") || key.contains("..")) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(), "Invalid report object name");
        }
    }

    private ReportingDomainException failure(String message, RuntimeException cause) {
        log.error("{}. bucket={}", message, properties.bucket(), cause);
        return new ReportingDomainException(ErrorCode.REPORT_EXECUTION_FAILED.getCode(), message);
    }
}
