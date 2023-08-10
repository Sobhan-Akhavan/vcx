package ir.vcx.domain.model.sso.otp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by a.rokni on 2020/07/03 @Podspace.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Verify {

    private String state;
    private String code;
    private String access_token;
    private String token_type;
    private Long expires_in;
    private String scope;
    private String refresh_token;
    private String id_token;
    private String device_uid;

}
