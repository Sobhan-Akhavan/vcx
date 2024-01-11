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
import ir.vcx.api.model.RestResponse;
import ir.vcx.data.entity.VCXPlan;
import ir.vcx.data.mapper.PlanMapper;
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
            @RequestParam(name = "name")
            @Parameter(description = "plan name", required = true)
            String name,
            @RequestParam(name = "limit")
            @Parameter(description = "month limitation", required = true)
            VCXPlan.MonthLimit limit,
            @RequestParam(name = "active", defaultValue = "true")
            @Parameter(description = "is plan active", required = true)
            boolean active
    ) throws VCXException {

        VCXPlan vcxPlan = planService.addPlan(name, limit, active);

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
}
