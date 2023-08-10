package ir.vcx.api.filter;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static org.springframework.http.HttpStatus.resolve;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Slf4j
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({VCXException.class})
    public ResponseEntity<?> ArchiveExceptionHandler(HttpServletRequest request, VCXException exception) {

        VCXExceptionStatus status = exception.getStatus();
        return ResponseEntity
                .status(status.getCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestResponse<>(
                        status.getCode(),
                        status.getReasonPhrase(),
                        (String) request.getAttribute("userUri"),
                        (String) request.getAttribute("referenceId"),
                        exception.getMessage(),
                        new Date(Long.parseLong((String) request.getAttribute("startDate")))
                ));
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<?> handleConversionFailedException(RuntimeException ex, HttpServletRequest request) {
        return ArchiveExceptionHandler(request, new VCXException(VCXExceptionStatus.INVALID_REQUEST));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(RuntimeException ex, HttpServletRequest request) {
        return ArchiveExceptionHandler(request, new VCXException(VCXExceptionStatus.INVALID_REQUEST));
    }

    @ExceptionHandler({MultipartException.class})
    public ResponseEntity<?> multipartExceptionHandler(HttpServletRequest request, MultipartException exception) {

        return ResponseEntity
                .status(400)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestResponse(
                        400,
                        "Error Parsing File",
                        (String) request.getAttribute("userUri"),
                        exception.getMessage(),
                        (String) request.getAttribute("referenceId"),
                        new Date(Long.parseLong((String) request.getAttribute("startDate")))
                ));

    }


    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<?> runtimeExceptionHandler(HttpServletRequest request, RuntimeException exception) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String userUri = (String) request.getAttribute("userUri");
        String referenceId = (String) request.getAttribute("referenceId");

        log.error("5xx exception with uri: " + userUri + " ,ref: " + referenceId, exception);

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestResponse<>(
                        status.value(),
                        status.getReasonPhrase(),
                        userUri,
                        "مشکلی در پردازش به وجود آمده است.",
                        referenceId,
                        new Date(Long.parseLong((String) request.getAttribute("startDate")))
                ));
    }

    @RestController
    public static class ErrorHandlerController implements ErrorController {
        @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(hidden = true)
        public ResponseEntity<?> err(HttpServletRequest request, HttpServletResponse response) {

            int code;
            String message, reasonPhrase;
            VCXExceptionStatus status;

            if (request.getAttribute("exceptionStatus") == null) {
                HttpStatus httpStatus = resolve(response.getStatus());
                code = httpStatus.value();
                message = httpStatus.getReasonPhrase();
                reasonPhrase = httpStatus.getReasonPhrase();
                status = null;
            } else {
                status = (VCXExceptionStatus) request.getAttribute("exceptionStatus");
                code = status.getCode();
                message = status.getMessage();
                reasonPhrase = status.getReasonPhrase();
            }

            return ResponseEntity.status(code)
                    .body(new RestResponse(
                                    code,
                                    reasonPhrase,
                                    message
                            )
                    );
        }
    }
}