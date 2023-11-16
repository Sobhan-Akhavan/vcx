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
    INVALID_REDIRECT_URI(400, "Invalid Redirect Uri", "درخواست معتبر نمی‌باشد."),
    INVALID_SSO_TYPE(400, "Invalid SSO Type", "درخواست معتبر نمی‌باشد."),


    UNAUTHORIZED(401, "Unauthorized", "درخواست دسترسی معتبر نیست."),
    INVALID_AUTHORIZATION_TYPE(401, "Invalid authorization type", "احراز هویت باید از نوع Bearer باشد"),

    FORBIDDEN(403, "Forbidden", "درخواست دسترسی رد شد."),
    NOT_FOUND(404, "Request Not found", "درخواست مورد نظر پیدا نشد."),

    SSO_CONNECTION_ERROR(500, "SSO Server Connection Error", "ارتباط با سرور SSO با مشکل مواجه شده است."),

    PROCESS_REQUEST_ERROR(500, "Process request error", "این امکان در حال حاضر وجود ندارد."),
    UNKNOWN_ERROR(500, "Unknown error", "خطایی رخ داده است!"),

    BAD_GATEWAY(502, "Bad Gateway", "عدم توانایی برقراری ارتباط."),

    ;

    private final int code;
    private final String reasonPhrase;
    private final String message;
}
