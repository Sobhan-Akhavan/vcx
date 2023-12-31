package ir.vcx.data.repository;

import ir.vcx.data.entity.VCXUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Sobhan on 11/16/2023 - VCX
 */


@Repository
public class UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public VCXUser getUserBySsoId(long ssoId) {

        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("SELECT VU FROM VCXUser VU " +
                        "WHERE VU.ssoId = :id", VCXUser.class)
                .setParameter("id", ssoId)
                .uniqueResult();
    }

    public VCXUser addUser(long ssoId, String preferredUsername, String userFullName, String picture) {
        Session session = sessionFactory.getCurrentSession();

        VCXUser vcxUser = new VCXUser();
        vcxUser.setSsoId(ssoId);
        vcxUser.setUsername(preferredUsername);
        vcxUser.setName(userFullName);
        vcxUser.setAvatar(picture);

        session.persist(vcxUser);

        return vcxUser;
    }

    public VCXUser updateUser(VCXUser vcxUser) {
        Session session = sessionFactory.getCurrentSession();

        return (VCXUser) session.merge(vcxUser);
    }
}
