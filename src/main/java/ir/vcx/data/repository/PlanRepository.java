package ir.vcx.data.repository;

import ir.vcx.data.entity.VCXPlan;
import ir.vcx.util.StringUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class PlanRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public PlanRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public List<VCXPlan> getPlansList() {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VP FROM VCXPlan VP " +
                        "WHERE VP.active = :active", VCXPlan.class)
                .setParameter("active", Boolean.TRUE)
                .getResultList();
    }

    @Transactional
    public Long getPlansCount() {
        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT COUNT(VP) FROM VCXPlan VP " +
                        "WHERE VP.active = :active", Long.class)
                .setParameter("active", Boolean.TRUE)
                .getSingleResult();
    }

    public VCXPlan addPlan(String name, long price, VCXPlan.DaysLimit limit, boolean active) {
        Session currentSession = sessionFactory.getCurrentSession();

        VCXPlan vcxPlan = new VCXPlan();
        vcxPlan.setName(name);
        vcxPlan.setHash(StringUtil.generateHash(4).toUpperCase());
        vcxPlan.setPrice(price);
        vcxPlan.setDaysLimit(limit);
        vcxPlan.setActive(active);

        currentSession.persist(vcxPlan);

        return vcxPlan;
    }

    public Optional<VCXPlan> getActivePlanByLimit(VCXPlan.DaysLimit daysLimit) {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VP FROM VCXPlan VP " +
                        "WHERE VP.daysLimit = :limit " +
                        "AND VP.active = :active", VCXPlan.class)
                .setParameter("active", Boolean.TRUE)
                .setParameter("limit", daysLimit)
                .uniqueResultOptional();

    }

    public int deactivatePlans() {
        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("UPDATE VCXPlan VP " +
                        "SET VP.active = :active", Integer.class)
                .setParameter("active", Boolean.FALSE)
                .executeUpdate();

    }

    public int deleteAllPlans() {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("DELETE FROM VCXPlan", Integer.class)
                .executeUpdate();

    }

    public Optional<VCXPlan> getPlan(String hash) {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VP FROM VCXPlan VP " +
                        "WHERE VP.hash = :hash " +
                        "AND VP.active = :active", VCXPlan.class)
                .setParameter("hash", hash)
                .setParameter("active", Boolean.TRUE)
                .uniqueResultOptional();

    }

    public VCXPlan updatePlan(VCXPlan vcxPlan) {

        Session currentSession = sessionFactory.getCurrentSession();

        currentSession.merge(vcxPlan);

        return vcxPlan;
    }
}
