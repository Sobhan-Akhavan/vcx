package ir.vcx.data.repository;

import ir.vcx.data.entity.VCXPlan;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

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

        return currentSession.createQuery("SELECT VP FROM VCXPlan VP", VCXPlan.class)
                .getResultList();
    }

    @Transactional
    public Long getPlansCount() {
        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT COUNT(VP) FROM VCXPlan VP", Long.class)
                .getSingleResult();
    }

    public VCXPlan addPlan(String name, VCXPlan.MonthLimit limit, boolean active) {
        Session currentSession = sessionFactory.getCurrentSession();

        byte[] bytes = new byte[8];
        new Random().nextBytes(bytes);

        VCXPlan vcxPlan = new VCXPlan();
        vcxPlan.setName(name);
        vcxPlan.setHash(new String(bytes, StandardCharsets.UTF_8).toUpperCase());
        vcxPlan.setLimit(limit);
        vcxPlan.setActive(active);

        currentSession.persist(vcxPlan);

        return vcxPlan;
    }

    public void getPlanByName(String name) {

    }
}
