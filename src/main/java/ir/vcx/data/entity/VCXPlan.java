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
    @Column(name = "DAYS_LIMIT", nullable = false)
    private DaysLimit daysLimit;

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
    public enum DaysLimit {
        ONE_MONTH(31),
        THREE_MONTH(93),
        SIX_MONTH(186),
        TWELVE_MONTH(372);

        private final int value;
    }
}
