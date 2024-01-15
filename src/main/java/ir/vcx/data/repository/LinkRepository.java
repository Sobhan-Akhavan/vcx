package ir.vcx.data.repository;

import ir.vcx.data.entity.VCXContent;
import ir.vcx.data.entity.VCXDownloadLink;
import ir.vcx.data.entity.VCXFolder;
import ir.vcx.data.entity.VCXLink;
import ir.vcx.domain.model.space.DownloadLink;
import ir.vcx.domain.model.space.UploadLink;
import ir.vcx.util.DateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Sobhan on 11/23/2023 - VCX
 */

@Repository
public class LinkRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public LinkRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Optional<VCXLink> getUploadLink(VCXFolder contentFolder) {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VL FROM VCXLink VL " +
                        "WHERE VL.folder = :folder " +
                        "AND VL.expiration > :exp " +
                        "AND VL.active = :active", VCXLink.class)
                .setParameter("folder", contentFolder)
                .setParameter("exp", DateUtil.getNowDate())
                .setParameter("active", Boolean.TRUE)
                .uniqueResultOptional();
    }

    public VCXLink addUploadLink(UploadLink link, VCXFolder contentFolder) {

        Session currentSession = sessionFactory.getCurrentSession();

        VCXLink vcxLink = new VCXLink();
        vcxLink.setLink(link.getUploadHash());
        vcxLink.setFolder(contentFolder);
        vcxLink.setExpiration(DateUtil.epochToDate(link.getExpiration()));
        vcxLink.setActive(Boolean.TRUE);

        currentSession.persist(vcxLink);

        return vcxLink;
    }

    public Optional<VCXDownloadLink> getDownloadLink(VCXContent vcxContent) {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VDL FROM VCXDownloadLink VDL " +
                        "WHERE VDL.content = :content " +
                        "AND VDL.expiration > :exp " +
                        "AND VDL.active = :active", VCXDownloadLink.class)
                .setParameter("content", vcxContent)
                .setParameter("exp", DateUtil.getNowDate())
                .setParameter("active", Boolean.TRUE)
                .uniqueResultOptional();

    }

    public VCXDownloadLink addDownloadLink(DownloadLink downloadLink, VCXContent vcxContent) {

        Session currentSession = sessionFactory.getCurrentSession();

        VCXDownloadLink vcxDownloadLink = new VCXDownloadLink();
        vcxDownloadLink.setLink(downloadLink.getDownloadLink());
        vcxDownloadLink.setContent(vcxContent);
        vcxDownloadLink.setExpiration(DateUtil.epochToDate(downloadLink.getExpiration()));
        vcxDownloadLink.setActive(Boolean.TRUE);

        currentSession.persist(vcxDownloadLink);

        return vcxDownloadLink;
    }
}
