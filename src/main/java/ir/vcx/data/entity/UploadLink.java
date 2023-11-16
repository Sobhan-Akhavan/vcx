package ir.vcx.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Sobhan on 11/9/2023 - VCX
 */

@Entity
@Table(name = "UPLOAD_LINK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "VIDEO_TYPE", nullable = false)
    private VideoType videoType;

    @Column(name = "GENRE_TYPE", nullable = false)
    private GenreType genreType;

    @Column(name = "LINK", nullable = false)
    private String link;

    @Column(name = "ACTIVE", nullable = false)
    private Boolean active;

    @Version
    @Column(name = "OPTLOCK", nullable = false)
    private Integer optlock;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED", nullable = false, updatable = false)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED", nullable = false)
    private Date updated;
}
