package ir.vcx.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VCXUser {

    private String username;
    private String name;
    private Long ssoId;
    private String avatar;

}
