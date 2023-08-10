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
public class Device {
    @JsonProperty("uid")
    private String uid;

    @JsonProperty("current")
    private Boolean current;

    @JsonProperty("lastAccessTime")
    private Long lastAccessTime;

    @JsonProperty("ip")
    private String ip;

    @JsonProperty("language")
    private String language;

    @JsonProperty("location")
    private Location location;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("agent")
    private String agent;

    @JsonProperty("os")
    private String os;

    @JsonProperty("osVersion")
    private String osVersion;

    @JsonProperty("browser")
    private String browser;

    @JsonProperty("browserVersion")
    private String browserVersion;

    @JsonProperty("deviceType")
    private String deviceType;

    @JsonProperty("name")
    private String name;

    @JsonProperty("appVersion")
    private String appVersion;

    @JsonProperty("appName")
    private String appName;
}
