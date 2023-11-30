package ir.vcx.domain.model.space;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadLinkInfo {
    private String userGroupHash;
    private String fileHash;
    private String fileName;
    private String filetype;
    private Long fileSize;
    private String downloadLink;
    private boolean revokeAbility;
    private Long created;
    private Long expiration;
}