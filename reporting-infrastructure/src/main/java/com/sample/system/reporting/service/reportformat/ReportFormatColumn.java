package com.sample.system.reporting.service.reportformat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportFormatColumn {

    private String field;
    private String title;
    private Integer width;

    public String titleOrField() {
        return title == null || title.isBlank() ? field : title;
    }
}
