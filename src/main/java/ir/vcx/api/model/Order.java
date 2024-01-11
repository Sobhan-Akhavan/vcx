package ir.vcx.api.model;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum Order {
    CREATED("created"),
    UPDATED("updated");


    private final String value;
}
