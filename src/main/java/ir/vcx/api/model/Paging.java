package ir.vcx.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paging {

    private int start;
    private int size;
    private Order order;
    private boolean desc;
}
