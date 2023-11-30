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

/**
 * Created by Sobhan on 11/23/2023 - VCX
 */

@Service
public class FolderService {

    private static final String ROOT_FOLDER_HASH = "ROOT";
    private static final String VIDEOS_FOLDER_NAME = "VIDEOS";
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

        VCXFolder videosFolder = folderRepository.getFolderByName(VIDEOS_FOLDER_NAME)
                .orElseGet(() -> {
                    try {
                        Folder folder = podSpaceUtil.createFolder(VIDEOS_FOLDER_NAME, ROOT_FOLDER_HASH).getResult();

                        return folderRepository.addFolder(folder.getName(), folder.getHash(), null);

                    } catch (VCXException e) {
                        throw new RuntimeException(e);
                    }
                });

        folderRepository.getFolderByName(MOVIES_FOLDER_NAME, videosFolder)
                .orElseGet(() -> {
                    try {
                        Folder folder = podSpaceUtil.createFolder(MOVIES_FOLDER_NAME, videosFolder.getHash()).getResult();

                        return folderRepository.addFolder(folder.getName(), folder.getHash(), videosFolder);
                    } catch (VCXException e) {
                        throw new RuntimeException(e);
                    }
                });

        folderRepository.getFolderByName(SERIES_FOLDER_NAME, videosFolder)
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

        VCXFolder primaryFolder = folderRepository.getFolderByName(videoType.name())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNKNOWN_ERROR));

        VCXFolder contentFolder = folderRepository.getFolderByName(name, primaryFolder)
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

            return folderRepository.getFolderByName(String.valueOf(season), contentFolder)
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
}
