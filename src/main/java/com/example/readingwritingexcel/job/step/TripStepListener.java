package com.example.readingwritingexcel.job.step;

import com.example.readingwritingexcel.domain.enums.ExecutionContextKey;
import com.example.readingwritingexcel.runner.ScheduledJobLauncher;
import com.example.readingwritingexcel.domain.constant.BatchConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Component
public class TripStepListener extends ExecutionContextPromotionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledJobLauncher.class);

    public static final String STEP_DESCRIPTION = "Trip step process";
    
    public static final String STEP_NAME = "TRIP SUMMARY";

    private LocalDateTime stepStartTime;

    public TripStepListener() {
        setKeys(new String[]{
                ExecutionContextKey.TRIP_TOTAL.getKey(), "hello.tr"
        });
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        stepStartTime = LocalDateTime.now();
        LOGGER.info(BatchConstants.START_BLOC_STEP_LISTENER_LOGGER_SEPARATOR, STEP_NAME);
        LOGGER.info(" >> START STEP: {} ...", STEP_DESCRIPTION);
        LOGGER.info(" >> START TIME: {}", stepStartTime);
        LOGGER.info(BatchConstants.END_BLOC_STEP_LISTENER_LOGGER_SEPARATOR);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        LOGGER.info(BatchConstants.START_BLOC_STEP_LISTENER_LOGGER_SEPARATOR, STEP_NAME);
        LocalDateTime stepStopTime = LocalDateTime.now();
        long stepDuration = Duration.between(stepStartTime, stepStopTime).getSeconds();
        LOGGER.info(" >> TRIP STEP SUMMARY");
        LOGGER.info(" >> END TIME: {}", stepStopTime);
        LOGGER.info(" >> TOTAL DURATION: {} seconds.", stepDuration);

        LOGGER.info(" >> ExitStatus: {}", stepExecution.getExitStatus().getExitCode());
        LOGGER.info(" >> ReadCount: {}", stepExecution.getReadCount());
        LOGGER.info(" >> ReadSkipCount: {}", stepExecution.getReadSkipCount());
        LOGGER.info(" >> WriteCount: {}", stepExecution.getWriteCount());
        LOGGER.info(" >> WriteSkipCount: {}", stepExecution.getWriteSkipCount());
        LOGGER.info(" >> Summary: {}" , stepExecution.getSummary());
        LOGGER.info(" >> END STEP: {}", STEP_DESCRIPTION);
        LOGGER.info(BatchConstants.END_BLOC_STEP_LISTENER_LOGGER_SEPARATOR);
        return ExitStatus.COMPLETED;
    }

}