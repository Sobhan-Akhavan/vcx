package ir.vcx.data.repository;

import ir.vcx.api.model.Paging;
import ir.vcx.data.entity.*;
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
        vcxContent.setGenresType(genreTypes);

        currentSession.persist(vcxContent);

        return vcxContent;
    }

    public Optional<VCXContent> getAvailableContent(String hash, boolean needGenreType, boolean needPoster, boolean needParentFolder) {

        Session currentSession = sessionFactory.getCurrentSession();

        StringBuilder stringQuery = new StringBuilder("SELECT VC FROM VCXContent VC ");

        if (needGenreType) {
            stringQuery.append("LEFT JOIN FETCH VC.genresType VCG ");
        }

        if (needPoster) {
            stringQuery.append("LEFT JOIN FETCH VC.posters VCP ");
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

        currentSession.merge(vcxContent);

        return vcxContent;

    }

    @Transactional
    public List<VCXContent> getContents(String name, VideoType videoType, Set<GenreType> genreTypes, boolean includePosterLessContent, Paging paging) {

        Session currentSession = sessionFactory.getCurrentSession();

        StringBuilder stringQuery = new StringBuilder("SELECT VC FROM VCXContent VC ");

        if (includePosterLessContent) {
            stringQuery.append("LEFT JOIN FETCH VC.genresType VCG ");
            stringQuery.append("LEFT JOIN FETCH VC.posters VCP ");
        } else {
            stringQuery.append("INNER JOIN FETCH VC.genresType VCG ");
            stringQuery.append("INNER JOIN FETCH VC.posters VCP ");
        }

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


        StringBuilder stringQuery = new StringBuilder("SELECT COUNT(VC) FROM VCXContent VC ");

        if (includePosterLessContent) {
            stringQuery.append("LEFT JOIN VC.genresType VCG ");
        } else {
            stringQuery.append("INNER JOIN VC.genresType VCG ");
        }

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

    public VCXPoster addPoster(String posterHash, boolean horizontal) {

        Session currentSession = sessionFactory.getCurrentSession();

        VCXPoster vcxPoster = new VCXPoster();
        vcxPoster.setPosterHash(posterHash);
        vcxPoster.setHorizontal(horizontal);

        currentSession.persist(vcxPoster);

        return vcxPoster;
    }
}
