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

    @Transactional
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
}
