package ir.vcx.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */
@Getter
@AllArgsConstructor
public enum VCXExceptionStatus {
    INVALID_REQUEST(400, "Invalid Request", "درخواست معتبر نمی‌باشد."),
    REQUEST_REJECTED(400, "Request Rejected", "درخواست شامل کاراکتر های مخرب است."),
    SSO_INVALID_REQUEST(400, "SSO Invalid Code Request", "کد تایید اشتباه می باشد و یا زمان استفاده از آن به اتمام رسیده است."),

    NOT_FOUND(404, "Request Not found", "درخواست مورد نظر پیدا نشد."),

    SSO_CONNECTION_ERROR(500, "SSO Server Connection Error", "ارتباط با سرور SSO با مشکل مواجه شده است."),

    PROCESS_REQUEST_ERROR(500, "Process Request Error", "این امکان در حال حاضر وجود ندارد."),
    UNKNOWN_ERROR(500, "Unknown Error", "خطایی رخ داده است!");

    private final int code;
    private final String reasonPhrase;
    private final String message;
}
