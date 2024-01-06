package ir.vcx.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Getter
@Setter
public class RestResponse<T> implements VCXApiModel {

    private Integer status;
    private String error;
    private String message;
    private String path;
    private Date timestamp;
    private T result;
    private String reference;

    @Builder(builderMethodName = "Builder")
    public RestResponse(HttpStatus status, String path, String message, T result, String reference, Date timestamp) {
        this.status = status.value();
        this.error = status.isError() ? status.getReasonPhrase() : null;
        this.path = path;
        this.message = message;
        this.result = result;
        this.reference = reference;
        this.timestamp = timestamp;
    }

    public RestResponse(int status, String error, String message, String path, Date timestamp, String reference) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.reference = reference;
    }

    public RestResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

}