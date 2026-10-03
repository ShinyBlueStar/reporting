package com.sample.system.reporting.service.dataaccess.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
class ReportFileCleanupJob {

    private static final String REPORT_EXECUTIONS_DIRECTORY = "report-executions";

    private final Path reportExecutionsPath;
    private final long retentionDays;
    private final ZoneId cleanupZone;

    public ReportFileCleanupJob(
            @Value("${reporting.storage.local-path:report-output}") String localStoragePath,
            @Value("${reporting.file-cleanup.retention-days:1}") long retentionDays,
            @Value("${reporting.file-cleanup.zone:Asia/Tehran}") String cleanupZone) {
        if (retentionDays <= 0) {
            throw new IllegalArgumentException("Report file retention days must be greater than zero");
        }
        Path storagePath = Path.of(localStoragePath).toAbsolutePath().normalize();
        this.reportExecutionsPath = storagePath.resolve(REPORT_EXECUTIONS_DIRECTORY).normalize();
        this.retentionDays = retentionDays;
        this.cleanupZone = ZoneId.of(cleanupZone);
    }

    @Scheduled(
            cron = "${reporting.file-cleanup.cron:0 0 0 * * *}",
            zone = "${reporting.file-cleanup.zone:Asia/Tehran}")
    public void cleanupExpiredReportFiles() {
        LocalDate lastExpiredDate = LocalDate.now(cleanupZone).minusDays(retentionDays);
        int deletedFiles = deleteFilesOnOrBefore(lastExpiredDate);
        log.info("Expired report file cleanup completed. lastExpiredDate={}, zone={}, deletedFiles={}, root={}",
                lastExpiredDate,
                cleanupZone,
                deletedFiles,
                reportExecutionsPath);
    }

    int deleteFilesOnOrBefore(LocalDate lastExpiredDate) {
        if (!Files.isDirectory(reportExecutionsPath)) {
            log.debug("Report execution directory does not exist; cleanup skipped. root={}", reportExecutionsPath);
            return 0;
        }

        int deletedFiles = 0;
        for (Path file : regularFilesUnderReportExecutions()) {
            try {
                Instant lastModified = Files.getLastModifiedTime(file).toInstant();
                LocalDate reportDate = lastModified.atZone(cleanupZone).toLocalDate();
                if (!reportDate.isAfter(lastExpiredDate) && Files.deleteIfExists(file)) {
                    deletedFiles++;
                }
            } catch (IOException ex) {
                log.warn("Cannot delete expired report file. file={}, message={}",
                        file,
                        ex.getMessage());
            }
        }

        removeEmptyExecutionDirectories();
        return deletedFiles;
    }

    private List<Path> regularFilesUnderReportExecutions() {
        try (Stream<Path> paths = Files.walk(reportExecutionsPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .toList();
        } catch (IOException ex) {
            log.error("Cannot scan report execution directory. root={}, message={}",
                    reportExecutionsPath,
                    ex.getMessage());
            return List.of();
        }
    }

    private void removeEmptyExecutionDirectories() {
        try (Stream<Path> paths = Files.walk(reportExecutionsPath)) {
            List<Path> directories = paths
                    .filter(Files::isDirectory)
                    .filter(path -> !path.equals(reportExecutionsPath))
                    .sorted(Comparator.reverseOrder())
                    .toList();
            for (Path directory : directories) {
                try {
                    Files.deleteIfExists(directory);
                } catch (DirectoryNotEmptyException ignored) {
                    // The directory still contains a retained report.
                } catch (IOException ex) {
                    log.warn("Cannot remove empty report directory. directory={}, message={}",
                            directory,
                            ex.getMessage());
                }
            }
        } catch (IOException ex) {
            log.warn("Cannot scan report directories for cleanup. root={}, message={}",
                    reportExecutionsPath,
                    ex.getMessage());
        }
    }
}
