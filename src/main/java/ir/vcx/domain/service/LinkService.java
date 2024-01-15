package ir.vcx.domain.service;

import ir.vcx.data.entity.*;
import ir.vcx.data.repository.LinkRepository;
import ir.vcx.domain.model.space.DownloadLink;
import ir.vcx.domain.model.space.UploadLink;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.request.PodSpaceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Created by Sobhan on 11/9/2023 - VCX
 */

@Service
public class LinkService {

    private final FolderService folderService;
    private final ContentService contentService;
    private final LinkRepository linkRepository;
    private final PodSpaceUtil podSpaceUtil;

    @Autowired
    public LinkService(FolderService folderService, ContentService contentService, LinkRepository linkRepository, PodSpaceUtil podSpaceUtil) {
        this.folderService = folderService;
        this.contentService = contentService;
        this.linkRepository = linkRepository;
        this.podSpaceUtil = podSpaceUtil;
    }

    @Transactional
    public VCXLink getContentUploadLink(String name, Integer season) throws VCXException {

        if (StringUtils.isBlank(name)) {
            throw new VCXException(VCXExceptionStatus.INVALID_NAME_VALUE);
        }

        VideoType videoType;
        if (Optional.ofNullable(season).isPresent()) {
            videoType = VideoType.SERIES;
        } else {
            videoType = VideoType.MOVIES;
        }

        VCXFolder contentFolder = folderService.getOrCreateFolder(name, season, videoType);

        return getVcxUploadLink(contentFolder);
    }

    @Transactional
    public VCXLink getPosterUploadLink(String hash) throws VCXException {
        VCXContent content = contentService.getAvailableContent(hash, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);

        return getVcxUploadLink(content.getParentFolder());
    }

    private VCXLink getVcxUploadLink(VCXFolder contentFolder) {
        return linkRepository.getUploadLink(contentFolder)
                .orElseGet(() -> {
                    try {
                        UploadLink podSpaceUploadLink = podSpaceUtil.getUploadLink(contentFolder.getHash()).getResult();

                        if (!podSpaceUploadLink.getFolderHash().equals(contentFolder.getHash())) {
                            throw new VCXException(VCXExceptionStatus.INVALID_REQUEST);
                        }

                        return linkRepository.addUploadLink(podSpaceUploadLink, contentFolder);
                    } catch (VCXException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Transactional
    public VCXDownloadLink getContentDownloadLink(String hash) throws VCXException {
        VCXContent vcxContent = contentService.getAvailableContent(hash, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);

        return linkRepository.getDownloadLink(vcxContent)
                .orElseGet(() -> {
                    try {
                        DownloadLink downloadLink = podSpaceUtil.getDownloadLink(hash).getResult();

                        return linkRepository.addDownloadLink(downloadLink, vcxContent);
                    } catch (VCXException e) {
                        throw new RuntimeException(e);
                    }

                });
    }
}
