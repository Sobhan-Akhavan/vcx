package ir.vcx.util;

import ir.vcx.domain.model.UserCredential;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by Sobhan on 11/16/2023 - VCX
 */

@Slf4j
@Component
public class UserUtil {
    private boolean isAuthenticated() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null &&
                context.getAuthentication() != null &&
                context.getAuthentication().getPrincipal() != null &&
                !context.getAuthentication().getPrincipal().equals("_ANONYMOUS_");
    }

    public UserCredential getCredential() {

        if (!isAuthenticated()) {
            return new UserCredential();
        }
        SecurityContext context = SecurityContextHolder.getContext();
        return (UserCredential) context.getAuthentication().getCredentials();

    }
}
