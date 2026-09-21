package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class S3ReportFileStorageAdapterTest {

    @TempDir
    Path tmp;

    private final S3Client s3 = mock(S3Client.class, RETURNS_DEEP_STUBS);
    private final S3StorageProperties props =
            new S3StorageProperties("http://s3:8333", null, "k", "s", "bucket", true, true, true, 3);
    private final S3ReportFileStorageAdapter adapter = new S3ReportFileStorageAdapter(s3, props);

    @Test
    void uploadsWithEncryptionContentTypeAndRemovesStagedFile() throws Exception {
        Path staged = Files.writeString(tmp.resolve("r.csv"), "a,b");

        adapter.store("report-executions/1/r.csv", staged, "text/csv");

        var captor = org.mockito.ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3).putObject(captor.capture(), any(RequestBody.class));
        assertEquals("bucket", captor.getValue().bucket());
        assertEquals("report-executions/1/r.csv", captor.getValue().key());
        assertEquals("text/csv", captor.getValue().contentType());
        assertEquals("AES256", captor.getValue().serverSideEncryptionAsString());
        assertFalse(Files.exists(staged));
        assertEquals("S3", adapter.providerName());
        assertEquals("bucket", adapter.bucketName());
    }

    @Test
    void createsMissingBucketOnce() throws Exception {
        when(s3.headBucket(any(java.util.function.Consumer.class))).thenThrow(NoSuchBucketException.builder().build());
        Path staged = Files.writeString(tmp.resolve("r.csv"), "x");

        adapter.store("report-executions/1/r.csv", staged, "text/csv");
        adapter.store("report-executions/1/r.csv", Files.writeString(tmp.resolve("r2.csv"), "x"), "text/csv");

        verify(s3, times(1)).createBucket(any(java.util.function.Consumer.class));
    }

    @Test
    void uploadFailureIsReportedAndStagedFileKept() throws Exception {
        Path staged = Files.writeString(tmp.resolve("r.csv"), "x");
        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(new RuntimeException("down"));

        assertThrows(ReportingDomainException.class,
                () -> adapter.store("report-executions/1/r.csv", staged, "text/csv"));
        assertTrue(Files.exists(staged));
    }

    @Test
    void rejectsUnsafeKeys() {
        ReportFile file = new ReportFile();
        file.setObjectName("../etc/passwd");
        assertThrows(ReportingDomainException.class, () -> adapter.readContent(file));
        assertThrows(ReportingDomainException.class, () -> adapter.store("/abs", tmp, "x"));
    }
}
