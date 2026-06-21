package com.sample.system.platform.commons.contracts.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ساختار استاندارد پاسخ سرویس‌ها
 * <p>
 * این DTO برای تمام پاسخ‌های موفق و ناموفق سیستم استفاده می‌شود
 * و بر اساس سند استانداردسازی تبادل داده بین بک‌اند و فرانت‌اند طراحی شده است.
 * </p>
 * 
 * @param <T> نوع داده‌ای که در فیلد data قرار می‌گیرد
 * @author Banking System Team
 * @version 1.0.0
 * @since 2025-10-14
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"success", "data", "trackingId", "doTimeStamp", "successDetail", "errorDetail", "warningDetail", 
                    "infoDetail", "confirmationDetail", "alertDetail", "notificationDetail", "debugDetail", 
                    "progressDetail", "criticalDetail"})
public class StandardResponse<T> {
    
    /**
     * وضعیت موفقیت یا عدم موفقیت درخواست
     */
    private Boolean success;
    
    /**
     * داده اصلی پاسخ
     */
    private T data;
    
    /**
     * شناسه ردیابی برای پیگیری درخواست
     */
    private String trackingId;
    
    /**
     * زمان پردازش درخواست (timestamp میلی‌ثانیه)
     */
    private Long doTimeStamp;
    
    /**
     * جزئیات پیام موفقیت
     */
    private MessageDetail successDetail;
    
    /**
     * جزئیات خطا
     */
    private MessageDetail errorDetail;
    
    /**
     * جزئیات هشدار
     */
    private MessageDetail warningDetail;
    
    /**
     * جزئیات اطلاع‌رسانی
     */
    private MessageDetail infoDetail;
    
    /**
     * جزئیات تأیید
     */
    private MessageDetail confirmationDetail;
    
    /**
     * جزئیات هشدار فوری
     */
    private MessageDetail alertDetail;
    
    /**
     * جزئیات اعلان
     */
    private MessageDetail notificationDetail;
    
    /**
     * جزئیات اشکال‌زدایی
     */
    private MessageDetail debugDetail;
    
    /**
     * جزئیات پیشرفت
     */
    private MessageDetail progressDetail;
    
    /**
     * جزئیات بحرانی
     */
    private MessageDetail criticalDetail;
    
    /**
     * ایجاد پاسخ موفق با داده
     */
    public static <T> StandardResponse<T> success(T data, String trackingId) {
        return StandardResponse.<T>builder()
                .success(true)
                .data(data)
                .trackingId(trackingId)
                .doTimeStamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * ایجاد پاسخ موفق با داده و پیام موفقیت
     */
    public static <T> StandardResponse<T> success(T data, String trackingId, String message, Integer code) {
        return StandardResponse.<T>builder()
                .success(true)
                .data(data)
                .trackingId(trackingId)
                .doTimeStamp(System.currentTimeMillis())
                .successDetail(MessageDetail.builder()
                        .message(message)
                        .code(code)
                        .build())
                .build();
    }
    
    /**
     * ایجاد پاسخ خطا
     */
    public static <T> StandardResponse<T> error(String message, Integer code, String trackingId) {
        return StandardResponse.<T>builder()
                .success(false)
                .data(null)
                .trackingId(trackingId)
                .doTimeStamp(System.currentTimeMillis())
                .errorDetail(MessageDetail.builder()
                        .message(message)
                        .code(code)
                        .build())
                .build();
    }
    
    /**
     * ایجاد پاسخ خطا با action
     */
    public static <T> StandardResponse<T> error(String message, Integer code, String trackingId, String action) {
        return StandardResponse.<T>builder()
                .success(false)
                .data(null)
                .trackingId(trackingId)
                .doTimeStamp(System.currentTimeMillis())
                .errorDetail(MessageDetail.builder()
                        .message(message)
                        .code(code)
                        .action(action)
                        .build())
                .build();
    }
}

