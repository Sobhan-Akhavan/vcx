package ir.vcx.domain.model.space;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Folder {
    private User owner;
    private User uploader;
    private long created;
    private String name;
    private List<String> attributes;
    private String type;
    private long updated;
    private String hash;
    private String parentHash;
}