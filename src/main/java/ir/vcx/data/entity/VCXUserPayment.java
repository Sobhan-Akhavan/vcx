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
@Table(name = "VCX_USER_PAYMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VCXUserPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TRACKING_NUMBER", unique = true, nullable = false)
    private String trackingNumber;

    @OneToOne(fetch = FetchType.EAGER)
    private VCXUserLimit userLimit;

    @Version
    @Column(name = "OPTLOCK", nullable = false)
    private Integer optlock;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED", nullable = false, updatable = false)
    private Date created;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED", nullable = false, updatable = false)
    private Date updated;
}
