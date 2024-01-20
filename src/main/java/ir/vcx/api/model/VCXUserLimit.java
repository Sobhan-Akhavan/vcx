package ir.vcx.api.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Created by Sobhan Akhavan on 1/20/2024 - vcx
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VCXUserLimit {

    private VCXPlan plan;
    private VCXUser user;
    private Date expiration;

}
