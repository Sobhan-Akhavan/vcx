package ir.vcx.util;

import com.fanapium.keylead.client.KeyleadClient;
import com.fanapium.keylead.client.KeyleadClientFactory;
import com.fanapium.keylead.client.exception.TokenInitializationException;
import com.fanapium.keylead.client.exception.UserOperationException;
import com.fanapium.keylead.client.tokens.ShortlifeToken;
import com.fanapium.keylead.client.tokens.Tokens;
import com.fanapium.keylead.client.users.ClientModifiableUser;
import com.fanapium.keylead.client.users.ModifiableUser;
import com.fanapium.keylead.client.users.Users;
import com.fanapium.keylead.client.vo.ClientCredentials;
import com.fanapium.keylead.common.oauth.exception.OAuthException;
import ir.vcx.api.model.IdentityType;
import ir.vcx.data.entity.VCXUser;
import ir.vcx.domain.model.UserCredential;
import ir.vcx.domain.service.UserService;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by a.rokni on 2020/03/14 @Space.
 */

@Slf4j
@Component
public class KeyleadConfiguration {


    private final UserService userService;
    @Value("${service.pod.sso.url}")
    private String POD_SSO_SERVER_URL;
    @Value("${service.pod.sso.client-id}")
    private String POD_SSO_CLIENT_ID;
    @Value("${security.sso.server-redirect-url}")
    private String SECURITY_SSO_REDIRECT_URL;
    @Value("${service.pod.sso.client-secret}")
    private String POD_SSO_CLIENT_SECRET;
    @Value("${service.pod.sso.api-token}")
    private String POD_SSO_API_TOKEN;

    private ClientCredentials clientCredentials;

    @Autowired
    public KeyleadConfiguration(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {

        KeyleadClient keyleadClient = KeyleadClientFactory.createClient(POD_SSO_SERVER_URL);

        clientCredentials = new ClientCredentials(
                POD_SSO_CLIENT_ID,
                POD_SSO_CLIENT_SECRET,
                SECURITY_SSO_REDIRECT_URL,
                POD_SSO_API_TOKEN
        );
    }


    public UserCredential checkBearerAuthentication(String token, HttpServletRequest request) throws VCXException, IOException {

        UserCredential userCredential = null;

        if (StringUtils.isNotBlank(token)) {

            Pair<ModifiableUser, ShortlifeToken> ssoToken;

            ssoToken = checkSsoToken(token);

            ModifiableUser modifiableUser = ssoToken.getLeft();

            VCXUser user = userService.getOrCreatePodUser(modifiableUser);

            userCredential = new UserCredential(token, user);
        }

        if (userCredential == null) {
            throw new VCXException(VCXExceptionStatus.UNAUTHORIZED);
        }

        return userCredential;
    }

    private Pair<ModifiableUser, ShortlifeToken> checkSsoToken(String token) throws VCXException {
        try {
            return Pair.of(Users.fromAccessToken(token), Tokens.fromAccessToken(token, clientCredentials));
        } catch (UserOperationException | TokenInitializationException e) {
            throw new VCXException(VCXExceptionStatus.SSO_CONNECTION_ERROR);
        } catch (OAuthException e) {
            throw new VCXException(VCXExceptionStatus.UNAUTHORIZED);
        }
    }

    public ClientModifiableUser getSSOUser(String identity, IdentityType identityType) throws VCXException {

        ClientModifiableUser user;

        try {
            switch (identityType) {
                case SSO_ID:
                    user = Users.fromId(Long.valueOf(identity), clientCredentials);
                    break;
                case USERNAME:
                    user = Users.fromUsername(identity, clientCredentials);
                    break;
                case PHONE_NUMBER:
                    user = Users.fromPhoneNumber(identity, clientCredentials);
                    break;
                default:
                    throw new VCXException(VCXExceptionStatus.INVALID_AUTHENTICATION_INFORMATION);
            }
        } catch (OAuthException | NumberFormatException | UserOperationException e) {
            throw new VCXException(VCXExceptionStatus.SSO_CONNECTION_ERROR);
        }

        return user;
    }
}
