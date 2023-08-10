package ir.vcx.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import ir.vcx.api.model.DeviceType;
import ir.vcx.domain.model.sso.otp.AccessToken;
import ir.vcx.domain.model.sso.otp.Authorize;
import ir.vcx.domain.model.sso.otp.Handshake;
import ir.vcx.domain.model.sso.otp.Verify;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.JsonUtil;
import ir.vcx.util.httprequest.RestRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

//TODO - migrate httpClient to webClient and remove httprequest package and dependency.

@Slf4j
@Service
public class Oauth2Service {

    private final RestRequest restRequest;
    @Value("${service.pod.sso.url}")
    private String POD_SSO_SERVER_URL;
    @Value("${service.pod.sso.client-id}")
    private String POD_SSO_CLIENT_ID;
    @Value("${service.pod.sso.client-secret}")
    private String POD_SSO_CLIENT_SECRET;
    @Value("${service.pod.sso.api-token}")
    private String POD_SSO_API_TOKEN;
    @Value("${service.pod.sso.scope}")
    private String POD_SSO_SCOPE;
    @Value("${service.pod.sso.otp-signature}")
    private String POD_SSO_OTP_SIGNATURE;

    public Oauth2Service(RestRequest restRequest) {
        this.restRequest = restRequest;
    }

    public Handshake otpHandshake(String deviceUID, String deviceName, DeviceType deviceType, String deviceOS,
                                  String deviceOsVersion, double deviceLat, double deviceLon) throws VCXException {
        try {

            StringBuilder builder = new StringBuilder(POD_SSO_SERVER_URL)
                    .append("/oauth2/clients/handshake/").append(POD_SSO_CLIENT_ID)
                    .append("?").append("device_uid=").append(deviceUID)
                    .append("&").append("algorithm=").append("rsa-sha256")
                    .append("&").append("device_os=").append(URLEncoder.encode(deviceOS, StandardCharsets.UTF_8.toString()))
                    .append("&").append("device_os_version=").append(URLEncoder.encode(deviceOsVersion, StandardCharsets.UTF_8.toString()))
                    .append("&").append("device_type=").append(URLEncoder.encode(deviceType.getValue(), StandardCharsets.UTF_8.toString()))
                    .append("&").append("device_name=").append(URLEncoder.encode(deviceName, StandardCharsets.UTF_8.toString()));

            if (deviceLat != 0.0 && deviceLon != 0.0) {
                builder.append("&").append("device_lat=").append(deviceLat)
                        .append("&").append("device_lon=").append(deviceLon);
            }

            Map<String, String> header = new HashMap<>();
            header.put("Authorization", "bearer " + POD_SSO_API_TOKEN);
            header.put("Content-Type", "application/x-www-form-urlencoded");
            header.put("Accept-Language", "fa");


            Handshake handshake = restRequest.post(builder.toString(), (Map) null, header, Handshake.class);

            return handshake;

        } catch (IOException e) {
            throw new VCXException(VCXExceptionStatus.PROCESS_REQUEST_ERROR);
        }
    }

    public Authorize otpAuthorize(String keyId, String identity, String payload)
            throws VCXException {

        try {
            StringBuilder builder = new StringBuilder(POD_SSO_SERVER_URL)
                    .append("/oauth2/otp/authorize/").append(URLEncoder.encode(identity, StandardCharsets.UTF_8.toString()))
                    .append("?").append("response_type=").append("code")
                    .append("&").append("scope=").append(POD_SSO_SCOPE);

            if (StringUtils.isNotBlank(payload)) {
                builder.append("&").append("state=").append(URLEncoder.encode(payload, StandardCharsets.UTF_8.toString()));
            }

            Map<String, String> header = getSignHeaderMap(keyId);

            Authorize authorize = restRequest.post(builder.toString(), (Map) null, header, Authorize.class);

            return authorize;


        } catch (IOException e) {
            log.error("PROCESS_REQUEST_ERROR in otpAuthorize", e);
            throw new VCXException(VCXExceptionStatus.PROCESS_REQUEST_ERROR);
        } catch (HttpStatusCodeException e) {

            throw getSpaceExceptionFromSSoError(e);
        }
    }

    private Map<String, String> getSignHeaderMap(String keyId) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");

        header.put("Accept-Language", "fa");
        header.put("Authorization", "Signature keyId=\"" + keyId + "\", signature=\"" + POD_SSO_OTP_SIGNATURE + "\", headers=\"host\"");
        header.put("host", "accounts.pod.ir");
        return header;
    }

    private VCXException getSpaceExceptionFromSSoError(HttpStatusCodeException e) {

        if (e.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            return new VCXException(VCXExceptionStatus.SSO_CONNECTION_ERROR);
        }

        try {

            String error_description = JsonUtil.getObject(
                            e.getResponseBodyAsString(StandardCharsets.UTF_8),
                            new TypeReference<Map<String, String>>() {
                            })
                    .get("error_description");
            return new VCXException(VCXExceptionStatus.SSO_INVALID_REQUEST, error_description);

        } catch (Exception exception) {

            return new VCXException(VCXExceptionStatus.SSO_CONNECTION_ERROR);
        }
    }

    public AccessToken otpVerify(String keyId, String identity, String otp)
            throws VCXException {
        try {

            String builder = POD_SSO_SERVER_URL +
                    "/oauth2/otp/verify/" + URLEncoder.encode(identity, StandardCharsets.UTF_8.toString()) +
                    "?" + "otp=" + otp;

            Map<String, String> header = getSignHeaderMap(keyId);

            Verify verify = restRequest.post(builder, (Map) null, header, Verify.class);

            return getOtpAccessToken(verify.getCode(), keyId);

        } catch (IOException e) {
            log.error("PROCESS_REQUEST_ERROR in otpVerify", e);
            throw new VCXException(VCXExceptionStatus.PROCESS_REQUEST_ERROR);
        } catch (HttpStatusCodeException e) {

            throw getSpaceExceptionFromSSoError(e);

        }
    }

    private AccessToken getOtpAccessToken(String code, String keyId) throws IOException, VCXException {

        String builder = POD_SSO_SERVER_URL +
                "/oauth2/token/" +
                "?" + "grant_type=" + "authorization_code" +
                "&" + "code=" + code +
                "&" + "client_id=" + POD_SSO_CLIENT_ID +
                "&" + "client_secret=" + POD_SSO_CLIENT_SECRET;

        Map<String, String> header = getSignHeaderMap(keyId);

        AccessToken accessToken = restRequest.post(builder, (Map) null, header, AccessToken.class);
//
//        //change to refresh key
//        accessToken.setRefreshToken(createPodRefreshKey(accessToken.getRefreshToken()));

        return accessToken;
    }
}
