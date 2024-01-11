package ir.vcx.data.repository;

import ir.vcx.data.entity.VCXPlan;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlanRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public PlanRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<VCXPlan> getPlansList() {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VP FROM VCXPlan VP", VCXPlan.class)
                .getResultList();
    }

    public Long getPlansCount() {
        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT COUNT(VP) FROM VCXPlan VP", Long.class)
                .getSingleResult();
    }
}
