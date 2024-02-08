package ir.vcx.api.model;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum Order {
    CREATED("created"),
    UPDATED("updated"),
    USERNAME("username"),
    NAME("name"),
    SSO_ID("ssoId"),
    COUNT("count"),


    ;


    private final String value;
}
