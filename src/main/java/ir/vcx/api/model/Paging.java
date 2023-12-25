package ir.vcx.api.model;

import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paging {

    private int start;
    private int size;
    private Order order;
    private boolean desc;

    public static void checkOrder(HashSet<Order> allowableOrders, Order order) throws VCXException {
        if (!allowableOrders.contains(order)) {
            throw new VCXException(VCXExceptionStatus.INVALID_PAGINATION_ORDER);
        }
    }
}
