package ir.vcx.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Sobhan on 11/16/2023 - VCX
 */
@Entity
@Table(name = "VCX_USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VCXUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "SSOID", nullable = false, unique = true)
    private Long ssoId;

    @Column(name = "AVATAR")
    private String avatar;

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
