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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
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

    private final FolderRepository folderRepository;
    private final ContentRepository contentRepository;
    private final PodSpaceUtil podSpaceUtil;
    private final ThreadPoolExecutor threadPoolFactory;

    @Autowired
    public ContentService(FolderRepository folderRepository, ContentRepository contentRepository, PodSpaceUtil podSpaceUtil, ThreadPoolExecutor threadPoolFactory) {
        this.folderRepository = folderRepository;
        this.contentRepository = contentRepository;
        this.podSpaceUtil = podSpaceUtil;
        this.threadPoolFactory = threadPoolFactory;
    }

    @Transactional
    public VCXContent createContent(String file_url, String description, VideoType videoType,
                                    Set<GenreType> genreTypes) throws VCXException {

        EntityDetail fileInfo = podSpaceUtil.uploaded_file_info(file_url)
                .getResult();

        VCXFolder vcxFolder = folderRepository.getFolder(fileInfo.getParentHash())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.NOT_FOUND));

        checkFileOwnerValidation(fileInfo.getHash());

        return contentRepository.addContent(fileInfo.getName(), fileInfo.getHash(), vcxFolder, description, videoType, genreTypes);
    }

    @Transactional
    public VCXContent getAvailableContent(String hash, boolean needGenreType, boolean needPoster, boolean needParentFolder) throws VCXException {

        return contentRepository.getAvailableContent(hash, needGenreType, needPoster, needParentFolder)
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.CONTENT_NOT_FOUND));
    }

    @Transactional
    public VCXContent updateContent(String hash, String name, String description, Set<GenreType> genreTypes) throws VCXException {

        VCXContent vcxContent = getAvailableContent(hash, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);

        if (StringUtils.isNotBlank(name)) {
            vcxContent.setName(name);
        }

        if (StringUtils.isNotBlank(description)) {
            vcxContent.setName(name);
        }

        if (!genreTypes.isEmpty()) {
            vcxContent.setGenresType(genreTypes);
        }


        return contentRepository.updateContent(vcxContent);
    }


    public Pair<List<VCXContent>, Long> getContents(String name, VideoType videoType, Set<GenreType> genreTypes, Paging paging) throws VCXException {

        if (StringUtils.isNotBlank(name) && name.length() < 3) {
            throw new VCXException(VCXExceptionStatus.INVALID_NAME_VALUE_LENGTH);
        }

        Future<List<VCXContent>> getContentsThread = threadPoolFactory.submit(() -> contentRepository.getContents(name, videoType, genreTypes, paging));

        Future<Long> getContentsCountThread = threadPoolFactory.submit(() -> contentRepository.getContentsCount(name, videoType, genreTypes));

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

        checkFileOwnerValidation(posterHash);

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

    private void checkFileOwnerValidation(String hash) throws VCXException {
        EntityDetail entityDetail = podSpaceUtil.getEntityDetail(hash)
                .getResult();

        if (entityDetail.getOwner().getSsoId() != 98878) {
            throw new VCXException(VCXExceptionStatus.UNAUTHORIZED);
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
