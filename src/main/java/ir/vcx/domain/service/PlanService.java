package ir.vcx.domain.service;

import com.fanapium.keylead.client.users.ClientModifiableUser;
import ir.vcx.api.model.IdentityType;
import ir.vcx.data.entity.VCXPlan;
import ir.vcx.data.entity.VCXUser;
import ir.vcx.data.entity.VCXUserLimit;
import ir.vcx.data.repository.PlanRepository;
import ir.vcx.data.repository.UserLimitRepository;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.DateUtil;
import ir.vcx.util.KeyleadConfiguration;
import ir.vcx.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private final UserLimitService userLimitService;
    private final UserService userService;
    private final PlanRepository planRepository;
    private final UserLimitRepository userLimitRepository;
    private final UserUtil userUtil;
    private final KeyleadConfiguration keyleadConfiguration;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    public PlanService(UserLimitService userLimitService, UserService userService, PlanRepository planRepository, UserLimitRepository userLimitRepository,
                       UserUtil userUtil, KeyleadConfiguration keyleadConfiguration,
                       @Qualifier("planThreadPool") ThreadPoolExecutor threadPoolExecutor) {
        this.planRepository = planRepository;
        this.userLimitService = userLimitService;
        this.userService = userService;
        this.userLimitRepository = userLimitRepository;
        this.userUtil = userUtil;
        this.keyleadConfiguration = keyleadConfiguration;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public Pair<List<VCXPlan>, Long> getPlansList(boolean includeDeactivatedPlan) throws VCXException {

        Future<List<VCXPlan>> getPlansListThread = threadPoolExecutor.submit(() -> planRepository.getPlansList(includeDeactivatedPlan));
        Future<Long> getPlansCountThread = threadPoolExecutor.submit(() -> planRepository.getPlansCount(includeDeactivatedPlan));

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
    public VCXPlan getPlan(String planHash) throws VCXException {
        return planRepository.getPlan(planHash)
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.PLAN_NOT_FOUND));
    }

    @Transactional
    public int deactivateAllPlans() throws VCXException {

        VCXUser vcxAdminUser = Optional.ofNullable(userUtil.getCredential().getUser())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNAUTHORIZED));

        return planRepository.deactivatePlans();
    }

    @Transactional
    public void deactivatePlan(String planHash) throws VCXException {

        VCXUser vcxAdminUser = Optional.ofNullable(userUtil.getCredential().getUser())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNAUTHORIZED));

        VCXPlan vcxPlan = planRepository.getPlan(planHash)
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.PLAN_NOT_FOUND));

        vcxPlan.setActive(Boolean.FALSE);

        planRepository.updatePlan(vcxPlan);
    }

    @Transactional
    public int deleteAllPlans() throws VCXException {

        VCXUser vcxAdminUser = Optional.ofNullable(userUtil.getCredential().getUser())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNAUTHORIZED));

        if (userLimitRepository.anyPlanUsed() > 0) {
            throw new VCXException(VCXExceptionStatus.INVALID_DELETE_PLAN);
        }

        return planRepository.deleteAllPlans();
    }

    @Transactional
    public void deletePlan(String planHash) throws VCXException {

        VCXUser vcxAdminUser = Optional.ofNullable(userUtil.getCredential().getUser())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNAUTHORIZED));

        VCXPlan vcxPlan = planRepository.getPlan(planHash)
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.PLAN_NOT_FOUND));

        if (userLimitRepository.isPlanUsed(vcxPlan).isPresent()) {
            throw new VCXException(VCXExceptionStatus.INVALID_DELETE_PLAN);
        }

        vcxPlan.setActive(Boolean.FALSE);

        planRepository.updatePlan(vcxPlan);

    }

    @Transactional
    public VCXUserLimit purchasePlan(String planHash, String trackingNumber) throws VCXException {

        VCXUser vcxUser = Optional.ofNullable(userUtil.getCredential().getUser())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNAUTHORIZED));

        VCXPlan vcxPlan = planRepository.getPlan(planHash)
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.PLAN_NOT_FOUND));

        if (userLimitService.hashValidPlan(vcxUser)) {
            throw new VCXException(VCXExceptionStatus.INVALID_REQUEST);
        }

        Date expirationDate = DateUtil.calculateTime(vcxPlan.getDaysLimit().getValue());

        return userLimitService.setPlanForUser(vcxUser, vcxPlan, expirationDate, trackingNumber);
    }

    @Transactional
    public VCXUserLimit setUserPlan(String planHash, String identity, IdentityType identityType, boolean force, String trackingNumber) throws VCXException {

        VCXUser vcxAdminUser = Optional.ofNullable(userUtil.getCredential().getUser())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNAUTHORIZED));

        VCXPlan vcxPlan = planRepository.getPlan(planHash)
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.PLAN_NOT_FOUND));

        ClientModifiableUser clientModifiableUser = keyleadConfiguration.getSSOUser(identity, identityType);
        VCXUser vcxUser = userService.getOrCreatePodUser(clientModifiableUser);

        if (userLimitService.hashValidPlan(vcxUser) && !force) {
            throw new VCXException(VCXExceptionStatus.INVALID_REQUEST);
        }

        Date expirationDate = DateUtil.calculateTime(vcxPlan.getDaysLimit().getValue());

        VCXUserLimit vcxUserLimit;
        if (StringUtils.isBlank(trackingNumber)) {
            vcxUserLimit = userLimitService.setPlanForUser(vcxUser, vcxPlan, expirationDate);
        } else {
            vcxUserLimit = userLimitService.setPlanForUser(vcxUser, vcxPlan, expirationDate, trackingNumber);
        }

        return vcxUserLimit;
    }
}
