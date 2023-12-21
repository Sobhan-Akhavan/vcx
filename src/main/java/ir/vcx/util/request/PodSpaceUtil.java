package ir.vcx.util.request;

import com.fasterxml.jackson.core.type.TypeReference;
import ir.vcx.domain.model.space.EntityDetail;
import ir.vcx.domain.model.space.Folder;
import ir.vcx.domain.model.space.SpaceResponse;
import ir.vcx.domain.model.space.UploadLink;
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


//    public void wipeEntity(String entityHash) throws VCXException {
//        try {
//            webClient.delete()
//                    .uri(uriBuilder -> uriBuilder
//                            .scheme(PODSPACE_SCHEME)
//                            .host(PODSPACE_HOST)
//                            .port(PODSPACE_PORT)
//                            .path("/api/trashes/{hash}")
//                            .queryParam("force", true)
//                            .build(entityHash))
//                    .header("Authorization", "Bearer " + API_TOKEN)
//                    .retrieve()
//                    .bodyToMono(new ParameterizedTypeReference<>() {
//                    })
//                    .block();
//        } catch (WebClientResponseException e) {
//            VCXException<Object> result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<>() {
//            });
//            throw new VCXException(result.getStatus(), result.getError(), result.getMessage());
//        } catch (Exception e) {
//            log.error("Unknown error while wipe entity", e);
//            throw new VCXException(VCXExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
//        }
//    }
//
//    public SpaceResponse<UserGroup> createUserGroup(Set<Long> ssoId, String destFolderHash) throws ArchiveException {
//        SpaceResponse<UserGroup> result;
//        try {
//            result = webClient.post()
//                    .uri(uriBuilder -> uriBuilder
//                            .scheme(PODSPACE_SCHEME)
//                            .host(PODSPACE_HOST)
//                            .port(PODSPACE_PORT)
//                            .path("/api/usergroups")
//                            .queryParam("ssoId", ssoId)
//                            .queryParam("destFolderHash", destFolderHash)
//                            .queryParam("allowMemberShare", "false")
//                            .build())
//                    .header("Authorization", "Bearer " + API_TOKEN)
//                    .retrieve()
//                    .bodyToMono(new ParameterizedTypeReference<SpaceResponse<UserGroup>>() {
//                    })
//                    .block();
//        } catch (WebClientResponseException e) {
//            result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<>() {
//            });
//            throw new ArchiveException(result.getStatus(), result.getError(), result.getMessage());
//        } catch (Exception e) {
//            log.error("Unknown error while create userGroup", e);
//            throw new ArchiveException(ArchiveExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
//        }
//        return result;
//    }
//
//    public SpaceResponse<UserGroup> getUserGroup(String userGroupHash) throws ArchiveException {
//        SpaceResponse<UserGroup> result;
//        try {
//            result = webClient.post()
//                    .uri(uriBuilder -> uriBuilder
//                            .scheme(PODSPACE_SCHEME)
//                            .host(PODSPACE_HOST)
//                            .port(PODSPACE_PORT)
//                            .path("/api/usergroups/{userGroupHash}")
//                            .build(userGroupHash))
//                    .header("Authorization", "Bearer " + API_TOKEN)
//                    .retrieve()
//                    .bodyToMono(new ParameterizedTypeReference<SpaceResponse<UserGroup>>() {
//                    })
//                    .block();
//        } catch (WebClientResponseException e) {
//            result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<>() {
//            });
//            throw new ArchiveException(result.getStatus(), result.getError(), result.getMessage());
//        } catch (Exception e) {
//            log.error("Unknown error while get userGroup", e);
//            throw new ArchiveException(ArchiveExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
//        }
//        return result;
//    }
//
//    public SpaceResponse<DownloadLinkInfo> shareEntityToUserGroup(String userGroupHash, String fileHash) throws ArchiveException {
//        SpaceResponse<DownloadLinkInfo> result;
//        try {
//            result = webClient.put()
//                    .uri(uriBuilder -> uriBuilder
//                            .scheme(PODSPACE_SCHEME)
//                            .host(PODSPACE_HOST)
//                            .port(PODSPACE_PORT)
//                            .path("/api/usergroups/{userGroupHash}/files/{fileHash}/share")
//                            .build(userGroupHash, fileHash))
//                    .header("Authorization", "Bearer " + API_TOKEN)
//                    .retrieve()
//                    .bodyToMono(new ParameterizedTypeReference<SpaceResponse<DownloadLinkInfo>>() {
//                    })
//                    .block();
//        } catch (WebClientResponseException e) {
//            result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<>() {
//            });
//            throw new ArchiveException(result.getStatus(), result.getError(), result.getMessage());
//        } catch (Exception e) {
//            log.error("Unknown error while share file to userGroup", e);
//            throw new ArchiveException(ArchiveExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
//        }
//        return result;
//    }
//
//    public void addUserToUserGroup(String userGroupHash, long ssoId) throws ArchiveException {
//        SpaceResponse<Object> result;
//        try {
//            webClient.post()
//                    .uri(uriBuilder -> uriBuilder
//                            .scheme(PODSPACE_SCHEME)
//                            .host(PODSPACE_HOST)
//                            .port(PODSPACE_PORT)
//                            .path("/api/usergroups/{userGroupHash}/users")
//                            .queryParam("ssoId", ssoId)
//                            .build(userGroupHash))
//                    .header("Authorization", "Bearer " + API_TOKEN)
//                    .retrieve()
//                    .bodyToMono(new ParameterizedTypeReference<SpaceResponse<Object>>() {
//                    })
//                    .block();
//        } catch (WebClientResponseException e) {
//            result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<>() {
//            });
//            throw new ArchiveException(result.getStatus(), result.getError(), result.getMessage());
//        } catch (Exception e) {
//            log.error("Unknown error while add user to userGroup", e);
//            throw new ArchiveException(ArchiveExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
//        }
//    }
//
//    public void removeUserGroup(String userGroupHash) throws ArchiveException {
//        SpaceResponse<Object> result;
//        try {
//            webClient.delete()
//                    .uri(uriBuilder -> uriBuilder
//                            .scheme(PODSPACE_SCHEME)
//                            .host(PODSPACE_HOST)
//                            .port(PODSPACE_PORT)
//                            .path("/api/usergroups/{userGroupHash}")
//                            .build(userGroupHash))
//                    .header("Authorization", "Bearer " + API_TOKEN)
//                    .retrieve()
//                    .bodyToMono(new ParameterizedTypeReference<SpaceResponse<Object>>() {
//                    })
//                    .block();
//        } catch (WebClientResponseException e) {
//            result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<>() {
//            });
//            throw new ArchiveException(result.getStatus(), result.getError(), result.getMessage());
//        } catch (Exception e) {
//            log.error("Unknown error while delete userGroup", e);
//            throw new ArchiveException(ArchiveExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
//        }
//    }
//
//    public void removeUserFromUserGroup(String userGroupHash, long ssoId) throws ArchiveException {
//        SpaceResponse<Object> result;
//        try {
//            webClient.delete()
//                    .uri(uriBuilder -> uriBuilder
//                            .scheme(PODSPACE_SCHEME)
//                            .host(PODSPACE_HOST)
//                            .port(PODSPACE_PORT)
//                            .path("/api/usergroups/{userGroupHash}/users")
//                            .queryParam("ssoId", ssoId)
//                            .build(userGroupHash))
//                    .header("Authorization", "Bearer " + API_TOKEN)
//                    .retrieve()
//                    .bodyToMono(new ParameterizedTypeReference<SpaceResponse<Object>>() {
//                    })
//                    .block();
//        } catch (WebClientResponseException e) {
//            result = JsonUtil.getObject(e.getResponseBodyAsString(), new TypeReference<>() {
//            });
//            throw new ArchiveException(result.getStatus(), result.getError(), result.getMessage());
//        } catch (Exception e) {
//            log.error("Unknown error while remove user from userGroup", e);
//            throw new ArchiveException(ArchiveExceptionStatus.PODSPACE_REQUEST_CALL_ERROR);
//        }
//    }
//

}
