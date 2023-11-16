package ir.vcx.api.filter.security;


import ir.vcx.domain.model.UserCredential;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * created by s.akhavan 2021/December/28 podspace-spring-backend
 */

public class VCXAuthenticationToken extends AbstractAuthenticationToken {

    private final UserCredential credential;
    private final String principal;
    private Boolean auth;

    public VCXAuthenticationToken(String principal, UserCredential credential, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.credential = credential;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return credential;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return auth;
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        auth = authenticated;
    }

    @Override
    public String getName() {
        return "VCXAuthentication";
    }
}
