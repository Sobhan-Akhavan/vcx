package ir.vcx.domain.service;

import ir.vcx.api.model.Paging;
import ir.vcx.data.entity.*;
import ir.vcx.data.repository.ContentRepository;
import ir.vcx.data.repository.FolderRepository;
import ir.vcx.domain.model.space.EntityDetail;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.request.PodSpaceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Sobhan on 11/30/2023 - VCX
 */

@Slf4j
@Service
public class ContentService {

    @Value("${service.pod.sso.id}")
    private int VCX_SSO_ID;

    private final FolderRepository folderRepository;
    private final ContentRepository contentRepository;
    private final PodSpaceUtil podSpaceUtil;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    public ContentService(FolderRepository folderRepository, ContentRepository contentRepository, PodSpaceUtil podSpaceUtil,
                          @Qualifier("contentThreadPool") ThreadPoolExecutor threadPoolExecutor) {
        this.folderRepository = folderRepository;
        this.contentRepository = contentRepository;
        this.podSpaceUtil = podSpaceUtil;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Transactional
    public VCXContent createContent(String file_url, String description, String contentName, VideoType videoType,
                                    Set<GenreType> genreTypes) throws VCXException {

        EntityDetail fileInfo = podSpaceUtil.uploaded_file_info(file_url)
                .getResult();

        checkEntityOwnerValidation(fileInfo);
        checkVideoTypeValidation(fileInfo);

        if (StringUtils.isNotBlank(contentName) && !Objects.equals(fileInfo.getName(), contentName)) {
            fileInfo = podSpaceUtil.renameEntity(fileInfo.getHash(), contentName)
                    .getResult();
        }

        VCXFolder vcxFolder = folderRepository.getFolder(fileInfo.getParentHash())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.NOT_FOUND));

        return contentRepository.addContent(fileInfo.getName(), fileInfo.getHash(), vcxFolder, description, videoType, genreTypes);
    }

    @Transactional
    public VCXContent getAvailableContent(String hash, boolean needGenreType, boolean needPoster, boolean needParentFolder) throws VCXException {

        return contentRepository.getAvailableContent(hash, needGenreType, needPoster, needParentFolder)
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.CONTENT_NOT_FOUND));
    }

    @Transactional
    public VCXContent updateContent(String hash, String name, String description, Set<GenreType> genreTypes) throws VCXException {

        VCXContent vcxContent = getAvailableContent(hash, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);

        if (StringUtils.isNotBlank(name) && !Objects.equals(vcxContent.getName(), name)) {
            EntityDetail fileEntity = podSpaceUtil.renameEntity(vcxContent.getHash(), name).getResult();
            vcxContent.setName(fileEntity.getName());
        }

        if (StringUtils.isNotBlank(description) && !Objects.equals(vcxContent.getDescription(), description)) {
            vcxContent.setDescription(description);
        }

        if (genreTypes != null && !genreTypes.isEmpty() && !Objects.equals(vcxContent.getGenresType(), genreTypes)) {
            vcxContent.setGenresType(genreTypes);
        }


        return contentRepository.updateContent(vcxContent);
    }


    public Pair<List<VCXContent>, Long> getContents(String name, VideoType videoType, Set<GenreType> genreTypes,
                                                    boolean includePosterLessContent, Paging paging) throws VCXException {

        if (StringUtils.isNotBlank(name) && name.length() < 3) {
            throw new VCXException(VCXExceptionStatus.INVALID_NAME_VALUE_LENGTH);
        }

        Future<List<VCXContent>> getContentsThread = threadPoolExecutor.submit(() ->
                contentRepository.getContents(name, videoType, genreTypes, includePosterLessContent, paging));

        Future<Long> getContentsCountThread = threadPoolExecutor.submit(() ->
                contentRepository.getContentsCount(name, videoType, genreTypes, includePosterLessContent));

        try {
            List<VCXContent> contents = getContentsThread.get();
            Long contentsCount = getContentsCountThread.get();

            return Pair.of(contents, contentsCount);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();

            throw new VCXException(VCXExceptionStatus.UNKNOWN_ERROR);
        }

    }

    @Transactional
    public VCXPoster addPoster(String hash, String posterHash, boolean horizontal) throws VCXException {
        VCXContent content = getAvailableContent(hash, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);

        EntityDetail imageInfo = podSpaceUtil.getEntityDetail(hash)
                .getResult();

        checkEntityOwnerValidation(imageInfo);
        checkImageTypeValidation(imageInfo);

        Set<VCXPoster> posters = content.getPosters();

        if (posters.stream().anyMatch(vcxPoster -> vcxPoster.getPosterHash().equals(posterHash))) {
            throw new VCXException(VCXExceptionStatus.POSTER_HASH_EXIST);
        }

        VCXPoster vcxPoster = contentRepository.addPoster(posterHash, horizontal);

        posters.add(vcxPoster);

        podSpaceUtil.publicShareEntity(posterHash);

        contentRepository.updateContent(content);

        return vcxPoster;
    }

    private void checkEntityOwnerValidation(EntityDetail entity) throws VCXException {
        if (entity.getOwner().getSsoId() != VCX_SSO_ID) {
            throw new VCXException(VCXExceptionStatus.INVALID_ENTITY_OWNER);
        }
    }

    private void checkVideoTypeValidation(EntityDetail videoInfo) throws VCXException {
        if (!videoInfo.getType().equalsIgnoreCase("video/mp4")) {
            throw new VCXException(VCXExceptionStatus.INVALID_VIDEO_TYPE);
        }
    }

    private void checkImageTypeValidation(EntityDetail imageInfo) throws VCXException {
        if (!imageInfo.getType().equalsIgnoreCase("image/jpeg")) {
            throw new VCXException(VCXExceptionStatus.INVALID_IMAGE_TYPE);
        }
    }

    @Transactional
    public VCXContent deleteContent(String hash) throws VCXException {
        VCXContent vcxContent = getAvailableContent(hash, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);

        podSpaceUtil.wipeEntity(vcxContent.getHash());

        vcxContent.setActive(Boolean.FALSE);

        return contentRepository.updateContent(vcxContent);
    }
}
