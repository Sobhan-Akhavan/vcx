package ir.vcx.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VCXPlan {

    private String name;
    private String hash;
    private long price;
    private ir.vcx.data.entity.VCXPlan.DaysLimit daysLimit;
    private Date created;
    private Date updated;

}
