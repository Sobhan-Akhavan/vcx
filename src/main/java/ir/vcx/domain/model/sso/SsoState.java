package ir.vcx.domain.model.sso;

import ir.vcx.domain.model.VCXDomainModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Sobhan on 11/16/2023 - VCX
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SsoState implements VCXDomainModel {
    private SsoType ssoType;
    private String remoteHost;
    private String payload;
    private String path;
    private Boolean renew;
}
