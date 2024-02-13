package ir.vcx.data.repository;

import ir.vcx.api.model.Order;
import ir.vcx.api.model.Paging;
import ir.vcx.data.entity.*;
import ir.vcx.domain.model.GenreTypeReport;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.List;
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
        vcxContent.setGenresType(genreTypes);

        currentSession.persist(vcxContent);

        return vcxContent;
    }

    public Optional<VCXContent> getAvailableContent(String hash, boolean needGenreType, boolean needParentFolder) {

        Session currentSession = sessionFactory.getCurrentSession();

        StringBuilder stringQuery = new StringBuilder("SELECT VC FROM VCXContent VC ");

        if (needGenreType) {
            stringQuery.append("LEFT JOIN FETCH VC.genresType VCG ");
        }

        if (needParentFolder) {
            stringQuery.append("LEFT JOIN FETCH VC.parentFolder VCPF ");
        }

        stringQuery.append("WHERE VC.hash = :hash ");
        stringQuery.append("AND VC.active = :val");

        return currentSession.createQuery(stringQuery.toString(), VCXContent.class)
                .setParameter("hash", hash)
                .setParameter("val", Boolean.TRUE)
                .uniqueResultOptional();

    }

    public VCXContent updateContent(VCXContent vcxContent) {

        Session currentSession = sessionFactory.getCurrentSession();

        return (VCXContent) currentSession.merge(vcxContent);
    }

    @Transactional
    public List<VCXContent> getContents(String name, VideoType videoType, Set<GenreType> genreTypes, boolean includePosterLessContent, Paging paging) {

        Session currentSession = sessionFactory.getCurrentSession();

        StringBuilder stringQuery = new StringBuilder("SELECT VC FROM VCXContent VC ");

        stringQuery.append("LEFT JOIN FETCH VC.genresType VCG ");

        boolean isWhereClauseAdded = false;
        if (StringUtils.isNotBlank(name)) {
            stringQuery.append("WHERE LOWER(VC.name) LIKE :name ");
            isWhereClauseAdded = true;
        }

        if (videoType != null) {
            stringQuery.append(isWhereClauseAdded ? "AND " : "WHERE ");
            stringQuery.append("VC.videoType = :videoType ");
            isWhereClauseAdded = true;
        }

        if (genreTypes != null && !genreTypes.isEmpty()) {
            stringQuery.append(isWhereClauseAdded ? "AND " : "WHERE ");
            stringQuery.append("VCG IN :genreTypes ");
            isWhereClauseAdded = true;
        }

        if (!includePosterLessContent) {
            stringQuery.append(isWhereClauseAdded ? "AND " : "WHERE ");
            stringQuery.append("VC.horizontalPoster IS NOT NULL ");
            stringQuery.append("AND ");
            stringQuery.append("VC.verticalPoster IS NOT NULL ");
        }

        stringQuery.append("ORDER BY VC.").append(paging.getOrder().getValue()).append(" ")
                .append((paging.isDesc() ? "DESC" : "ASC"));

        Query<VCXContent> query = currentSession.createQuery(stringQuery.toString(), VCXContent.class);

        if (StringUtils.isNotBlank(name)) {
            query.setParameter("name", "%" + name.toLowerCase() + "%");
        }

        if (videoType != null) {
            query.setParameter("videoType", videoType);
        }

        if (genreTypes != null && !genreTypes.isEmpty()) {
            query.setParameterList("genreTypes", genreTypes);
        }

        return query
                .setFirstResult(paging.getStart())
                .setMaxResults(paging.getSize())
                .getResultList();
    }

    @Transactional
    public Long getContentsCount(String name, VideoType videoType, Set<GenreType> genreTypes, boolean includePosterLessContent) {
        Session currentSession = sessionFactory.getCurrentSession();

        StringBuilder stringQuery = new StringBuilder("SELECT COUNT(DISTINCT VC) FROM VCXContent VC ");

        stringQuery.append("LEFT JOIN VC.genresType VCG ");

        boolean isWhereClauseAdded = false;
        if (StringUtils.isNotBlank(name)) {
            stringQuery.append("WHERE LOWER(VC.name) LIKE :name ");
            isWhereClauseAdded = true;
        }

        if (videoType != null) {
            stringQuery.append(isWhereClauseAdded ? "AND " : "WHERE ");
            stringQuery.append("VC.videoType = :videoType ");
            isWhereClauseAdded = true;
        }

        if (genreTypes != null && !genreTypes.isEmpty()) {
            stringQuery.append(isWhereClauseAdded ? "AND " : "WHERE ");
            stringQuery.append("VCG IN :genreTypes ");
            isWhereClauseAdded = true;
        }

        if (!includePosterLessContent) {
            stringQuery.append(isWhereClauseAdded ? "AND " : "WHERE ");
            stringQuery.append("VC.horizontalPoster IS NOT NULL ");
            stringQuery.append("AND ");
            stringQuery.append("VC.verticalPoster IS NOT NULL");
        }

        Query<Long> query = currentSession.createQuery(stringQuery.toString(), Long.class);

        if (StringUtils.isNotBlank(name)) {
            query.setParameter("name", "%" + name.toLowerCase() + "%");
        }

        if (videoType != null) {
            query.setParameter("videoType", videoType);
        }

        if (genreTypes != null && !genreTypes.isEmpty()) {
            query.setParameterList("genreTypes", genreTypes);
        }

        return query.getSingleResult();
    }

    @Transactional
    public void incrementViewCount(VCXContent vcxContent) {

        Session currentSession = sessionFactory.getCurrentSession();

        VCXContentVisit vcxContentVisit = currentSession.createQuery("SELECT VCV FROM VCXContentVisit VCV " +
                        "WHERE VCV.content = :content", VCXContentVisit.class)
                .setParameter("content", vcxContent)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getSingleResult();

        vcxContentVisit.setCount(vcxContentVisit.getCount() + 1);
        currentSession.merge(vcxContentVisit);
    }

    public void addFirstVisitedCount(VCXContent vcxContent) {
        Session currentSession = sessionFactory.getCurrentSession();

        VCXContentVisit newVisit = new VCXContentVisit();
        newVisit.setContent(vcxContent);
        newVisit.setCount(1);

        currentSession.persist(newVisit);
    }

    @Transactional
    public List<VCXContentVisit> mostVisitedContent(Paging paging) {
        Session currentSession = sessionFactory.getCurrentSession();

        String orderValue = paging.getOrder().getValue();
        return currentSession.createQuery("SELECT VCV FROM VCXContentVisit VCV " +
                        "INNER JOIN FETCH VCV.content VCVC " +
                        "INNER JOIN FETCH VCVC.genresType VCVCG " +
                        "ORDER BY " + (paging.getOrder().equals(Order.NAME) ? "VCVC." + orderValue : "VCV." + orderValue) + " " +
                        ((paging.isDesc()) ? "DESC" : "ASC"), VCXContentVisit.class)
                .setFirstResult(paging.getStart())
                .setMaxResults(paging.getSize())
                .getResultList();

    }

    @Transactional
    public Long mostVisitedVideoCount() {
        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT COUNT(VCV) FROM VCXContentVisit VCV", Long.class)
                .getSingleResult();

    }

    public VCXContentVisit getContentVisited(VCXContent vcxContent) {
        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VCV FROM VCXContentVisit VCV " +
                        "WHERE VCV.content = :content", VCXContentVisit.class)
                .setParameter("content", vcxContent)
                .getSingleResult();
    }

    @Transactional
    public Long getContentTypeCount(VideoType videoType) {
        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT COUNT(VC) FROM VCXContent VC " +
                        "WHERE VC.videoType = :type", Long.class)
                .setParameter("type", videoType)
                .getSingleResult();
    }

    /**
     * we can handle all logic with one nativeQuery, but try it to make the query with a hibernated framework.
     * we can't calculate the percentage of each genreType
     * because postgresSQL didn't support a nested query like SUM (COUNT(VCG)).
     * and other so because GROUP BY syntax has a heavy load, we didn't try to calculate SUM (COUNT(VCG)) separate query.
     *
     * @return List<GenresTypeReport>
     */
    @Transactional
    public List<GenreTypeReport> getMostGenreTypes() {
        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT new ir.vcx.domain.model.GenreTypeReport(VCG, COUNT(VCG)) " +
                        "FROM VCXContent VC " +
                        "INNER JOIN VC.genresType VCG " +
                        "GROUP BY VCG", GenreTypeReport.class)
                .getResultList();
    }

    public void saveContentByte(byte[] bytes, VCXContent vcxContent) {
        Session currentSession = sessionFactory.getCurrentSession();

        VCXFileEntity vcxFileEntity = new VCXFileEntity();
        vcxFileEntity.setData(bytes);
        vcxFileEntity.setSize(bytes.length);
        vcxFileEntity.setVcxContent(vcxContent);

        currentSession.persist(vcxFileEntity);
    }

    public VCXFileEntity getContentsBytes(VCXContent vcxContent) {
        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VFE FROM VCXFileEntity VFE " +
                        "WHERE VFE.vcxContent = :content", VCXFileEntity.class)
                .setParameter("content", vcxContent)
                .getSingleResult();
    }
}
