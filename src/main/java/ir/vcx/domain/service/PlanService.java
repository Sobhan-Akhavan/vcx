package ir.vcx.domain.service;

import com.fanapium.keylead.client.users.ClientModifiableUser;
import ir.vcx.api.model.IdentityType;
import ir.vcx.data.entity.VCXPlan;
import ir.vcx.data.entity.VCXUser;
import ir.vcx.data.entity.VCXUserLimit;
import ir.vcx.data.repository.PlanRepository;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.DateUtil;
import ir.vcx.util.KeyleadConfiguration;
import ir.vcx.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final UserService userService;
    private final UserUtil userUtil;
    private final KeyleadConfiguration keyleadConfiguration;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    public PlanService(PlanRepository planRepository, UserService userService, UserUtil userUtil, KeyleadConfiguration keyleadConfiguration,
                       @Qualifier("planThreadPool") ThreadPoolExecutor threadPoolExecutor) {
        this.planRepository = planRepository;
        this.userService = userService;
        this.userUtil = userUtil;
        this.keyleadConfiguration = keyleadConfiguration;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public Pair<List<VCXPlan>, Long> getPlansList() throws VCXException {

        Future<List<VCXPlan>> getPlansListThread = threadPoolExecutor.submit(planRepository::getPlansList);

        Future<Long> getPlansCountThread = threadPoolExecutor.submit(planRepository::getPlansCount);

        try {
            List<VCXPlan> contents = getPlansListThread.get();
            Long plansCount = getPlansCountThread.get();

            return Pair.of(contents, plansCount);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();

            throw new VCXException(VCXExceptionStatus.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public VCXPlan addPlan(long price, VCXPlan.DaysLimit limit, boolean active) throws VCXException {

        VCXUser vcxAdminUser = Optional.ofNullable(userUtil.getCredential().getUser())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNAUTHORIZED));

        String name;
        switch (limit) {
            case ONE_MONTH:
                name = "طرح اشتراک یک ماهه";
                break;
            case THREE_MONTH:
                name = "طرح اشتراک سه ماهه";
                break;
            case SIX_MONTH:
                name = "طرح اشتراک شش ماهه";
                break;
            case TWELVE_MONTH:
                name = "طرح اشتراک دوازده ماهه";
                break;
            default:
                throw new VCXException(VCXExceptionStatus.UNKNOWN_ERROR);
        }

        if (planRepository.getActivePlanByLimit(limit).isPresent()) {

            throw new VCXException(VCXExceptionStatus.PLAN_LIMIT_CONFLICT);

        } else {

            return planRepository.addPlan(name, price, limit, active);

        }
    }

    @Transactional
    public void deactivatePlans() throws VCXException {

        VCXUser vcxAdminUser = Optional.ofNullable(userUtil.getCredential().getUser())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNAUTHORIZED));

        planRepository.deactivatePlans();

    }

    @Transactional
    public VCXUserLimit purchasePlan(String planHash, String trackingNumber) throws VCXException {

        VCXUser vcxUser = Optional.ofNullable(userUtil.getCredential().getUser())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNAUTHORIZED));

        VCXPlan vcxPlan = planRepository.getPlan(planHash)
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.PLAN_NOT_FOUND));

        if (userService.hashValidPlan(vcxUser)) {
            throw new VCXException(VCXExceptionStatus.INVALID_REQUEST);
        }

        Date expirationDate = DateUtil.calculateTime(vcxPlan.getDaysLimit().getValue());

        return userService.setPlanForUser(vcxUser, vcxPlan, expirationDate, trackingNumber);
    }

    @Transactional
    public VCXUserLimit purchasePlan(String planHash, String identity, IdentityType identityType, boolean force, String trackingNumber) throws VCXException {

        VCXUser vcxAdminUser = Optional.ofNullable(userUtil.getCredential().getUser())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNAUTHORIZED));

        VCXPlan vcxPlan = planRepository.getPlan(planHash)
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.PLAN_NOT_FOUND));

        ClientModifiableUser clientModifiableUser = keyleadConfiguration.getSSOUser(identity, identityType);
        VCXUser vcxUser = userService.getOrCreatePodUser(clientModifiableUser);

        if (userService.hashValidPlan(vcxUser) && !force) {
            throw new VCXException(VCXExceptionStatus.INVALID_REQUEST);
        }

        Date expirationDate = DateUtil.calculateTime(vcxPlan.getDaysLimit().getValue());

        return userService.setPlanForUser(vcxUser, vcxPlan, expirationDate, trackingNumber);

    }
}
