package ir.vcx.domain.service;

import ir.vcx.api.model.Order;
import ir.vcx.api.model.Paging;
import ir.vcx.data.entity.*;
import ir.vcx.data.repository.ContentRepository;
import ir.vcx.data.repository.FolderRepository;
import ir.vcx.domain.model.ContentsReport;
import ir.vcx.domain.model.GenreTypeReport;
import ir.vcx.domain.model.VideoTypeReport;
import ir.vcx.domain.model.space.EntityDetail;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.EncryptionUtil;
import ir.vcx.util.LimitUtil;
import ir.vcx.util.request.PodSpaceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.net.URLEncoder;
import java.util.Arrays;
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

    @Transactional(rollbackOn = Exception.class)
    public VCXContent createContent(String file_url, String contentName, String description, VideoType videoType,
                                    Set<GenreType> genreTypes) throws Exception {

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

        VCXContent vcxContent = contentRepository.addContent(fileInfo.getName(), fileInfo.getHash(), vcxFolder, description, videoType, genreTypes);

        contentRepository.addFirstVisitedCount(vcxContent);

        byte[] bytes = podSpaceUtil.downloadFile(vcxContent.getHash());

        contentRepository.saveContentByte(EncryptionUtil.encryptData(bytes), vcxContent);

        return vcxContent;
    }

    @Transactional
    public VCXContent getAvailableContent(String hash, boolean needGenreType, boolean needParentFolder) throws VCXException {

        return contentRepository.getAvailableContent(hash, needGenreType, needParentFolder)
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.CONTENT_NOT_FOUND));
    }

    @Transactional
    public VCXContent updateContent(String hash, String name, String description, Set<GenreType> genreTypes) throws VCXException {

        VCXContent vcxContent = getAvailableContent(hash, Boolean.TRUE, Boolean.FALSE);

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


    public Pair<List<VCXContent>, Long> searchOnContents(String name, VideoType videoType, Set<GenreType> genreTypes,
                                                         boolean includePosterLessContent, Paging paging) throws VCXException {


        LimitUtil.validateInput(Arrays.asList(Order.CREATED, Order.UPDATED), paging.getOrder());


        Future<List<VCXContent>> getContentThread = threadPoolExecutor.submit(() ->
                contentRepository.getContents(name, videoType, genreTypes, includePosterLessContent, paging));

        Future<Long> getContentCountThread = threadPoolExecutor.submit(() ->
                contentRepository.getContentsCount(name, videoType, genreTypes, includePosterLessContent));

        try {
            List<VCXContent> contents = getContentThread.get();
            Long contentsCount = getContentCountThread.get();

            return Pair.of(contents, contentsCount);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();

            throw new VCXException(VCXExceptionStatus.UNKNOWN_ERROR);
        }

    }

    @Transactional
    public VCXContent addPoster(String hash, String posterHash, boolean horizontal) throws VCXException {
        VCXContent content = getAvailableContent(hash, Boolean.FALSE, Boolean.FALSE);

        EntityDetail imageInfo = podSpaceUtil.getEntityDetail(posterHash)
                .getResult();

        checkEntityOwnerValidation(imageInfo);
        checkImageTypeValidation(imageInfo);

        String horizontalPoster = content.getHorizontalPoster();
        String verticalPoster = content.getVerticalPoster();

        if (posterHash.equals(horizontalPoster) || posterHash.equals(verticalPoster)) {
            throw new VCXException(VCXExceptionStatus.POSTER_HASH_EXIST);
        }

        String posterToWipe = horizontal ? horizontalPoster : verticalPoster;
        if (StringUtils.isNotBlank(posterToWipe)) {
            podSpaceUtil.wipeEntity(posterToWipe);
        }

        if (horizontal) {
            content.setHorizontalPoster(imageInfo.getHash());
        } else {
            content.setVerticalPoster(imageInfo.getHash());
        }

        podSpaceUtil.publicShareEntity(posterHash);

        return contentRepository.updateContent(content);
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
    public void deleteContent(String hash) throws VCXException {
        VCXContent vcxContent = getAvailableContent(hash, Boolean.FALSE, Boolean.FALSE);

        podSpaceUtil.wipeEntity(vcxContent.getHash());

        vcxContent.setActive(Boolean.FALSE);

        contentRepository.updateContent(vcxContent);
    }


    @Async
    public void incrementViewCount(VCXContent vcxContent) {
        contentRepository.incrementViewCount(vcxContent);
    }

    public ContentsReport contentsReport() throws VCXException {

        Future<List<GenreTypeReport>> mostGenreTypesThread = threadPoolExecutor.submit(() -> {
            List<GenreTypeReport> mostGenreTypes = contentRepository.getMostGenreTypes();

            long totalCount = mostGenreTypes.stream().mapToLong(GenreTypeReport::getCount).sum();

            mostGenreTypes.forEach(genreTypeReport -> genreTypeReport.setPercent(((float) genreTypeReport.getCount() / totalCount) * 100));

            return mostGenreTypes;
        });

        Future<Long> movieContentsCountThread = threadPoolExecutor.submit(() ->
                contentRepository.getContentTypeCount(VideoType.MOVIES));

        Future<Long> seriesContentsCountThread = threadPoolExecutor.submit(() ->
                contentRepository.getContentTypeCount(VideoType.SERIES));

        Future<Long> contentsCountThread = threadPoolExecutor.submit(() ->
                contentRepository.getContentsCount(null, null, null, Boolean.TRUE));

        try {
            List<GenreTypeReport> genreTypeReports = mostGenreTypesThread.get();
            Long moviesCount = movieContentsCountThread.get();
            Long seriesCount = seriesContentsCountThread.get();
            Long contentsCount = contentsCountThread.get();

            VideoTypeReport moviesVideoTypeReport = new VideoTypeReport(VideoType.MOVIES, moviesCount, ((float) moviesCount / contentsCount) * 100);
            VideoTypeReport seriestVideoTypeReport = new VideoTypeReport(VideoType.SERIES, seriesCount, ((float) seriesCount / contentsCount) * 100);

            return new ContentsReport(Arrays.asList(moviesVideoTypeReport, seriestVideoTypeReport), genreTypeReports, contentsCount);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();

            throw new VCXException(VCXExceptionStatus.UNKNOWN_ERROR);
        }
    }

    public Pair<List<VCXContentVisit>, Long> mostVisitedContent(Paging paging) throws VCXException {
        LimitUtil.validateInput(Arrays.asList(Order.NAME, Order.COUNT), paging.getOrder());

        Future<List<VCXContentVisit>> mostVisitedContentThread = threadPoolExecutor.submit(() ->
                contentRepository.mostVisitedContent(paging));

        Future<Long> mostVisitedContentCountThread = threadPoolExecutor.submit(contentRepository::mostVisitedVideoCount);

        try {
            List<VCXContentVisit> visitedContents = mostVisitedContentThread.get();
            Long visitedContentsCount = mostVisitedContentCountThread.get();

            return Pair.of(visitedContents, visitedContentsCount);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();

            throw new VCXException(VCXExceptionStatus.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public VCXContentVisit visitedContent(String hash) throws VCXException {

        VCXContent vcxContent = getAvailableContent(hash, Boolean.TRUE, Boolean.FALSE);

        return contentRepository.getContentVisited(vcxContent);
    }

    @Transactional
    public byte[] getContentsBytes(String hash, HttpServletResponse response) throws Exception {
        VCXContent vcxContent = getAvailableContent(hash, Boolean.TRUE, Boolean.FALSE);

        VCXFileEntity contentsBytes = contentRepository.getContentsBytes(vcxContent);

        byte[] data = EncryptionUtil.decryptData(contentsBytes.getData());

        response.setContentType("video/mp4");
        response.setContentLength(data.length);
        String contentDisposition = "attachment;" + "filename=\"" + URLEncoder.encode(vcxContent.getName() + ".mp4", "UTF-8").replace('+', ' ') + "\"";
        response.setHeader("Content-Disposition", contentDisposition);


        return data;
    }
}
