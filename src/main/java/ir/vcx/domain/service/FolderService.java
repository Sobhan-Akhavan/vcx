package ir.vcx.domain.service;

import ir.vcx.data.entity.VCXFolder;
import ir.vcx.data.entity.VideoType;
import ir.vcx.data.repository.FolderRepository;
import ir.vcx.domain.model.space.Folder;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.request.PodSpaceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Created by Sobhan on 11/23/2023 - VCX
 */

@Service
public class FolderService {

    private static final String ROOT_FOLDER_HASH = "ROOT";
    private static final String VIDEOS_FOLDER_NAME = "VIDEOS (1)";
    private static final String MOVIES_FOLDER_NAME = "MOVIES";
    private static final String SERIES_FOLDER_NAME = "SERIES";

    private final FolderRepository folderRepository;
    private final PodSpaceUtil podSpaceUtil;

    @Autowired
    public FolderService(FolderRepository folderRepository, PodSpaceUtil podSpaceUtil) {
        this.folderRepository = folderRepository;
        this.podSpaceUtil = podSpaceUtil;
    }

    @PostConstruct
    public void checkPrimaryFolders() {

        VCXFolder videosFolder = folderRepository.getAvailableFolderByName(VIDEOS_FOLDER_NAME)
                .orElseGet(() -> {
                    try {
                        Folder folder = podSpaceUtil.createFolder(VIDEOS_FOLDER_NAME, ROOT_FOLDER_HASH).getResult();

                        return folderRepository.addFolder(folder.getName(), folder.getHash(), null);

                    } catch (VCXException e) {
                        throw new RuntimeException(e);
                    }
                });

        folderRepository.getAvailableFolderByName(MOVIES_FOLDER_NAME, videosFolder)
                .orElseGet(() -> {
                    try {
                        Folder folder = podSpaceUtil.createFolder(MOVIES_FOLDER_NAME, videosFolder.getHash()).getResult();

                        return folderRepository.addFolder(folder.getName(), folder.getHash(), videosFolder);
                    } catch (VCXException e) {
                        throw new RuntimeException(e);
                    }
                });

        folderRepository.getAvailableFolderByName(SERIES_FOLDER_NAME, videosFolder)
                .orElseGet(() -> {
                    try {
                        Folder folder = podSpaceUtil.createFolder(SERIES_FOLDER_NAME, videosFolder.getHash()).getResult();

                        return folderRepository.addFolder(folder.getName(), folder.getHash(), videosFolder);
                    } catch (VCXException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public VCXFolder getOrCreateFolder(String name, Integer season, VideoType videoType) throws VCXException {

        VCXFolder primaryFolder = folderRepository.getAvailableFolderByName(videoType.name())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.FOLDER_NOT_FOUND));

        VCXFolder contentFolder = folderRepository.getAvailableFolderByName(name, primaryFolder)
                .orElseGet(() -> {
                    try {
                        Folder podSpaceContentFolder = podSpaceUtil.createFolder(name, primaryFolder.getHash()).getResult();

                        return folderRepository.addFolder(podSpaceContentFolder.getName(),
                                podSpaceContentFolder.getHash(), primaryFolder);

                    } catch (VCXException e) {
                        throw new RuntimeException(e);
                    }
                });

        if (videoType.equals(VideoType.SERIES)) {

            return folderRepository.getAvailableFolderByName(String.valueOf(season), contentFolder)
                    .orElseGet(() -> {
                        try {
                            Folder podSpaceNestedSeriesFolder = podSpaceUtil.createFolder(String.valueOf(season),
                                    contentFolder.getHash()).getResult();

                            return folderRepository.addFolder(podSpaceNestedSeriesFolder.getName(),
                                    podSpaceNestedSeriesFolder.getHash(), contentFolder);

                        } catch (VCXException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        return contentFolder;
    }

    @Transactional
    public VCXFolder deleteFolder(String name, Integer season) throws VCXException {

        VCXFolder vcxFolder;

        vcxFolder = folderRepository.getAvailableFolderByName(name.toUpperCase())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.FOLDER_NOT_FOUND));

        if (Optional.ofNullable(season).isPresent()) {
            vcxFolder = folderRepository.getAvailableFolderByName(String.valueOf(season), vcxFolder)
                    .orElseThrow(() -> new VCXException(VCXExceptionStatus.FOLDER_NOT_FOUND));

        }

        podSpaceUtil.wipeEntity(vcxFolder.getHash());

        vcxFolder.setActive(Boolean.FALSE);

        return folderRepository.updateFolder(vcxFolder);
    }
}
