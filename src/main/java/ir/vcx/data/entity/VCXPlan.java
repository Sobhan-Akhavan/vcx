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
@Table(name = "VCX_PLAN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VCXPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "HASH", unique = true, nullable = false)
    private String hash;

    @Column(name = "PRICE", nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "MONTH_LIMIT", nullable = false)
    private MonthLimit monthLimit;

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

    @Getter
    @AllArgsConstructor
    public enum MonthLimit {
        ONE(1),
        THREE(3),
        SIX(6),
        TWELVE(12);

        private final int value;
    }
}
