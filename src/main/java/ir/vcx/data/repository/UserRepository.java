package ir.vcx.data.repository;

import ir.vcx.data.entity.VCXPlan;
import ir.vcx.data.entity.VCXUser;
import ir.vcx.data.entity.VCXUserLimit;
import ir.vcx.data.entity.VCXUserPayment;
import ir.vcx.util.DateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

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

    public Optional<VCXPlan> getActiveUserPlan(VCXUser vcxUser) {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VUL FROM VCXUserLimit VUL " +
                        "WHERE VUL.user = :user " +
                        "AND VUL.expiration > :exp " +
                        "AND VUL.active = :active", VCXPlan.class)
                .setParameter("user", vcxUser)
                .setParameter("exp", DateUtil.getNowDate())
                .setParameter("active", Boolean.TRUE)
                .setMaxResults(1)
                .uniqueResultOptional();

    }

    public VCXUserLimit setUserPlan(VCXUser vcxUser, VCXPlan vcxPlan, Date expirationDate) {

        Session currentSession = sessionFactory.getCurrentSession();

        VCXUserLimit vcxUserLimit = new VCXUserLimit();
        vcxUserLimit.setUser(vcxUser);
        vcxUserLimit.setPlan(vcxPlan);
        vcxUserLimit.setExpiration(expirationDate);
        vcxUserLimit.setActive(Boolean.TRUE);

        currentSession.persist(vcxUserLimit);

        return vcxUserLimit;

    }

    public VCXUserPayment saveUserPayment(VCXUserLimit vcxUserLimit, String trackingNumber) {

        Session currentSession = sessionFactory.getCurrentSession();

        VCXUserPayment vcxUserPayment = new VCXUserPayment();
        vcxUserPayment.setUserLimit(vcxUserLimit);
        vcxUserPayment.setTrackingNumber(trackingNumber);

        currentSession.persist(vcxUserPayment);

        return vcxUserPayment;
    }
}
