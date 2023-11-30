package ir.vcx.domain.model.space;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityDetail {
    private String hash;
    private String name;
    private String type;
    private boolean isRemovedTemporary;
    private String parentHash;
    private User owner;
    private User uploader;
    private List<Object> attributes;
    private boolean isPublic;
    private boolean isShared;
    private boolean isBookmarked;
    private long created;
    private long updated;
    private String extension;
    private int size;
    private int version;
    private String thumbnail;
}