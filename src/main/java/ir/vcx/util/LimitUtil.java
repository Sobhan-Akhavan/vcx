package ir.vcx.util;

import ir.vcx.api.model.IdentityType;
import ir.vcx.api.model.Order;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;

import java.util.List;

/**
 * Created by Sobhan Akhavan on 2/6/2024 - vcx
 */

public class LimitUtil {

    public static <T extends Order> void validateInput(List<T> allowableVariable, T variable) throws VCXException {
        if (!allowableVariable.contains(variable) && variable != null) {
            throw new VCXException(VCXExceptionStatus.INVALID_PAGINATION_ORDER);
        }
    }

    public static <T extends IdentityType> void validateInput(List<T> allowableVariable, T variable) throws VCXException {
        if (!allowableVariable.contains(variable) && variable != null) {
            throw new VCXException(VCXExceptionStatus.INVALID_IDENTITY_TYPE);
        }
    }

}
