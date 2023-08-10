package ir.vcx.api.filter;

import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@Component
public class AfterMatchingFilter implements Filter {

    @Value("${server.version.name}")
    private String SERVER_VERSION_NAME;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        String timestamp = String.valueOf(DateUtil.getNowDate().getTime());
        servletRequest.setAttribute("startDate", timestamp);
        servletRequest.setAttribute("referenceId", generateReferenceId());

        RequestFacade facade = (RequestFacade) servletRequest;
        String method = facade.getMethod();
        String requestURI = facade.getRequestURI();
        servletRequest.setAttribute("userUri", method + " " + requestURI);

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (RequestRejectedException e) {

            log.error(requestURI + " " + e.getLocalizedMessage());
            servletRequest.setAttribute("exceptionStatus", VCXExceptionStatus.REQUEST_REJECTED);
            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    public String generateReferenceId() {
        UUID prefix = UUID.randomUUID();
        return prefix.toString().concat("-").concat(SERVER_VERSION_NAME);
    }
}
