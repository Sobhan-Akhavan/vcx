package ir.vcx.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Sobhan Akhavan on 2/8/2024 - vcx
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VCXContentVisited {

    private VCXContent content;
    private long visitCount;
}
