package ir.vcx.domain.model.sso.otp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    @JsonProperty("allowedRedirectUris")
    private List<String> allowedRedirectUris;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("allowedScopes")
    private List<String> allowedScopes;

    @JsonProperty("allowedUserIPs")
    private List<String> allowedUserIPs;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("url")
    private String url;

    @JsonProperty("allowedGrantTypes")
    private List<String> allowedGrantTypes;

    @JsonProperty("signupEnabled")
    private Boolean signupEnabled;

    @JsonProperty("captchaEnabled")
    private Boolean captchaEnabled;

    @JsonProperty("loginUrl")
    private String loginUrl;

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private Integer id;
}
