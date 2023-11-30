package ir.vcx.domain.model.space;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceResponse<T> {
    private T result;
    private String reference;
    private String path;
    private int status;
    private String timestamp;
    private String error;
    private String message;
}
