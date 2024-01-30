package ir.vcx.domain.service;

import ir.vcx.data.entity.VCXPlan;
import ir.vcx.data.entity.VCXUser;
import ir.vcx.data.entity.VCXUserLimit;
import ir.vcx.data.repository.UserLimitRepository;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Sobhan Akhavan on 1/28/2024 - vcx
 */

@Service
public class UserLimitService {

    private final UserLimitRepository userLimitRepository;

    @Autowired
    public UserLimitService(UserLimitRepository userLimitRepository) {
        this.userLimitRepository = userLimitRepository;
    }

    public boolean hashValidPlan(VCXUser vcxUser) {
        return userLimitRepository.getActiveUserPlan(vcxUser).isPresent();
    }

    public VCXUserLimit setPlanForUser(VCXUser vcxUser, VCXPlan vcxPlan, Date expirationDate) {

        return userLimitRepository.setUserPlan(vcxUser, vcxPlan, expirationDate);
    }

    public VCXUserLimit setPlanForUser(VCXUser vcxUser, VCXPlan vcxPlan, Date expirationDate, String trackingNumber) throws VCXException {

        if (StringUtils.isBlank(trackingNumber)) {
            throw new VCXException(VCXExceptionStatus.INVALID_REQUEST);
        }

        VCXUserLimit vcxUserLimit = userLimitRepository.setUserPlan(vcxUser, vcxPlan, expirationDate);

        userLimitRepository.saveUserPayment(vcxUserLimit, trackingNumber);

        return vcxUserLimit;
    }
}
