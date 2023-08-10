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
public class Location {
    private Long lat;
    private Long lon;
    private String name;
    private String countryCode;
}
