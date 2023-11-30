package ir.vcx.domain.model.space;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroup {
    private User owner;
    private boolean isRemoved;
    private List<String> zoneNames;
    private Folder folder;
    private long created;
    private boolean allowMemberShare;
    private String cachePolicy;
    private long updated;
    private String hash;
    private List<User> users;
}