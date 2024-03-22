package com.example.readingwritingexcel.job;

import com.example.readingwritingexcel.domain.constant.BatchConstants;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import com.example.readingwritingexcel.domain.enums.ExecutionContextKey;
import com.example.readingwritingexcel.runner.ScheduledJobLauncher;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class TripJobCompletionListener implements JobExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledJobLauncher.class);

    private LocalDateTime jobStartTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobStartTime = LocalDateTime.now();
        var jobName = MessageFormat.format("{0} {1}", BatchConstants.JOB_NAME, jobExecution.getExecutionContext().get("batch.version"));
        LOGGER.info(BatchConstants.START_BLOC_JOB_LISTENER_LOGGER_SEPARATOR, jobName);
        LOGGER.info(" >> START JOB EXECUTION {} AT {}", jobName, jobStartTime);
        LOGGER.info(BatchConstants.END_BLOC_JOB_LISTENER_LOGGER_SEPARATOR);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        var jobName = MessageFormat.format("{0} {1}", BatchConstants.JOB_NAME, jobExecution.getExecutionContext().get("batch.version"));
        LocalDateTime jobStopTime = LocalDateTime.now();
        long jobDuration = Duration.between(jobStartTime, jobStopTime).getSeconds();
        LOGGER.info(BatchConstants.START_BLOC_JOB_LISTENER_LOGGER_SEPARATOR, jobName);
        LOGGER.info(" >> END JOB EXECUTION {}", jobName);

        // get values to execution context
        LOGGER.info(" >> Total trips: {} ", jobExecution.getExecutionContext().get(
                ExecutionContextKey.TRIP_TOTAL.getKey()));


        LOGGER.info(" >> TOTAL JOB PROCESSING DURATION {} IN {} second(s).", jobName, jobDuration);

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            LOGGER.info(" >> EXECUTION OF JOB {} COMPLETED SUCCESSFULLY", jobName);
        } else {
            LOGGER.error(" >> JOB EXECUTION FAILED {}", jobName);
        }
        LOGGER.info(BatchConstants.END_BLOC_JOB_LISTENER_LOGGER_SEPARATOR);
    }

}