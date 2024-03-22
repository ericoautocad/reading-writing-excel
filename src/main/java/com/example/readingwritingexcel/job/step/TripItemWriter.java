package com.example.readingwritingexcel.job.step;

import static com.example.readingwritingexcel.domain.constant.BatchConstants.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.example.readingwritingexcel.domain.enums.ExecutionContextKey;
import com.example.readingwritingexcel.domain.enums.JobParametersKey;
import com.example.readingwritingexcel.domain.model.TripCsvLine;
import com.example.readingwritingexcel.runner.ScheduledJobLauncher;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TripItemWriter implements ItemWriter<TripCsvLine>, StepExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledJobLauncher.class);
    
    private StepExecution stepExecution;

    private int totalWriteTrip = 0;

    private final List<TripCsvLine> writeTrips = new ArrayList<>();

	private final DateFormat fileDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm");
    private static final String CSV_HEADER = "bikeID,Age,Gender,TripDuration,StartStation,EndStation";

    @Override
    public void write(Chunk<? extends TripCsvLine> tripsChunk) {
        totalWriteTrip += tripsChunk.getItems().size();

        writeTrips.addAll(tripsChunk.getItems());
    }


    @BeforeStep
    public void beforeStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @AfterStep
    public void afterStepExecution() {
        LOGGER.info("logger {}", stepExecution.getExecutionContext().get(ExecutionContextKey.TRIP_TOTAL.getKey()));
        JobParameters parameters = stepExecution.getJobExecution().getJobParameters();
        String directoryPath = parameters.getString(JobParametersKey.PATH_DIRECTORY.getKey());

		String csvFileName = getFilePath(directoryPath, parameters.getDate(JobParametersKey.CURRENT_TIME.getKey()));
		if(stepExecution.getStatus().equals(BatchStatus.COMPLETED)){
			generateCsvFile(writeTrips, csvFileName);
		}
        stepExecution.getExecutionContext().put(ExecutionContextKey.TRIP_TOTAL.getKey(), totalWriteTrip);
    }

	private String getFilePath(String directoryPath, Date jobCurrentTime){

		String strDate = fileDateFormat.format(jobCurrentTime);

		String fileName = MessageFormat.format("{0}_{1}.csv", CSV_BASE_NAME, strDate);

		return MessageFormat.format("{0}/{1}", directoryPath, fileName);
	}


    public String buildCsvLine(TripCsvLine trips) {
        return String.join(",", trips.getBikeId().toString(), trips.getAge().toString(), trips.getGender(), trips.getDurationTime(), trips.getStartStationName(), trips.getEndStationName());
    }

    private void generateCsvFile(List<TripCsvLine> trips, @NonNull String filePathName) {

        if (!CollectionUtils.isEmpty(trips)) {
			File csvOutputFile = new File(filePathName);

            try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                pw.println(CSV_HEADER);
                trips.stream()
                        .map(this::buildCsvLine)
                        .forEach(pw::println);
            } catch (FileNotFoundException e) {
                LOGGER.error("CSV file not found {} ", e.getMessage());
            } finally {
                writeTrips.clear();
            }
        }
    }
}