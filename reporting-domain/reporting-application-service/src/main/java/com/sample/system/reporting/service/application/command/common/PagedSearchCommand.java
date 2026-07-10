package com.sample.system.reporting.service.application.command.common;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagedSearchCommand {

    @PositiveOrZero
    private Integer page = 0;

    @Min(1)
    private Integer size = 10;
}
