package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.ReportFileId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportFile extends BaseEntity<ReportFileId> {

    private String fileName;
    private String originalFileName;
    private String fileExtension;
    private Long fileSize;
    private String bucketName;
    private String objectName;
    private String contentType;
    private String checksum;
    private String storageProvider;
}
