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
import ir.vcx.api.model.Order;
import ir.vcx.api.model.Paging;
import ir.vcx.api.model.RestResponse;
import ir.vcx.data.entity.GenreType;
import ir.vcx.data.entity.VCXContent;
import ir.vcx.data.entity.VideoType;
import ir.vcx.data.mapper.ContentMapper;
import ir.vcx.domain.model.sso.otp.Handshake;
import ir.vcx.domain.service.ContentService;
import ir.vcx.exception.VCXException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Sobhan on 11/23/2023 - VCX
 */

@Tag(name = "Content Controller")
@CrossOrigin("*")
@RequestMapping("/api/v1/contents")
@RestController
public class ContentController {

    private final ContentService contentService;

    @Autowired
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @Operation(
            summary = "create content",
            description = "create content"
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
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<?> createContent(
            @RequestParam(name = "file_url")
            @Parameter(description = "url of file which is uploaded completely", required = true)
            String file_url,
            @RequestParam(name = "name", required = false)
            @Parameter(description = "content name")
            String name,
            @RequestParam(name = "description")
            @Parameter(description = "description of video", required = true)
            String description,
            @RequestParam(name = "videoType")
            @Parameter(description = "type of video", required = true)
            VideoType videoType,
            @RequestParam(name = "genreType")
            @Parameter(description = "type of genres", required = true)
            Set<GenreType> genreTypes
    ) throws Exception {

        VCXContent vcxContent = contentService.createContent(file_url, name, description, videoType, genreTypes);

        ir.vcx.api.model.VCXContent content = ContentMapper.INSTANCE.entityToApi(vcxContent);

        return ResponseEntity.ok(RestResponse.Builder()
                .status(HttpStatus.OK)
                .result(new ApiPageList<>(content))
                .build()
        );
    }


    @Operation(
            summary = "get content",
            description = "get content"
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
    @GetMapping("/{hash}")
    public ResponseEntity<?> getContent(
            @PathVariable(name = "hash")
            @Parameter(description = "hash of video", required = true)
            String hash
    ) throws VCXException {

        VCXContent vcxContent = contentService.getAvailableContent(hash, Boolean.TRUE, Boolean.FALSE);

        ir.vcx.api.model.VCXContent content = ContentMapper.INSTANCE.entityToApi(vcxContent);

        return ResponseEntity.ok(RestResponse.Builder()
                .status(HttpStatus.OK)
                .result(new ApiPageList<>(content))
                .build()
        );
    }

    @Operation(
            summary = "update content",
            description = "update content"
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
    @PatchMapping("/{hash}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<?> updateContent(
            @PathVariable(name = "hash")
            @Parameter(description = "hash of video", required = true)
            String hash,
            @RequestParam(name = "name", required = false)
            @Parameter(description = "name of video")
            String name,
            @RequestParam(name = "description", required = false)
            @Parameter(description = "description of video")
            String description,
            @RequestParam(name = "genreType", required = false)
            @Parameter(description = "type of genres")
            Set<GenreType> genreTypes
    ) throws VCXException {

        VCXContent vcxContent = contentService.updateContent(hash, name, description, genreTypes);

        ir.vcx.api.model.VCXContent content = ContentMapper.INSTANCE.entityToApi(vcxContent);

        return ResponseEntity.ok(RestResponse.Builder()
                .status(HttpStatus.OK)
                .result(new ApiPageList<>(content))
                .build()
        );
    }

    @Operation(
            summary = "delete content",
            description = "delete content"
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
    @DeleteMapping("/{hash}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<?> deleteContent(
            @PathVariable(name = "hash")
            @Parameter(description = "hash of video", required = true)
            String hash
    ) throws VCXException {

        contentService.deleteContent(hash);

        return ResponseEntity.ok(RestResponse.Builder()
                .status(HttpStatus.OK)
                .message("فایل مورد نظر با موفقیت حذف شد.")
                .build()
        );
    }

    @Operation(
            summary = "search on contents",
            description = "search on contents"
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
    public ResponseEntity<?> searchOnContents(
            @RequestParam(name = "name", required = false)
            @Parameter(description = "name of videos (must be 3 character minimum)")
            String name,
            @RequestParam(name = "videoType", required = false)
            @Parameter(description = "type of video")
            VideoType videoType,
            @RequestParam(name = "genreType", required = false)
            @Parameter(description = "type of genres")
            Set<GenreType> genreTypes,
            @RequestParam(name = "includePosterLessContent", defaultValue = "FALSE")
            @Parameter(description = "maybe contents return without poster", schema = @Schema(defaultValue = "FALSE", allowableValues = {"FALSE", "TRUE"}), required = true)
            boolean includePosterLessContent,
            @RequestParam(value = "start", defaultValue = "0")
            @Parameter(description = "offset of pagination", schema = @Schema(defaultValue = "0", minimum = "0"), required = true)
            int start,
            @RequestParam(value = "size", defaultValue = "20")
            @Parameter(description = "size of pagination", schema = @Schema(defaultValue = "20", minimum = "1"), required = true)
            int size,
            @RequestParam(name = "order", defaultValue = "UPDATED")
            @Parameter(description = "sort result by", schema = @Schema(defaultValue = "UPDATED", allowableValues = {"CREATED", "UPDATED"}), required = true)
            Order order,
            @RequestParam(name = "desc", defaultValue = "FALSE")
            @Parameter(description = "sort returned items ascending or descending", schema = @Schema(defaultValue = "FALSE", allowableValues = {"FALSE", "TRUE"}), required = true)
            boolean desc
    ) throws VCXException {


        Paging paging = new Paging(start, size, order, desc);

        Pair<List<VCXContent>, Long> contents = contentService.searchOnContents(name, videoType, genreTypes, includePosterLessContent, paging);

        List<ir.vcx.api.model.VCXContent> contentList = contents.getKey()
                .stream()
                .map(ContentMapper.INSTANCE::entityToApi)
                .collect(Collectors.toList());

        return ResponseEntity.ok(RestResponse.Builder()
                .status(HttpStatus.OK)
                .result(new ApiPageList<>(contentList, contents.getValue()))
                .build()
        );
    }

    @Operation(
            summary = "add poster for content",
            description = "add poster"
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
    @PutMapping("/{hash}/posters/{posterHash}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<?> addPoster(
            @PathVariable(name = "hash")
            @Parameter(description = "hash of video", required = true)
            String hash,
            @PathVariable(name = "posterHash")
            @Parameter(description = "poster hash", required = true)
            String posterHash,
            @RequestParam(name = "horizontal")
            @Parameter(description = "poster orientation type", required = true)
            boolean horizontal
    ) throws VCXException {

        VCXContent vcxContent = contentService.addPoster(hash, posterHash, horizontal);

        ir.vcx.api.model.VCXContent content = ContentMapper.INSTANCE.entityToApi(vcxContent);

        return ResponseEntity.ok(RestResponse.Builder()
                .status(HttpStatus.OK)
                .result(new ApiPageList<>(content))
                .build()
        );
    }

    @Operation(
            summary = "getContentBytes",
            description = "getContentBytes"
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
    @GetMapping("/{hash}/bytes")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<?> getContentBytes(
            @PathVariable(name = "hash")
            @Parameter(description = "hash of video", required = true)
            String hash,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {

        byte[] contentsBytes = contentService.getContentsBytes(hash, response);

        return ResponseEntity.ok()
                .body(contentsBytes);
    }

}
