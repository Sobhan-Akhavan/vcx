package ir.vcx.api.contrller;

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
import ir.vcx.data.entity.VCXPlan;
import ir.vcx.data.entity.VCXUserLimit;
import ir.vcx.data.mapper.PlanMapper;
import ir.vcx.data.mapper.UserLimitMapper;
import ir.vcx.domain.model.sso.otp.Handshake;
import ir.vcx.domain.service.PlanService;
import ir.vcx.exception.VCXException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Plan Controller")
@CrossOrigin("*")
@RequestMapping("/api/v1/plans")
@SecurityRequirement(name = "Bearer")
@RestController
public class PlanController {

    private final PlanService planService;

    @Autowired
    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @Operation(
            summary = "add plan",
            description = "add plan"
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
    @PostMapping
    public ResponseEntity<?> addPlan(
            @RequestParam(name = "price")
            @Parameter(description = "price (TOMAN)", required = true)
            long price,
            @RequestParam(name = "limit")
            @Parameter(description = "month limitation", required = true)
            VCXPlan.DaysLimit limit,
            @RequestParam(name = "active", defaultValue = "TRUE")
            @Parameter(description = "is plan active", schema = @Schema(defaultValue = "TRUE", allowableValues = {"TRUE", "FALSE"}), required = true)
            boolean active
    ) throws VCXException {

        VCXPlan vcxPlan = planService.addPlan(price, limit, active);

        ir.vcx.api.model.VCXPlan result = PlanMapper.INSTANCE.entityToApi(vcxPlan);

        return ResponseEntity.ok(RestResponse.Builder()
                .result(new ApiPageList<>(result))
                .status(HttpStatus.OK)
                .build()
        );

    }

    @Operation(
            summary = "get plans list",
            description = "get plans list"
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
    public ResponseEntity<?> getPlans(
    ) throws VCXException {

        Pair<List<VCXPlan>, Long> plansList = planService.getPlansList();

        List<ir.vcx.api.model.VCXPlan> contentList = plansList.getKey()
                .stream()
                .map(PlanMapper.INSTANCE::entityToApi)
                .collect(Collectors.toList());

        return ResponseEntity.ok(RestResponse.Builder()
                .result(new ApiPageList<>(contentList, plansList.getValue()))
                .status(HttpStatus.OK)
                .build()
        );

    }

    @Operation(
            summary = "deactivate all plans",
            description = "deactivate all plans"
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
    @DeleteMapping
    public ResponseEntity<?> deactivatePlans(
    ) throws VCXException {

        planService.deactivatePlans();

        return ResponseEntity.ok(RestResponse.Builder()
                .message("تمامی طرح‌های اشتراک با موفقیت غیر فعال گردیدند")
                .status(HttpStatus.OK)
                .build()
        );

    }


    @Operation(
            summary = "purchase plan",
            description = "purchase plan"
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
    @PutMapping("/{hash}")
    public ResponseEntity<RestResponse<Object>> purchasePlan(
            @PathVariable(name = "hash")
            @Parameter(description = "plan hash", required = true)
            String hash,
            @RequestParam(name = "trackingNumber", required = false)
            @Parameter(description = "purchase number")
            String trackingNumber
    ) throws VCXException {

        VCXUserLimit vcxUserLimit = planService.purchasePlan(hash, trackingNumber);

        ir.vcx.api.model.VCXUserLimit result = UserLimitMapper.INSTANCE.entityToApi(vcxUserLimit);

        return ResponseEntity.ok(RestResponse.Builder()
                .result(new ApiPageList<>(result))
                .status(HttpStatus.OK)
                .build()
        );

    }

    @Operation(
            summary = "set user plan",
            description = "set plan for user"
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
    @PutMapping("/{hash}/users/{identity}")
    public ResponseEntity<RestResponse<Object>> setUserPlan(
            @PathVariable(name = "hash")
            @Parameter(description = "plan hash", required = true)
            String hash,
            @PathVariable(name = "identity")
            @Parameter(description = "user identity", required = true)
            String identity,
            @RequestParam(name = "identityType")
            @Parameter(description = "user identity type", required = true)
            IdentityType identityType,
            @RequestParam(name = "force")
            @Parameter(description = "if active plan exist, force to change plan", schema = @Schema(defaultValue = "TRUE", allowableValues = {"TRUE", "FALSE"}), required = true)
            boolean force,
            @RequestParam(name = "trackingNumber", required = false)
            @Parameter(description = "purchase number")
            String trackingNumber
    ) throws VCXException {

        VCXUserLimit vcxUserLimit = planService.purchasePlan(hash, identity, identityType, force, trackingNumber);

        ir.vcx.api.model.VCXUserLimit result = UserLimitMapper.INSTANCE.entityToApi(vcxUserLimit);

        return ResponseEntity.ok(RestResponse.Builder()
                .result(new ApiPageList<>(result))
                .status(HttpStatus.OK)
                .build()
        );

    }

}
