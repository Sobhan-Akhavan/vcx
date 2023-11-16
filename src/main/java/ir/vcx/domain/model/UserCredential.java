package ir.vcx.domain.model;

import ir.vcx.data.entity.VCXUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Sobhan on 11/16/2023 - VCX
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCredential implements VCXDomainModel {
    private String token;
    private String refreshToken;
    private VCXUser user;
}
