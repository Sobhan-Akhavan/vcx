package ir.vcx.domain.service;

import ir.vcx.data.entity.GenreType;
import ir.vcx.data.entity.VCXContent;
import ir.vcx.data.entity.VCXFolder;
import ir.vcx.data.entity.VideoType;
import ir.vcx.data.repository.ContentRepository;
import ir.vcx.data.repository.FolderRepository;
import ir.vcx.domain.model.space.EntityDetail;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.request.PodSpaceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

/**
 * Created by Sobhan on 11/30/2023 - VCX
 */

@Slf4j
@Service
public class ContentService {

    private final FolderRepository folderRepository;
    private final ContentRepository contentRepository;
    private final PodSpaceUtil podSpaceUtil;

    @Autowired
    public ContentService(FolderRepository folderRepository, ContentRepository contentRepository, PodSpaceUtil podSpaceUtil) {
        this.folderRepository = folderRepository;
        this.contentRepository = contentRepository;
        this.podSpaceUtil = podSpaceUtil;
    }

    @Transactional
    public VCXContent createContent(String name, String hash, String parentHash, String description, VideoType videoType,
                                    Set<GenreType> genreTypes) throws VCXException {

        VCXFolder vcxFolder = folderRepository.getFolder(parentHash)
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.NOT_FOUND));

        EntityDetail entityDetail = podSpaceUtil.getEntityDetail(hash).getResult();

        if (entityDetail.getOwner().getSsoId() != 98878) {
            throw new VCXException(VCXExceptionStatus.UNAUTHORIZED);
        }


        return contentRepository.addContent(name, hash, vcxFolder, description, videoType, genreTypes);
    }

    @Transactional
    public VCXContent getContent(String hash) throws VCXException {

        return contentRepository.getContent(hash)
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.NOT_FOUND));
    }

    @Transactional
    public VCXContent updateContent(String hash, String name, String description, Set<GenreType> genreTypes) throws VCXException {

        VCXContent vcxContent = getContent(hash);

        if (StringUtils.isNotBlank(name)) {
            vcxContent.setName(name);
        }

        if (StringUtils.isNotBlank(description)) {
            vcxContent.setName(name);
        }

        if (!genreTypes.isEmpty()) {
            vcxContent.setGenreType(genreTypes);
        }


        return contentRepository.updateContent(vcxContent);
    }
}
