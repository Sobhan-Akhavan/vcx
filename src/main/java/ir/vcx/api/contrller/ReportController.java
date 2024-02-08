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
import ir.vcx.api.model.*;
import ir.vcx.data.entity.VCXContentVisit;
import ir.vcx.data.entity.VCXUser;
import ir.vcx.data.entity.VCXUserLimit;
import ir.vcx.data.mapper.ContentVisitedMapper;
import ir.vcx.data.mapper.UserLimitMapper;
import ir.vcx.data.mapper.UserMapper;
import ir.vcx.domain.model.sso.otp.Handshake;
import ir.vcx.domain.service.ContentService;
import ir.vcx.domain.service.UserLimitService;
import ir.vcx.domain.service.UserService;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.KeyleadConfiguration;
import ir.vcx.util.UserUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Report Controller")
@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/reports")
@SecurityRequirement(name = "Bearer")
public class ReportController {

    private final UserService userService;
    private final UserLimitService userLimitService;
    private final ContentService contentService;
    private final KeyleadConfiguration keyleadConfiguration;
    private final UserUtil userUtil;

    @Autowired
    public ReportController(UserService userService, UserLimitService userLimitService, ContentService contentService, KeyleadConfiguration keyleadConfiguration, UserUtil userUtil) {
        this.userService = userService;
        this.userLimitService = userLimitService;
        this.contentService = contentService;
        this.keyleadConfiguration = keyleadConfiguration;
        this.userUtil = userUtil;
    }

    @Operation(
            summary = "search on users",
            description = "search on users"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Long.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),
    })
    @GetMapping("/users")
    public ResponseEntity<?> searchOnUsers(
            @RequestParam(value = "identity", required = false)
            @Parameter(description = "user identity")
            String identity,
            @RequestParam(value = "identityType", required = false)
            @Parameter(description = "identity type", schema = @Schema(allowableValues = {"USERNAME", "SSO_ID"}))
            IdentityType identityType,
            @RequestParam(value = "start", defaultValue = "0")
            @Parameter(description = "offset of pagination", schema = @Schema(defaultValue = "0", minimum = "0"), required = true)
            int start,
            @RequestParam(value = "size", defaultValue = "20")
            @Parameter(description = "size of pagination", schema = @Schema(defaultValue = "20", minimum = "1"), required = true)
            int size,
            @RequestParam(name = "order", defaultValue = "CREATED")
            @Parameter(description = "sort result by", schema = @Schema(defaultValue = "UPDATED", allowableValues = {"CREATED", "UPDATED", "USERNAME", "NAME", "SSO_ID"}), required = true)
            Order order,
            @RequestParam(name = "desc", defaultValue = "FALSE")
            @Parameter(description = "sort returned items ascending or descending", schema = @Schema(defaultValue = "FALSE", allowableValues = {"FALSE", "TRUE"}), required = true)
            boolean desc
    ) throws VCXException {


        Paging paging = new Paging(start, size, order, desc);

        Pair<List<VCXUser>, Long> users = userService.searchOnUsers(identity, identityType, paging);

        List<ir.vcx.api.model.VCXUser> vcxUserList = users.getKey()
                .stream()
                .map(UserMapper.INSTANCE::entityToApi)
                .collect(Collectors.toList());

        return ResponseEntity.ok(RestResponse.Builder()
                .status(HttpStatus.OK)
                .result(new ApiPageList<>(vcxUserList, users.getValue()))
                .build()
        );

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
                .status(HttpStatus.OK)
                .result(new ApiPageList<>(UserLimitMapper.INSTANCE.entityToApi(vcxUserLimit)))
                .build()
        );

    }

    @Operation(
            summary = "most visited content",
            description = "most visited content"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Long.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestResponse.class))}),
    })
    @GetMapping("/contents/visit")
    public ResponseEntity<?> mostVisitedContent(
            @RequestParam(value = "start", defaultValue = "0")
            @Parameter(description = "offset of pagination", schema = @Schema(defaultValue = "0", minimum = "0"), required = true)
            int start,
            @RequestParam(value = "size", defaultValue = "20")
            @Parameter(description = "size of pagination", schema = @Schema(defaultValue = "20", minimum = "1"), required = true)
            int size,
            @RequestParam(name = "order", defaultValue = "COUNT")
            @Parameter(description = "sort result by", schema = @Schema(defaultValue = "COUNT", allowableValues = {"NAME", "COUNT"}), required = true)
            Order order,
            @RequestParam(name = "desc", defaultValue = "TRUE")
            @Parameter(description = "sort returned items ascending or descending", schema = @Schema(defaultValue = "TRUE", allowableValues = {"FALSE", "TRUE"}), required = true)
            boolean desc
    ) throws VCXException {


        Paging paging = new Paging(start, size, order, desc);

        Pair<List<VCXContentVisit>, Long> contentVisits = contentService.mostVisitedVideo(paging);

        List<VCXContentVisited> result = contentVisits.getKey()
                .stream()
                .map(ContentVisitedMapper.INSTANCE::entityToApi)
                .collect(Collectors.toList());

        return ResponseEntity.ok(RestResponse.Builder()
                .status(HttpStatus.OK)
                .result(new ApiPageList<>(result, contentVisits.getValue()))
                .build()
        );

    }

}
