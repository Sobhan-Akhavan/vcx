package ir.vcx.api.filter.security;

import ir.vcx.domain.model.UserCredential;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.KeyleadConfiguration;
import ir.vcx.util.ResponseWriterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Sobhan on 11/16/2023 - VCX
 */

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class MyBasicAuthenticationFilter extends BasicAuthenticationFilter {
    private static final String AUTHORIZATION = "Authorization";
    private static final String SECURITY_AUTHORIZATION_SCHEMA = "bearer ";

    private final KeyleadConfiguration keyleadConfiguration;
    private final ResponseWriterUtil responseWriterUtil;

    public MyBasicAuthenticationFilter(AuthenticationManager authenticationManager, KeyleadConfiguration keyleadConfiguration,
                                       ResponseWriterUtil responseWriterUtil) {
        super(authenticationManager);
        this.keyleadConfiguration = keyleadConfiguration;
        this.responseWriterUtil = responseWriterUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String authorizationToken = request.getHeader(AUTHORIZATION);

        try {
            checkToken(authorizationToken, request, response);
        } catch (VCXException e) {
            return;
        }

        chain.doFilter(request, response);
    }

    private void checkToken(String accessToken, HttpServletRequest request, HttpServletResponse response) throws IOException, VCXException {

        if (StringUtils.isBlank(accessToken)) {

            accessToken = request.getParameter(AUTHORIZATION.toLowerCase()).toLowerCase();

            if (StringUtils.isNotBlank(accessToken)) {
                accessToken = accessToken.startsWith(SECURITY_AUTHORIZATION_SCHEMA)
                        ? accessToken
                        : SECURITY_AUTHORIZATION_SCHEMA + accessToken;
            }
        }

        if (StringUtils.isNotBlank(accessToken)) {

            if (accessToken.toLowerCase().startsWith(SECURITY_AUTHORIZATION_SCHEMA)) {

                String token = accessToken.substring(SECURITY_AUTHORIZATION_SCHEMA.length());

                try {
                    handleBearerToken(token, request);
                } catch (VCXException e) {
                    responseWriterUtil.sendProcessErrorResponse(request, response, e);
                    throw e;
                }

            } else {
                responseWriterUtil.sendInvalidAuthorizationType(request, response, new VCXException(VCXExceptionStatus.INVALID_AUTHORIZATION_TYPE));
                throw new VCXException(VCXExceptionStatus.INVALID_AUTHORIZATION_TYPE);
            }
        }
    }

    private void handleBearerToken(String token, HttpServletRequest request) throws VCXException, IOException {

        UserCredential userCredential = getBearerTokenUser(token.trim(), request);

        if (userCredential != null) {
            VCXAuthenticationToken authentication = getAuthentication(userCredential, request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

    }

    private UserCredential getBearerTokenUser(String token, HttpServletRequest request) throws VCXException, IOException {
        try {
            return keyleadConfiguration.checkBearerAuthentication(token, request);
        } catch (VCXException e) {
            if (e.getStatus() == VCXExceptionStatus.BAD_GATEWAY) {
                throw new VCXException(VCXExceptionStatus.SSO_CONNECTION_ERROR);
            }
            return null;
        }
    }

    private VCXAuthenticationToken getAuthentication(UserCredential credential, HttpServletRequest request) {

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        String principal = credential.getUser().getUsername();

        return new VCXAuthenticationToken(principal, credential, authorities);
    }
}
