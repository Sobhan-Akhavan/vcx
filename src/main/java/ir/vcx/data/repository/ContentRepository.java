package ir.vcx.data.repository;

import ir.vcx.api.model.Paging;
import ir.vcx.data.entity.GenreType;
import ir.vcx.data.entity.VCXContent;
import ir.vcx.data.entity.VCXFolder;
import ir.vcx.data.entity.VideoType;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
        vcxContent.setGenreType(genreTypes);

        currentSession.persist(vcxContent);

        return vcxContent;
    }

    public Optional<VCXContent> getContent(String hash) {

        Session currentSession = sessionFactory.getCurrentSession();

        return currentSession.createQuery("SELECT VC FROM VCXContent VC " +
                        "WHERE VC.hash = :hash", VCXContent.class)
                .setParameter("hash", hash)
                .uniqueResultOptional();

    }

    public VCXContent updateContent(VCXContent vcxContent) {

        Session currentSession = sessionFactory.getCurrentSession();

        currentSession.merge(vcxContent);

        return vcxContent;

    }

    @Transactional
    public List<VCXContent> getContents(String name, VideoType videoType, Set<GenreType> genreTypes, Paging paging) {

        Session currentSession = sessionFactory.getCurrentSession();

        StringBuilder stringQuery = new StringBuilder("SELECT VC FROM VCXContent VC ");

        stringQuery.append("INNER JOIN FETCH VC.genreType VCG ");

        boolean isWhereClauseAdded = false;
        if (StringUtils.isNotBlank(name)) {
            stringQuery.append("WHERE LOWER(VC.name) LIKE :name ");
            isWhereClauseAdded = true;
        }

        if (videoType != null) {
            stringQuery.append(isWhereClauseAdded ? "AND " : "WHERE ");
            stringQuery.append("VC.videoType = :videoType ");
        }

        if (genreTypes != null && !genreTypes.isEmpty()) {
            stringQuery.append(isWhereClauseAdded ? "AND " : "WHERE ");
            stringQuery.append("VCG IN :genreTypes ");
        }

        stringQuery.append("ORDER BY ").append(paging.getOrder()).append(" ")
                .append((paging.isDesc() ? "desc" : "asc"));

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
    public Long getContentsCount(String name, VideoType videoType, Set<GenreType> genreTypes) {
        Session currentSession = sessionFactory.getCurrentSession();

        StringBuilder stringQuery = new StringBuilder("SELECT COUNT(VC) FROM VCXContent VC ");

        stringQuery.append("INNER JOIN VC.genreType VCG ");

        boolean isWhereClauseAdded = false;
        if (StringUtils.isNotBlank(name)) {
            stringQuery.append("WHERE LOWER(VC.name) LIKE :name ");
            isWhereClauseAdded = true;
        }

        if (videoType != null) {
            stringQuery.append(isWhereClauseAdded ? "AND " : "WHERE ");
            stringQuery.append("VC.videoType = :videoType ");
        }

        if (genreTypes != null && !genreTypes.isEmpty()) {
            stringQuery.append(isWhereClauseAdded ? "AND " : "WHERE ");
            stringQuery.append("VCG IN :genreTypes ");
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
}
