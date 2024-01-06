package ir.vcx.data.repository;

import ir.vcx.data.entity.VCXFolder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Created by Sobhan on 11/23/2023 - VCX
 */

@Repository
public class FolderRepository {

    private final SessionFactory sessionFactory;


    @Autowired
    public FolderRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Transactional
    public Optional<VCXFolder> getAvailableFolderByName(String name) {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VF FROM VCXFolder VF " +
                        "WHERE UPPER(VF.name) = :name " +
                        "AND VF.active = :val", VCXFolder.class)
                .setParameter("name", name.toUpperCase())
                .setParameter("val", Boolean.FALSE)
                .uniqueResultOptional();

    }

    @Transactional
    public Optional<VCXFolder> getFolder(String hash) {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VF FROM VCXFolder VF " +
                        "WHERE VF.hash = :hash", VCXFolder.class)
                .setParameter("hash", hash)
                .uniqueResultOptional();

    }

    @Transactional
    public Optional<VCXFolder> getAvailableFolderByName(String name, VCXFolder parent) {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VF FROM VCXFolder VF " +
                        "WHERE UPPER(VF.name) = :name " +
                        "AND VF.parent = :parent", VCXFolder.class)
                .setParameter("name", name.toUpperCase())
                .setParameter("parent", parent)
                .uniqueResultOptional();

    }

    @Transactional
    public VCXFolder addFolder(String name, String hash, VCXFolder parent) {

        Session currentSession = sessionFactory.getCurrentSession();

        VCXFolder vcxFolder = new VCXFolder();
        vcxFolder.setName(name);
        vcxFolder.setHash(hash);
        vcxFolder.setParent(parent);

        currentSession.persist(vcxFolder);

        return vcxFolder;
    }

    public VCXFolder updateFolder(VCXFolder contentFolder) {

        Session currentSession = sessionFactory.getCurrentSession();

        currentSession.merge(contentFolder);

        return contentFolder;
    }
}
