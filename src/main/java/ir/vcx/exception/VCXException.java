package ir.vcx.exception;

import lombok.Getter;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Getter
public class VCXException extends Exception {

    private VCXExceptionStatus status;

    private int code;
    private String reasonPhrase;
    private String message;



    public VCXException(VCXExceptionStatus status) {
        this.status = status;
    }

    public VCXException(VCXExceptionStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public VCXException(int code, String reasonPhrase, String message) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
        this.message = message;
    }
}
