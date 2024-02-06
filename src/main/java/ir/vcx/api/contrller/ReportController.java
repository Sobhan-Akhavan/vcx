package ir.vcx.api.contrller;

import com.fanapium.keylead.client.users.ClientModifiableUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.vcx.api.model.ApiPageList;
import ir.vcx.api.model.IdentityType;
import ir.vcx.api.model.RestResponse;
import ir.vcx.data.entity.VCXUser;
import ir.vcx.data.entity.VCXUserLimit;
import ir.vcx.data.mapper.UserLimitMapper;
import ir.vcx.domain.model.sso.otp.Handshake;
import ir.vcx.domain.service.UserLimitService;
import ir.vcx.domain.service.UserService;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.KeyleadConfiguration;
import ir.vcx.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Report Controller")
@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/reports")
@SecurityRequirement(name = "Bearer")
public class ReportController {

    private final UserService userService;
    private final UserLimitService userLimitService;
    private final KeyleadConfiguration keyleadConfiguration;
    private final UserUtil userUtil;

    @Autowired
    public ReportController(UserService userService, UserLimitService userLimitService, KeyleadConfiguration keyleadConfiguration, UserUtil userUtil) {
        this.userService = userService;
        this.userLimitService = userLimitService;
        this.keyleadConfiguration = keyleadConfiguration;
        this.userUtil = userUtil;
    }

    @Operation(
            summary = "get user",
            description = "get user"
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
    @GetMapping("/users/{identity}")
    public ResponseEntity<?> getUser(
            @PathVariable(value = "identity")
            @Parameter(description = "user identity", required = true)
            String identity,
            @RequestParam(value = "identityType")
            @Parameter(description = "identity type", required = true)
            IdentityType identityType,
            @RequestParam(value = "addUser", required = false)
            @Parameter(description = "if user hasn't login yet, user will be added")
            boolean addUser
    ) throws VCXException {

        VCXUser vcxAdminUser = Optional.ofNullable(userUtil.getCredential().getUser())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNAUTHORIZED));

        ClientModifiableUser clientModifiableUser = keyleadConfiguration.getSSOUser(identity, identityType);
        if (clientModifiableUser == null) {
            throw new VCXException(VCXExceptionStatus.USER_NOT_FOUND);
        }

        VCXUser vcxUser;
        if (addUser) {
            vcxUser = userService.getOrCreatePodUser(clientModifiableUser);
        } else {
            vcxUser = userService.getUser(clientModifiableUser);
        }

        Optional<ir.vcx.data.entity.VCXUserLimit> userPlan = userLimitService.getUserLimit(vcxUser);

        ir.vcx.data.entity.VCXUserLimit vcxUserLimit = userPlan.orElseGet(VCXUserLimit::new);
        vcxUserLimit.setUser(vcxUser);

        return ResponseEntity.ok(RestResponse.Builder()
                .result(new ApiPageList<>(UserLimitMapper.INSTANCE.entityToApi(vcxUserLimit)))
                .status(HttpStatus.OK)
                .build()
        );

    }

}
