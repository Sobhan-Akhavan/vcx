package ir.vcx.api.filter;

import ir.vcx.api.model.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by a.rokni on 2020/05/14 @Podspace.
 */

@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
@ControllerAdvice
public class HeaderDecoratorAdvice implements ResponseBodyAdvice<Object> {

    @Value("${server.version.name}")
    private String SERVER_VERSION_NAME;
    @Value("${server.version.code}")
    private String SEVER_VERSION_CODE;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getParameterType().getTypeName().equalsIgnoreCase(ResponseEntity.class.getTypeName());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest httpRequest, ServerHttpResponse httpResponse) {

        HttpHeaders headers = httpResponse.getHeaders();

        ((ServletServerHttpResponse) httpResponse).getServletResponse().setCharacterEncoding("UTF-8");

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) httpRequest).getServletRequest();
        String referenceId = (String) servletRequest.getAttribute("referenceId");

        if (body instanceof RestResponse) {

            String userUri = (String) servletRequest.getAttribute("userUri");
            String startDate = (String) servletRequest.getAttribute("startDate");

            headers.set("version", "v3");

            if (referenceId != null) {
                ((RestResponse) body).setReference(referenceId);
            }

            if (userUri != null) {
                ((RestResponse) body).setPath(userUri);
            }

            if (startDate != null) {
                ((RestResponse) body).setTimestamp(new Date(Long.parseLong(startDate)));
            }
        }

        String server = "VCX Server " + "v" + SEVER_VERSION_CODE + " #" + SERVER_VERSION_NAME;

        headers.set("Access-Control-Allow-Headers", "Origin, Content-type, Accept, Authorization");
        headers.set("Access-Control-Allow-Credentials", "true");
        headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        headers.set("Reference-Id", referenceId);

        headers.set("Server", server);
        headers.set("X-Powered-By", "VCX Platform (" + server + ")");

        return body;
    }


}
