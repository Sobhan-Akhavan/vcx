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
 * Created by Sobhan Akhavan on 1/28/2024 - vcx
 */

@Repository
public class UserLimitRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserLimitRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Optional<VCXUserLimit> getActiveUserPlan(VCXUser vcxUser) {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VUL FROM VCXUserLimit VUL " +
                        "WHERE VUL.user = :user " +
                        "AND VUL.expiration > :exp " +
                        "AND VUL.active = :active", VCXUserLimit.class)
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

    public Long anyPlanUsed() {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT COUNT(VUL) FROM VCXUserLimit VUL", Long.class)
                .getSingleResult();
    }

    public Optional<VCXUserLimit> isPlanUsed(VCXPlan vcxPlan) {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VUL FROM VCXUserLimit VUL " +
                        "WHERE VUL.plan = :plan", VCXUserLimit.class)
                .setParameter("plan", vcxPlan)
                .uniqueResultOptional();

    }
}
