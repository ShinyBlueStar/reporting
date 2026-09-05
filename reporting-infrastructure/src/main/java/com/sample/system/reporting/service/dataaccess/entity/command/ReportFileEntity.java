package com.sample.system.reporting.service.dataaccess.entity.command;

import com.sample.system.reporting.service.dataaccess.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import java.io.Serializable;

@Audited
@AuditOverride(forClass = Auditable.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "report_file")
public class ReportFileEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_file_seq")
    @SequenceGenerator(name = "report_file_seq", sequenceName = "REPORT_FILE_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "file_name", length = 500, nullable = false)
    private String fileName;

    @Column(name = "original_file_name", length = 500)
    private String originalFileName;

    @Column(name = "file_extension", length = 20)
    private String fileExtension;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "bucket_name", length = 200)
    private String bucketName;

    @Column(name = "object_name", length = 1000)
    private String objectName;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "checksum", length = 128)
    private String checksum;

    @Column(name = "storage_provider")
    private String storageProvider; // MINIO S3 AZURE LOCAL
}
