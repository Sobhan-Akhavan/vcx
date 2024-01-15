package ir.vcx.domain.service;

import ir.vcx.data.entity.VCXPlan;
import ir.vcx.data.repository.PlanRepository;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    public PlanService(PlanRepository planRepository, @Qualifier("planThreadPool") ThreadPoolExecutor threadPoolExecutor) {
        this.planRepository = planRepository;
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
    public VCXPlan addPlan(long price, VCXPlan.MonthLimit limit, boolean active) throws VCXException {

        String name;
        switch (limit) {
            case ONE:
                name = "طرح اشتراک یک ماهه";
                break;
            case THREE:
                name = "طرح اشتراک سه ماهه";
                break;
            case SIX:
                name = "طرح اشتراک شش ماهه";
                break;
            case TWELVE:
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

        planRepository.deactivatePlans();

    }
}
