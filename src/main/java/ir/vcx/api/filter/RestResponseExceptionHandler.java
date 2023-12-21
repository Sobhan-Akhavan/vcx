package ir.vcx.api.filter;

import ir.vcx.api.model.RestResponse;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Slf4j
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(VCXException.class)
    public ResponseEntity<?> handleVCXException(HttpServletRequest request, VCXException exception) {

        int code;
        String reasonPhrase, message;

        VCXExceptionStatus status = exception.getStatus();
        if (status != null && exception.getMessage() != null) {
            code = status.getCode();
            reasonPhrase = status.getReasonPhrase();
            message = exception.getMessage();
        } else if (status != null) {
            code = status.getCode();
            reasonPhrase = status.getReasonPhrase();
            message = status.getMessage();
        } else {
            code = exception.getCode();
            reasonPhrase = exception.getReasonPhrase();
            message = exception.getMessage();
        }

        return ResponseEntity
                .status(code)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestResponse<>(
                        code,
                        reasonPhrase,
                        message,
                        String.valueOf(request.getAttribute("userUri")),
                        new Date(Long.parseLong(String.valueOf(request.getAttribute("startDate")))),
                        String.valueOf(request.getAttribute("referenceId"))
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(HttpServletRequest request, RuntimeException exception) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "مشکلی در پردازش به ‌وجود آمده است";
        String userUri = String.valueOf(request.getAttribute("userUri"));
        String referenceId = String.valueOf(request.getAttribute("referenceId"));

        log.error("500 exception with referenceId: {} and uri: {}", referenceId, userUri, exception);

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestResponse<>(
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        userUri,
                        new Date(Long.parseLong(String.valueOf(request.getAttribute("startDate")))),
                        referenceId
                ));
    }

    @ExceptionHandler({ConversionFailedException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<?> handleMethodCastFailedException(HttpServletRequest request, RuntimeException exception) {
        return handleCustomException(request, exception);
    }

    private ResponseEntity<?> handleCustomException(HttpServletRequest request, RuntimeException exception) {

        int code = VCXExceptionStatus.INVALID_REQUEST.getCode();
        return ResponseEntity
                .status(code)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestResponse<>(
                        code,
                        VCXExceptionStatus.INVALID_REQUEST.getReasonPhrase(),
                        exception.getMessage(),
                        String.valueOf(request.getAttribute("userUri")),
                        new Date(Long.parseLong(String.valueOf(request.getAttribute("startDate")))),
                        String.valueOf(request.getAttribute("referenceId"))
                ));
    }

    @RestController
    public static class ErrorHandlerController implements ErrorController {
        @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> err(HttpServletRequest request, HttpServletResponse response) {

            int code;
            String message, reasonPhrase;
            VCXExceptionStatus status;

            if (request.getAttribute("exceptionStatus") == null) {
                HttpStatus httpStatus = HttpStatus.resolve(response.getStatus());
                code = httpStatus.value();
                message = httpStatus.getReasonPhrase();
                reasonPhrase = httpStatus.getReasonPhrase();
            } else {
                status = (VCXExceptionStatus) request.getAttribute("exceptionStatus");
                code = status.getCode();
                message = status.getMessage();
                reasonPhrase = status.getReasonPhrase();
            }

            return ResponseEntity.status(code)
                    .body(new RestResponse<>(
                            code,
                            reasonPhrase,
                            message
                    ));
        }
    }
}