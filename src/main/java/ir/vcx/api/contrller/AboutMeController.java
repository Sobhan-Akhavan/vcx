package ir.vcx.api.contrller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.vcx.api.model.ApiPageList;
import ir.vcx.api.model.RestResponse;
import ir.vcx.data.entity.VCXUser;
import ir.vcx.data.entity.VCXUserLimit;
import ir.vcx.data.mapper.UserLimitMapper;
import ir.vcx.domain.model.sso.otp.Handshake;
import ir.vcx.domain.service.UserLimitService;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Created by Sobhan on 11/16/2023 - VCX
 */

@Tag(name = "Me Controller")
@CrossOrigin("*")
@RequestMapping("/api/v1/me")
@SecurityRequirement(name = "Bearer")
@RestController
public class AboutMeController {
    private final UserLimitService userLimitService;
    private final UserUtil userUtil;

    @Autowired
    public AboutMeController(UserLimitService userLimitService, UserUtil userUtil) {
        this.userLimitService = userLimitService;
        this.userUtil = userUtil;
    }

    @Operation(
            summary = "about user",
            description = "get detail about user"
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
    @GetMapping
    public ResponseEntity<?> getUser() throws VCXException {

        VCXUser vcxUser = Optional.ofNullable(userUtil.getCredential().getUser())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNAUTHORIZED));

        Optional<VCXUserLimit> userPlan = userLimitService.getUserLimit(vcxUser);

        VCXUserLimit vcxUserLimit = userPlan.orElseGet(VCXUserLimit::new);
        vcxUserLimit.setUser(vcxUser);

        return ResponseEntity.ok(RestResponse.Builder()
                .result(new ApiPageList<>(UserLimitMapper.INSTANCE.entityToApi(vcxUserLimit)))
                .status(HttpStatus.OK)
                .build());
    }
}
