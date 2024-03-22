package com.example.readingwritingexcel.runner;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.readingwritingexcel.domain.enums.JobParametersKey;

/**
 * @author aek
 */
@Component
public class ScheduledJobLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledJobLauncher.class);

    @Value("${batch.csv-path-directory}")
    private String pathDirectory;
    private final Job job;

    private final JobLauncher jobLauncher;

    ScheduledJobLauncher(Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

    // run every 2 min
   // @Scheduled(fixedRate = 120000)
    @Scheduled(fixedRate = 240000)
    void launchFileToJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobInstanceAlreadyCompleteException, JobRestartException {
        LOGGER.info("Scheduled Starting job");
        DateFormat fileDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm");
        
        
        Long jobCurrentTime = System.currentTimeMillis();
        String strDate = fileDateFormat.format(jobCurrentTime);
		String fileName = MessageFormat.format("{0}_{1}.csv", "citibike_extract", strDate);

        JobParameters params = new JobParametersBuilder()
                .addLong(JobParametersKey.JOB_ID.getKey(), jobCurrentTime)
                .addDate(JobParametersKey.CURRENT_TIME.getKey(),new Date())
                .addString(JobParametersKey.PATH_DIRECTORY.getKey(), pathDirectory)
                .addString(JobParametersKey.FILE_NAME.getKey(), fileName)
                .toJobParameters();

        jobLauncher.run(job, params);

        LOGGER.info("Scheduled Stopping job");
    }

}