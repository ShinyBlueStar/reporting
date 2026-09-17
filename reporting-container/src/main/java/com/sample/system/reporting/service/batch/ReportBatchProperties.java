package com.sample.system.reporting.service.batch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "reporting.batch")
public class ReportBatchProperties {

    private int chunkSize = 1000;
}
