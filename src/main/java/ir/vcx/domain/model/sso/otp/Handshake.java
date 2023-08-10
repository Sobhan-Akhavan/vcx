package ir.vcx.domain.model.sso.otp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Handshake {
    @JsonProperty("keyFormat")
    private String keyFormat;

    @JsonProperty("client")
    private Client client;

    @JsonProperty("keyId")
    private String keyId;

    @JsonProperty("publicKey")
    private String publicKey;

    @JsonProperty("device")
    private Device device;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    @JsonProperty("algorithm")
    private String algorithm;
}
