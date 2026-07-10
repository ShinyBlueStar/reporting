package com.sample.system.reporting.service.application.command.common;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindByIdCommand {
    @NotNull
    private Long id;
}
