package ir.vcx.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by a.rokni on 2020/03/11 @Space.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponse {

    private String id;
    private String messageId;

    private String principal;

    private Long userId;
    private Long userSsoId;
    private String username;

    private Long agentId;
    private Long agentSsoId;
    private String agentUsername;

    private String token;
    private List<String> scopes;

    private Date timestamp;
    private Date requestTimestamp;
    private Date responseTimestamp;

    private Long ssoResponseTime;

    private String uri;
    private String method;
    private String endpointName;
    private String apiVersion;
    private String remoteAddr;
    private String realRemoteAddr;

    private Map<String, String[]> parameters;
    private String ignoredParameters;

    private Map<String, String[]> requestHeaders;
    private Map<String, String[]> responseHeaders;
    private int status;
    private Object response;
    private Object legacyResponse;
    private Object errorResponse;
    private String serverId;

    private boolean deprecated;

    private String userAgent;
    private String accept;

    private String tokenIssuerId;
    private String tokenIssuerClient;

    private String stackTrace;

}
