package ir.vcx.util;


import ir.vcx.api.filter.RequestResponseLoggerAdvice;
import ir.vcx.api.model.RestResponse;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/**
 * Created by a.rokni on 2020/05/14 @Podspace.
 */

@Component
public class ResponseWriterUtil {

    private final RequestResponseLoggerAdvice responseLoggerAdvice;
    @Value("${server.version.name}")
    private String SPACE_SEVER_NAME;
    @Value("${server.version.code}")
    private String SPACE_SEVER_VERSION;

    @Autowired
    public ResponseWriterUtil(RequestResponseLoggerAdvice responseLoggerAdvice) {
        this.responseLoggerAdvice = responseLoggerAdvice;
    }

    public void sendProcessErrorResponse(HttpServletRequest request, HttpServletResponse response, VCXException e) throws IOException {

        response.setHeader("Content-Language", "fa");

        VCXExceptionStatus status = e.getStatus();
        RestResponse<?> restResponse = new RestResponse<>(
                e.getStatus().getCode(),
                e.getStatus().getReasonPhrase(),
                (String) request.getAttribute("userUri"),
                e.getMessage(),
                (String) request.getAttribute("referenceId"),
                new Date(Long.parseLong((String) Objects.requireNonNull(request.getAttribute("startDate"))))
        );

        sendResponse(response, request, status, restResponse);
    }

    public void sendInvalidAuthorizationType(HttpServletRequest request, HttpServletResponse response, VCXException e) throws IOException {

        response.setHeader("Content-Language", "fa");

        VCXExceptionStatus status = e.getStatus();
        RestResponse<?> restResponse = new RestResponse<>(
                401,
                e.getStatus().getReasonPhrase(),
                (String) request.getAttribute("userUri"),
                e.getMessage(),
                (String) request.getAttribute("referenceId"),
                new Date(Long.parseLong((String) Objects.requireNonNull(request.getAttribute("startDate"))))
        );

        sendResponse(response, request, status, restResponse);
    }

    private void sendResponse(HttpServletResponse response, HttpServletRequest request, VCXExceptionStatus status, RestResponse<?> restResponse) throws IOException {
        response.setStatus(status.getCode());
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String server = "Space Server " + "v" + SPACE_SEVER_VERSION + " #" + SPACE_SEVER_NAME;
        response.setHeader("Server", server);
        response.setHeader("X-Powered-By", "FanapSoft/PodPlatform (" + server + ")");

        String referenceId = (String) request.getAttribute("referenceId");
        response.setHeader("Reference-Id", referenceId);

        responseLoggerAdvice.beforeBodyWrite(restResponse, null, null, null,
                new ServletServerHttpRequest(request), new ServletServerHttpResponse(response));

        response.getWriter().write(JsonUtil.getStringJson(restResponse));
    }
}
