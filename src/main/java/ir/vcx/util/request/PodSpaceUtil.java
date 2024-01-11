package ir.vcx.util.request;

import com.fasterxml.jackson.core.type.TypeReference;
import ir.vcx.domain.model.space.*;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.DateUtil;
import ir.vcx.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Sobhan on 11/23/2023 - VCX
 */

@Slf4j
@Component
public class PodSpaceUtil {

    private final WebClient webClient;
    @Value("${service.podspace.scheme}")
    private String PODSPACE_SCHEME;
    @Value("${service.podspace.host}")
    private String PODSPACE_HOST;
    @Value("${service.podspace.port}")
    private int PODSPACE_PORT;
    @Value("${security.sso.api-token}")
    private String API_TOKEN;

    public PodSpaceUtil(WebClient webClient) {
        this.webClient = webClient;
    }

    public SpaceResponse<Folder> createFolder(String folderName, String parentHash) throws VCXException {

        SpaceResponse<Folder> result;
        try {
            result = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(PODSPACE_SCHEME)
                            .host(PODSPACE_HOST)
                            .port(PODSPACE_PORT)
                            .path("/api/folders")
                            .queryParam("name", folderName.toUpperCase())
                            .queryParamIfPresent("parentHash", Optional.ofNullable(parentHash))
                            .build())
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<SpaceResponse<Folder>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<SpaceResponse<Folder>>() {
            });
            throw new VCXException(result.getStatus(), result.getError(), result.getMessage());
        } catch (Exception e) {
            log.error("Unknown error while create folder", e);
            throw new VCXException(VCXExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
        }
        return result;
    }

    public SpaceResponse<UploadLink> getUploadLink(String destinationHash) throws VCXException {

        SpaceResponse<UploadLink> result;
        try {
            result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(PODSPACE_SCHEME)
                            .host(PODSPACE_HOST)
                            .port(PODSPACE_PORT)
                            .path("/api/files/link")
                            .queryParam("size", 0)
                            .queryParam("destination", destinationHash)
                            .queryParam("expiration", DateUtil.futureTime(DateUtil.TimeInFuture.OneDay).getTime())
                            .build())
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<SpaceResponse<UploadLink>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<SpaceResponse<UploadLink>>() {
            });
            throw new VCXException(result.getStatus(), result.getError(), result.getMessage());
        } catch (Exception e) {
            log.error("Unknown error while get upload link folder", e);
            throw new VCXException(VCXExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
        }
        return result;
    }

    public SpaceResponse<EntityDetail> uploaded_file_info(String fileUrl) throws VCXException {
        SpaceResponse<EntityDetail> result;
        try {
            result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(PODSPACE_SCHEME)
                            .host(PODSPACE_HOST)
                            .port(PODSPACE_PORT)
                            .path("/api/files/uploaded_file_info/{file_url}")
                            .build(fileUrl))
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<SpaceResponse<EntityDetail>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<SpaceResponse<EntityDetail>>() {
            });
            throw new VCXException(result.getStatus(), result.getError(), result.getMessage());
        } catch (Exception e) {
            log.error("Unknown error while get uploaded file info folder", e);
            throw new VCXException(VCXExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
        }
        return result;
    }

    public SpaceResponse<EntityDetail> getEntityDetail(String entityHash) throws VCXException {
        SpaceResponse<EntityDetail> result;
        try {
            result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(PODSPACE_SCHEME)
                            .host(PODSPACE_HOST)
                            .port(PODSPACE_PORT)
                            .path("/api/files/{hash}/detail")
                            .build(entityHash))
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<SpaceResponse<EntityDetail>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<SpaceResponse<EntityDetail>>() {
            });
            throw new VCXException(result.getStatus(), result.getError(), result.getMessage());
        } catch (Exception e) {
            log.error("Unknown error while get entity detail", e);
            throw new VCXException(VCXExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
        }
        return result;
    }


    public void wipeEntity(String entityHash) throws VCXException {
        try {
            webClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(PODSPACE_SCHEME)
                            .host(PODSPACE_HOST)
                            .port(PODSPACE_PORT)
                            .path("/api/trashes/{hash}")
                            .queryParam("force", true)
                            .build(entityHash))
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<String>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            SpaceResponse<Objects> result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<SpaceResponse<Objects>>() {
            });

            throw new VCXException(result.getStatus(), result.getError(), result.getMessage());
        } catch (Exception e) {
            log.error("Unknown error while wipe entity", e);
            throw new VCXException(VCXExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
        }
    }

    public SpaceResponse<Share> publicShareEntity(String entityHash) throws VCXException {
        SpaceResponse<Share> result;
        try {
            result = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(PODSPACE_SCHEME)
                            .host(PODSPACE_HOST)
                            .port(PODSPACE_PORT)
                            .path("/api/files/{hash}/public")
                            .queryParam("expiration", DateUtil.futureTime(DateUtil.TimeInFuture.OneHundredYears).getTime())
                            .queryParam("access", Arrays.asList("DOWNLOAD", "VIEW"))
                            .build(entityHash))
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<SpaceResponse<Share>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<SpaceResponse<Share>>() {
            });
            throw new VCXException(result.getStatus(), result.getError(), result.getMessage());
        } catch (Exception e) {
            log.error("Unknown error while public share entity", e);
            throw new VCXException(VCXExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
        }
        return result;
    }

    public SpaceResponse<DownloadLink> getDownloadLink(String hash) throws VCXException {
        SpaceResponse<DownloadLink> result;
        try {
            result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(PODSPACE_SCHEME)
                            .host(PODSPACE_HOST)
                            .port(PODSPACE_PORT)
                            .path("/api/links/files/{fileHash}")
                            .queryParam("revokeAbility", Boolean.TRUE)
                            .build(hash))
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<SpaceResponse<DownloadLink>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<SpaceResponse<DownloadLink>>() {
            });
            throw new VCXException(result.getStatus(), result.getError(), result.getMessage());
        } catch (Exception e) {
            log.error("Unknown error while get download link", e);
            throw new VCXException(VCXExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
        }
        return result;
    }
}
