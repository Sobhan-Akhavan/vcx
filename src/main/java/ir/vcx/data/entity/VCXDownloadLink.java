package ir.vcx.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VCX_DOWNLOAD_LINK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VCXDownloadLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "LINK", nullable = false)
    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    private VCXContent content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EXPIRATION", nullable = false)
    private Date expiration;

    @Column(name = "ACTIVE", nullable = false)
    private Boolean active;

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
