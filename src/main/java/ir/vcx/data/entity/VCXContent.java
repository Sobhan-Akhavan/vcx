package ir.vcx.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by Sobhan on 11/23/2023 - VCX
 */

@Entity
@Table(name = "VCX_CONTENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VCXContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "HASH", nullable = false)
    private String hash;

    @Column(name = "POSTER_HASH")
    private String posterHash;

    @ManyToOne(fetch = FetchType.EAGER)
    private VCXFolder parentFolder;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "VIDEO_TYPE", nullable = false)
    private VideoType videoType;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "GENRE_TYPE", nullable = false)
    private Set<GenreType> genreType;

    @Version
    @Column(name = "OPTLOCK", nullable = false)
    private Integer optlock;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED", nullable = false, updatable = false)
    private Date created;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED", nullable = false)
    private Date updated;
}
