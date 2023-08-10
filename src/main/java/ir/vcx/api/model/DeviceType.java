package ir.vcx.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Getter
@AllArgsConstructor
public enum DeviceType {

    MOBILE_PHONE("Mobile Phone"),
    DESKTOP("Desktop"),
    TABLET("Tablet"),
    CONSOLE("Console"),
    TV_DEVICE("TV Device"),
    LEGACY_DEVICE("Legacy Device");

    private final String value;
}
