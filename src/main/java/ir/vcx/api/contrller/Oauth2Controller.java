package ir.vcx.api.contrller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.vcx.api.model.RestResponse;
import ir.vcx.domain.model.sso.AccessToken;
import ir.vcx.domain.model.sso.SsoState;
import ir.vcx.domain.service.Oauth2Service;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Sobhan on 11/16/2023 - VCX
 */

@Tag(name = "Oauth2")
@CrossOrigin("*")
@RequestMapping("/api/v1/oauth2")
@RestController
public class Oauth2Controller {

    private final Oauth2Service oauth2Service;

    @Autowired
    public Oauth2Controller(Oauth2Service oauth2Service) {
        this.oauth2Service = oauth2Service;
    }

    @Operation(
            summary = "refresh token",
            description = "refresh token and get new access token and refresh token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccessToken.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Invalid SSO Type",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),
    })
    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestParam(value = "refreshToken")
            @Parameter(description = "refreshToken", required = true)
            String refreshToken
    ) throws VCXException {


        AccessToken accessToken = oauth2Service.refresh(refreshToken);
        accessToken.setIdToken(null);

        return ResponseEntity.ok(
                RestResponse.Builder()
                        .result(accessToken)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @Operation(
            summary = "verify login",
            description = "verify callback url to redirect from sso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = void.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid SSO Type",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),
    })
    @GetMapping("/verify")
    public void verify(
            @RequestParam(value = "code")
            @Parameter(description = "code", required = true)
            String code,
            @RequestParam(value = "state")
            @Parameter(description = "state", required = true)
            String state,
            HttpServletResponse response) throws VCXException, IOException {

        if (StringUtils.isBlank(state) || StringUtils.isBlank(code)) {
            throw new VCXException(VCXExceptionStatus.INVALID_REQUEST);
        }

        SsoState ssoState = oauth2Service.parseState(state);
        String redirect = oauth2Service.verify(code, ssoState);


        response.sendRedirect(redirect);
    }

}
