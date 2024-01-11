package ir.vcx.api.contrller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.vcx.api.model.DeviceType;
import ir.vcx.api.model.RestResponse;
import ir.vcx.api.model.mapper.HandshakeMapper;
import ir.vcx.domain.model.sso.AccessToken;
import ir.vcx.domain.model.sso.otp.Authorize;
import ir.vcx.domain.model.sso.otp.Handshake;
import ir.vcx.domain.service.Oauth2Service;
import ir.vcx.exception.VCXException;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Tag(name = "Oauth2 Controller")
@CrossOrigin("*")
@RequestMapping("/api/v1/oauth2/otp")
@RestController
public class OtpController {

    private final Oauth2Service oauth2Service;

    public OtpController(Oauth2Service oauth2Service) {
        this.oauth2Service = oauth2Service;
    }

    @Operation(
            summary = "OTP handshake",
            description = "each OTP login based device should handshake for first time"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Handshake.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),
    })
    @PostMapping("/handshake")
    public ResponseEntity<?> handshake(
            @RequestParam(name = "deviceUID")
            @Parameter(description = "deviceUID", required = true)
            String deviceUID,
            @RequestParam(name = "deviceName")
            @Parameter(description = "deviceName", required = true)
            String deviceName,
            @RequestParam(name = "deviceLat", defaultValue = "0", required = false)
            @Parameter(description = "deviceLat")
            double deviceLat,
            @RequestParam(value = "deviceLon", defaultValue = "0", required = false)
            @Parameter(description = "deviceLon")
            double deviceLon,
            @RequestParam(value = "deviceOs")
            @Parameter(description = "deviceOs", required = true)
            String deviceOS,
            @RequestParam(value = "deviceOsVersion")
            @Parameter(description = "deviceOsVersion", required = true)
            String deviceOsVersion,
            @RequestParam(value = "deviceType")
            @Parameter(description = "deviceType", schema = @Schema(allowableValues = {"MOBILE_PHONE", "DESKTOP", "TABLET", "CONSOLE", "TV_DEVICE"}), required = true)
            DeviceType deviceType
    ) throws VCXException {

        Handshake handshake = oauth2Service.otpHandshake(deviceUID, deviceName, deviceType, deviceOS, deviceOsVersion,
                deviceLat, deviceLon);

        return ResponseEntity.ok(RestResponse.Builder()
                .result(HandshakeMapper.INSTANCE.mapToApi(handshake))
                .status(HttpStatus.OK)
                .build()
        );
    }

    @Operation(
            summary = "OTP authorize",
            description = "OTP login based device could authorize user identity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Authorization.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),

    })
    @PostMapping("/authorize")
    public ResponseEntity<?> authorize(
            @RequestHeader(value = "keyId")
            @Parameter(description = "keyId", required = true)
            String keyId,
            @RequestParam(value = "identity")
            @Parameter(description = "identity", required = true)
            String identity,
            @RequestParam(value = "payload", required = false)
            @Parameter(description = "payload")
            String payload
    ) throws VCXException {

        Authorize authorize = oauth2Service.otpAuthorize(keyId, identity, payload);

        return ResponseEntity.ok(RestResponse.Builder()
                .result(authorize)
                .status(HttpStatus.OK)
                .build()
        );
    }


    @Operation(
            summary = "OTP verify",
            description = "OTP login based device could verify user by code thant sent to identity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccessToken.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),
    })
    @PostMapping("/verify")
    public ResponseEntity<?> verify(
            @RequestHeader(value = "keyId")
            @Parameter(description = "keyId", required = true)
            String keyId,
            @RequestParam(value = "identity")
            @Parameter(description = "identity", required = true)
            String identity,
            @RequestParam(value = "otp")
            @Parameter(description = "otp", required = true)
            String otp
    ) throws VCXException {

        AccessToken ssoAccessToken = oauth2Service.otpVerify(keyId, identity, otp);
//        ssoAccessToken.setIdToken(null);

        return ResponseEntity.ok(RestResponse.Builder()
                .result(ssoAccessToken)
                .status(HttpStatus.OK)
                .build()
        );
    }
}
