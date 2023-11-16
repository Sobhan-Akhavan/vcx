package ir.vcx.api.filter;

import ir.vcx.domain.model.RequestResponse;
import ir.vcx.domain.model.UserCredential;
import ir.vcx.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a.rokni on 2020/05/14 @Podspace.
 */
@Slf4j
@ControllerAdvice
public class RequestResponseLoggerAdvice implements ResponseBodyAdvice<Object> {


    private final List<HandlerMapping> handlerMappings;
    @Value("${service.log-request-response.exception-list}")
    private List<String> EXCEPTION_LIST;
    @Value("${service.log-request-response.force-all:true}")
    private Boolean LOG_ALL;


    @Autowired
    public RequestResponseLoggerAdvice(List<HandlerMapping> handlerMappings) {
        this.handlerMappings = handlerMappings;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (LOG_ALL) {
            return true;
        } else {
            return returnType.getParameterType().getTypeName().equalsIgnoreCase(ResponseEntity.class.getTypeName());
        }
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest httpRequest, ServerHttpResponse httpResponse) {

        try {

            HttpServletRequest request = ((ServletServerHttpRequest) httpRequest).getServletRequest();
            HttpServletResponse response = ((ServletServerHttpResponse) httpResponse).getServletResponse();

            if (request.getAttribute("referenceId") == null) {
                return body;
            }

            String userUri = (String) request.getAttribute("userUri");
            if (StringUtils.isNotBlank(userUri) && EXCEPTION_LIST.contains(userUri)) {
                return body;
            }

            RequestResponse requestResponse = new RequestResponse();
            Map<String, String[]> parametersMap = new HashMap<>(request.getParameterMap());
            Map<String, String[]> ignoredParameters = new HashMap<>();

            Object handler = getHandler(request);
            if (handler instanceof HandlerMethod) {

                Method method = ((HandlerMethod) handler).getMethod();


                requestResponse.setIgnoredParameters(JsonUtil.getStringJson(ignoredParameters));

//                requestResponse.setEndpointName(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, method.getName()));
                requestResponse.setDeprecated(((HandlerMethod) handler).hasMethodAnnotation(Deprecated.class));
            }


            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            requestResponse.setMessageId((String) request.getAttribute("messageId"));

            if (request.getAttribute("ssoResponseTime") != null) {
                requestResponse.setSsoResponseTime(((Long) request.getAttribute("ssoResponseTime")));
            }

            if (request.getRequestURI().startsWith("/api/") || request.getRequestURI().startsWith("/app/")) {
                requestResponse.setApiVersion("v3");
                //      requestResponse.setResponse(bodyToLog);
            } else {
                requestResponse.setApiVersion("unknown");
                //      requestResponse.setErrorResponse(bodyToLog);
            }

            requestResponse.setMethod(request.getMethod());

            if (authentication != null) {

                requestResponse.setPrincipal((String) authentication.getPrincipal());

                if (authentication.getCredentials() instanceof UserCredential) {
                    UserCredential credentials = (UserCredential) authentication.getCredentials();

                    if (credentials.getUser() != null) {
                        requestResponse.setUsername(credentials.getUser().getUsername());
                        requestResponse.setUserId(credentials.getUser().getId());
                        requestResponse.setUserSsoId(credentials.getUser().getSsoId());
                    }

                    requestResponse.setToken(credentials.getToken());
                }
            }

            HashMap<String, String[]> requestHeadersMap = new HashMap<>();
            httpRequest.getHeaders().forEach((key, value) -> requestHeadersMap.put(key, value.toArray(new String[0])));
            requestResponse.setRequestHeaders(requestHeadersMap);

            HashMap<String, String[]> responseHeadersMap = new HashMap<>();
            httpResponse.getHeaders().forEach((key, value) -> responseHeadersMap.put(key, value.toArray(new String[0])));
            requestResponse.setResponseHeaders(responseHeadersMap);

            requestResponse.setUri(userUri);
            requestResponse.setParameters(parametersMap);
            requestResponse.setRemoteAddr(httpRequest.getRemoteAddress().getHostString());
            requestResponse.setRealRemoteAddr(request.getHeader("X-Forwarded-For"));
            requestResponse.setServerId(InetAddress.getLocalHost().getHostAddress());

            requestResponse.setStatus(response.getStatus());

            if (response.getStatus() >= 400) {
                requestResponse.setStackTrace((String) request.getAttribute("stackTrace"));
            }

            requestResponse.setRequestTimestamp(new Date(Long.parseLong((String) request.getAttribute("startDate"))));
            requestResponse.setResponseTimestamp(new Date(System.currentTimeMillis()));
            requestResponse.setTimestamp(new Date(System.currentTimeMillis()));

            requestResponse.setId((String) request.getAttribute("referenceId"));

            requestResponse.setUserAgent(request.getHeader("user-agent"));
            requestResponse.setAccept(request.getHeader("accept"));

            requestResponse.setTokenIssuerId(String.valueOf(request.getAttribute("token_issuer_business")));
            requestResponse.setTokenIssuerClient(String.valueOf(request.getAttribute("token_issuer_client")));

            //  to bypass request-response log without change in code change this package level to off
            //  logging.level.ir.pod.podspace.api.filter=off

            log.info(JsonUtil.getWithIso8601DateFormatJson(requestResponse));

        } catch (Throwable e) {
            log.warn("An exception occurred", e);
        }
        return body;
    }

    private Object getHandler(HttpServletRequest servletRequest) {
        Object handler = null;
        for (HandlerMapping handlerMapping : handlerMappings) {
            try {
                HandlerExecutionChain handlerExecutionChain =
                        handlerMapping.getHandler(servletRequest);
                if (handlerExecutionChain != null) {
                    if (handlerExecutionChain.getHandler() instanceof HandlerMethod) {
                        handler = handlerExecutionChain.getHandler();
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return handler;
    }
}
