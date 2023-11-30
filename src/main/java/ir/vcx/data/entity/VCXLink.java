package ir.vcx.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Sobhan on 11/23/2023 - VCX
 */

@Entity
@Table(name = "VCX_LINK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VCXLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "LINK", nullable = false)
    private String link;

    @ManyToOne(fetch = FetchType.EAGER)
    private VCXFolder folder;

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
