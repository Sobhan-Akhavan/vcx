package ir.vcx.domain.model.space;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int ssoId;
    private List<String> roles;
    private String name;
    private String avatar;
    private String username;
}