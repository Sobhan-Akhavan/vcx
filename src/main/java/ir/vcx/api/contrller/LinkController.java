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
import ir.vcx.data.entity.VCXDownloadLink;
import ir.vcx.data.entity.VCXLink;
import ir.vcx.data.mapper.LinkMapper;
import ir.vcx.domain.model.sso.otp.Handshake;
import ir.vcx.domain.service.LinkService;
import ir.vcx.exception.VCXException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Sobhan on 11/9/2023 - VCX
 */

@Tag(name = "Link Controller")
@CrossOrigin("*")
@RequestMapping("/api/v1/links")
@SecurityRequirement(name = "Bearer")
@RestController
public class LinkController {
    private final LinkService linkService;

    @Autowired
    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @Operation(
            summary = "content upload link",
            description = "get link for content upload in specific destination"
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
    @GetMapping("/contents/upload")
    public ResponseEntity<?> getContentUploadLink(
            @RequestParam(name = "name")
            @Parameter(description = "name of folder(destination folder for upload file)", required = true)
            String name,
            @RequestParam(name = "season", required = false)
            @Parameter(description = "season of series")
            Integer season
    ) throws VCXException {

        VCXLink uploadLink = linkService.getContentUploadLink(name, season);

        ir.vcx.api.model.VCXLink result = LinkMapper.INSTANCE.uploadLinkEntityToApi(uploadLink);

        return ResponseEntity.ok(RestResponse.Builder()
                .status(HttpStatus.OK)
                .result(new ApiPageList<>(result))
                .build()
        );
    }

    @Operation(
            summary = "poster upload link",
            description = "get link for poster upload in specific destination"
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
    @GetMapping("/contents/{hash}/posters/upload")
    public ResponseEntity<?> getPosterUploadLink(
            @PathVariable(name = "hash")
            @Parameter(description = "hash of video", required = true)
            String hash
    ) throws VCXException {

        VCXLink posterUploadLink = linkService.getPosterUploadLink(hash);

        ir.vcx.api.model.VCXLink vcxLink = LinkMapper.INSTANCE.uploadLinkEntityToApi(posterUploadLink);

        return ResponseEntity.ok(RestResponse.Builder()
                .status(HttpStatus.OK)
                .result(new ApiPageList<>(vcxLink))
                .build()
        );
    }

    @Operation(
            summary = "content download link",
            description = "get download link for content "
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
    @GetMapping("/contents/{hash}/download")
    public ResponseEntity<?> getContentDownloadLink(
            @PathVariable(name = "hash")
            @Parameter(description = "hash of video", required = true)
            String hash
    ) throws VCXException {

        VCXDownloadLink contentDownloadLink = linkService.getContentDownloadLink(hash);

        ir.vcx.api.model.VCXLink result = LinkMapper.INSTANCE.downloadLinkEntityToApi(contentDownloadLink);

        return ResponseEntity.ok(RestResponse.Builder()
                .status(HttpStatus.OK)
                .result(new ApiPageList<>(result))
                .build()
        );
    }

}
