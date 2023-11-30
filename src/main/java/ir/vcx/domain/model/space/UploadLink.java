package ir.vcx.domain.model.space;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Sobhan on 11/29/2023 - VCX
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadLink {

    private String uploadHash;
    private long size;
    private long expiration;
    private String folderHash;
    private boolean isPublic;

}
