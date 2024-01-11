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
import ir.vcx.data.entity.VCXFolder;
import ir.vcx.data.mapper.FolderMapper;
import ir.vcx.domain.model.sso.otp.Handshake;
import ir.vcx.domain.service.FolderService;
import ir.vcx.exception.VCXException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Folder Controller")
@CrossOrigin("*")
@RequestMapping("/api/v1/folders")
@SecurityRequirement(name = "Bearer")
@RestController
public class FolderController {

    private final FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @Operation(
            summary = "delete folder",
            description = "delete folder"
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
    public ResponseEntity<?> deleteFolder(
            @RequestParam(name = "name")
            @Parameter(description = "name of folder(destination folder for upload file)", required = true)
            String name,
            @RequestParam(name = "season", required = false)
            @Parameter(description = "season of series")
            Integer season
    ) throws VCXException {

        VCXFolder vcxFolder = folderService.deleteFolder(name, season);

        ir.vcx.api.model.VCXFolder folder = FolderMapper.INSTANCE.entityToApi(vcxFolder);

        return ResponseEntity.ok(RestResponse.Builder()
                .status(HttpStatus.OK)
                .message("پوشه مورد نظر با موفقیت حذف گردید")
                .result(new ApiPageList<>(folder))
                .build()
        );
    }
}
