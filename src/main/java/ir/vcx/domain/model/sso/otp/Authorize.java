package ir.vcx.domain.model.sso.otp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Authorize {
    private Long expires_in;
    private String identity;
    private String type;
    private String user_id;
}
