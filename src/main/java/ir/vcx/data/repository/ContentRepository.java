package ir.vcx.data.repository;

import ir.vcx.data.entity.GenreType;
import ir.vcx.data.entity.VCXContent;
import ir.vcx.data.entity.VCXFolder;
import ir.vcx.data.entity.VideoType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Sobhan on 11/30/2023 - VCX
 */

@Repository
public class ContentRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public ContentRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public VCXContent addContent(String name, String hash, VCXFolder parentFolder, String description, VideoType videoType,
                                 Set<GenreType> genreTypes) {

        Session currentSession = sessionFactory.getCurrentSession();

        VCXContent vcxContent = new VCXContent();
        vcxContent.setName(name);
        vcxContent.setHash(hash);
        vcxContent.setParentFolder(parentFolder);
        vcxContent.setDescription(description);
        vcxContent.setVideoType(videoType);
        vcxContent.setGenreType(genreTypes);

        currentSession.persist(vcxContent);

        return vcxContent;
    }

    public Optional<VCXContent> getContent(String hash) {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VC FROM VCXContent VC " +
                        "WHERE VC.hash = :hash", VCXContent.class)
                .setParameter("hash", hash)
                .uniqueResultOptional();

    }

    public VCXContent updateContent(VCXContent vcxContent) {

        Session currentSession = sessionFactory.getCurrentSession();

        currentSession.merge(vcxContent);

        return vcxContent;

    }
}
