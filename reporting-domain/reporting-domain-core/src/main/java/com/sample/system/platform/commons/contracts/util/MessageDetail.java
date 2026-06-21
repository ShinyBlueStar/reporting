package com.sample.system.platform.commons.contracts.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * جزئیات پیام‌های مختلف (موفقیت، خطا، هشدار و ...)
 * 
 * @author Banking System Team
 * @version 1.0.0
 * @since 2025-10-14
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code", "message", "action"})
public class MessageDetail {
    
    /**
     * کد پیام (عدد)
     * مثال کدها:
     * - 1000: Success
     * - 2000: Error
     * - 3000: Warning
     * - 4000: Info
     * - 5000: Confirmation
     * - 6000: Alert
     * - 7000: Notification
     * - 8000: Debug
     * - 9000: Progress
     * - 10000: Critical
     */
    private Integer code;
    
    /**
     * پیام (فارسی)
     */
    private String message;
    
    /**
     * اقدام پیشنهادی (اختیاری)
     */
    private String action;
}

