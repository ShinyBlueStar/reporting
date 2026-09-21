package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalReportFileStorageAdapterTest {

    @TempDir
    Path tmp;

    private ReportFile fileNamed(String objectName) {
        ReportFile file = new ReportFile();
        file.setObjectName(objectName);
        return file;
    }

    @Test
    void readsFileInsideStorageDirectory() throws Exception {
        Path storage = Files.createDirectory(tmp.resolve("storage"));
        Files.writeString(storage.resolve("a.csv"), "x");
        Resource resource = new LocalReportFileStorageAdapter(storage.toString()).readContent(fileNamed("a.csv"));
        assertEquals(1, resource.contentLength());
    }

    @Test
    void rejectsPathTraversalAndMissingNames() throws Exception {
        Path storage = Files.createDirectory(tmp.resolve("storage"));
        Files.writeString(tmp.resolve("secret.txt"), "secret");
        LocalReportFileStorageAdapter adapter = new LocalReportFileStorageAdapter(storage.toString());
        assertThrows(ReportingDomainException.class, () -> adapter.readContent(fileNamed("../secret.txt")));
        assertThrows(ReportingDomainException.class, () -> adapter.readContent(fileNamed(" ")));
        assertThrows(ReportingDomainException.class, () -> adapter.readContent(fileNamed("missing.csv")));
    }

    @Test
    void storeKeepsTheFileInPlaceAndReportsLocalProvider() throws Exception {
        Path storage = Files.createDirectory(tmp.resolve("out"));
        Path file = Files.writeString(storage.resolve("a.csv"), "x");
        LocalReportFileStorageAdapter adapter = new LocalReportFileStorageAdapter(storage.toString());

        adapter.store("a.csv", file, "text/csv");

        assertTrue(Files.exists(file));
        assertEquals("LOCAL", adapter.providerName());
        assertEquals("out", adapter.bucketName());
    }
}
