package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.reportformat.ReportFormatResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
public abstract class StreamingReportResource implements AutoCloseable {

    private final ReportOutputFile outputFile;
    private ReportFormatResult result;

    protected StreamingReportResource(ReportOutputFile outputFile) {
        this.outputFile = outputFile;
    }

    public final ReportOutputFile getOutputFile() {
        return outputFile;
    }

    public final ReportFormatResult closeAndGetResult() {
        if (result != null) {
            return result;
        }
        try {
            finishContent();
            moveToFinalFile();
            result = new ReportFormatResult(
                    outputFile.getFinalFile(),
                    outputFile.getFileName(),
                    outputFile.getFormat().extension(),
                    outputFile.getFormat().contentType(),
                    Files.size(outputFile.getFinalFile()),
                    outputFile.getObjectName());
            return result;
        } catch (IOException ex) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    "Cannot finalize streamed report file: " + ex.getMessage());
        }
    }

    @Override
    public final void close() {
        closeAndGetResult();
    }

    protected abstract void finishContent() throws IOException;

    protected void moveToFinalFile() throws IOException {
        Path tempFile = outputFile.getTempFile();
        Path finalFile = outputFile.getFinalFile();
        // XLSX writes directly to finalFile; factory sets tempFile == finalFile for that format.
        if (tempFile.equals(finalFile) || !Files.exists(tempFile)) {
            return;
        }
        log.info("[BatchWriter] Moving streamed report file. fileName={}, from={}, to={}",
                outputFile.getFileName(),
                tempFile,
                finalFile);
        Files.createDirectories(finalFile.getParent());
        try {
            Files.move(tempFile, finalFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ex) {
            Files.copy(tempFile, finalFile, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(tempFile);
        }
    }
}
