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
 * Created by Sobhan Akhavan on 2/8/2024 - vcx
 */

@Entity
@Table(name = "VCX_CONTENT_VISIT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VCXContentVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private VCXContent content;

    @Column(name = "COUNT")
    private long count;

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
