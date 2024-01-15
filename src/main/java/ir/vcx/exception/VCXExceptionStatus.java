package ir.vcx.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */
@Getter
@AllArgsConstructor
public enum VCXExceptionStatus {
    INVALID_REQUEST(400, "Invalid request", "درخواست معتبر نمی‌باشد"),
    POSTER_HASH_EXIST(400, "Poster hash exist", "شناسه پوستر تکراری می‌باشد"),
    REQUEST_REJECTED(400, "Request rejected", "درخواست شامل کاراکتر های مخرب است"),
    SSO_INVALID_REQUEST(400, "SSO Invalid Code Request", "کد تایید اشتباه می باشد و یا زمان استفاده از آن به اتمام رسیده است"),
    INVALID_REDIRECT_URI(400, "Invalid Redirect Uri", "درخواست معتبر نمی‌باشد"),
    INVALID_SSO_TYPE(400, "Invalid SSO Type", "درخواست معتبر نمی‌باشد"),
    INVALID_NAME_VALUE(400, "Invalid name value", "نام وارد شده معتبر نمی‌باشد."),
    INVALID_NAME_VALUE_LENGTH(400, "Invalid name value length", "نام وارد شده حدااقل می‌بایست دارای ۳ حرف باشد"),
    INVALID_PAGINATION_ORDER(400, "Invalid pagination order", "نوع مرتب کننده لیست قابل قبول نمی‌باشد"),

    UNAUTHORIZED(401, "Unauthorized", "درخواست دسترسی معتبر نیست"),
    INVALID_AUTHORIZATION_TYPE(401, "Invalid authorization type", "احراز هویت باید از نوع Bearer باشد"),

    FORBIDDEN(403, "Forbidden", "درخواست دسترسی رد شد"),
    NOT_FOUND(404, "Request not found", "درخواست مورد نظر پیدا نشد"),
    FOLDER_NOT_FOUND(404, "Folder not found", "پوشه مورد نظر پیدا نشد"),
    CONTENT_NOT_FOUND(404, "Content not found", "محتوایی با این شناسه یافت نشد"),
    PARENT_FOLDER_NOT_FOUND(404, "Parent folder not found", "پوشه مقصد پیدا نشد"),

    PLAN_LIMIT_CONFLICT(409, "Plan limit conflict", "طرح اشتراک فعالی با این محدودیت وجود دارد"),

    SSO_CONNECTION_ERROR(500, "SSO Server Connection Error", "ارتباط با سرور SSO با مشکل مواجه شده است"),

    PROCESS_REQUEST_ERROR(500, "Process request error", "این امکان در حال حاضر وجود ندارد"),
    PODSPACE_REQUEST_CALL_ERROR(500, "PodSpace Request Call Error", "در ارتباط با پاداسپیس مشکلی به وجود آمده است."),
    UNKNOWN_ERROR(500, "Unknown error", "خطایی رخ داده است"),

    BAD_GATEWAY(502, "Bad Gateway", "عدم توانایی برقراری ارتباط"),

    ;

    private final int code;
    private final String reasonPhrase;
    private final String message;
}
