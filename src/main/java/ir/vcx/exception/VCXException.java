package ir.vcx.exception;

import lombok.Getter;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Getter
public class VCXException extends Exception {

    protected VCXExceptionStatus status;
    protected String message;

    public VCXException(VCXExceptionStatus status) {
        this.status = status;
    }

    public VCXException(VCXExceptionStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
