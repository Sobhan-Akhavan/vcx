package ir.vcx.domain.model.space;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Share {

    private String hash;
    private EntityDetail entity;
    private EntityType entityType;
    private ShareType type;
    private ShareLevel level;
    private Long expiration;
    private User creator;
    private User person;
    private Long created;
    private Long updated;
    private Set<ShareAccess> accessList;
}
