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
 * Created by Sobhan Akhavan on 1/15/2024 - vcx
 */

@Entity
@Table(name = "VCX_USER_LIMIT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VCXUserLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private VCXPlan plan;

    @OneToOne(fetch = FetchType.EAGER)
    private VCXUser user;

    @Column(name = "ACTIVE", nullable = false)
    private Boolean active;

    @Version
    @Column(name = "OPTLOCK", nullable = false)
    private Integer optlock;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EXPIRATION", nullable = false, updatable = false)
    private Date expiration;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED", nullable = false, updatable = false)
    private Date created;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED", nullable = false)
    private Date updated;
}
