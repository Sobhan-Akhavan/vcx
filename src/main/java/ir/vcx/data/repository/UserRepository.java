package ir.vcx.data.repository;

import ir.vcx.api.model.IdentityType;
import ir.vcx.api.model.Paging;
import ir.vcx.data.entity.VCXUser;
import ir.vcx.exception.VCXException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public Optional<VCXUser> getUserBySsoId(long ssoId) {

        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("SELECT VU FROM VCXUser VU " +
                        "WHERE VU.ssoId = :id", VCXUser.class)
                .setParameter("id", ssoId)
                .uniqueResultOptional();
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

    @Transactional
    public List<VCXUser> searchOnUser(String identity, IdentityType identityType, Paging paging) throws VCXException {
        Session currentSession = sessionFactory.getCurrentSession();

        StringBuilder stringQuery = new StringBuilder("SELECT VU FROM VCXUser VU ");

        if (identityType != null) {
            if (identityType.equals(IdentityType.USERNAME)) {
                stringQuery.append("WHERE VU.username LIKE :identity ");
            } else if (identityType.equals(IdentityType.SSO_ID)) {
                stringQuery.append("WHERE str(VU.ssoId) LIKE :identity ");
            }
        }

        stringQuery.append("ORDER BY VU.").append(paging.getOrder().getValue()).append(" ");
        stringQuery.append((paging.isDesc()) ? "DESC" : "ASC");

        Query<VCXUser> query = currentSession.createQuery(stringQuery.toString(), VCXUser.class);

        if (identityType != null) {
            query = query.setParameter("identity", "%" + identity.toLowerCase() + "%");
        }

        return query.setFirstResult(paging.getStart())
                .setMaxResults(paging.getSize())
                .getResultList();

    }

    @Transactional
    public Long searchOnUserCount(String identity, IdentityType identityType) throws VCXException {
        Session currentSession = sessionFactory.getCurrentSession();

        StringBuilder stringQuery = new StringBuilder("SELECT COUNT(VU) FROM VCXUser VU ");

        if (identityType != null) {
            if (identityType.equals(IdentityType.USERNAME)) {
                stringQuery.append("WHERE VU.username LIKE :identity ");
            } else if (identityType.equals(IdentityType.SSO_ID)) {
                stringQuery.append("WHERE str(VU.ssoId) LIKE :identity ");
            }
        }

        Query<Long> query = currentSession.createQuery(stringQuery.toString(), Long.class);

        if (identityType != null) {
            query = query.setParameter("identity", "%" + identity.toLowerCase() + "%");
        }

        return query.uniqueResult();
    }
}
