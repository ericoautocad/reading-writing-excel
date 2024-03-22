package com.example.readingwritingexcel.job.step;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.readingwritingexcel.domain.model.TripChosen;
import com.example.readingwritingexcel.domain.model.TripCsvLine;
import com.example.readingwritingexcel.runner.ScheduledJobLauncher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TripItemFileProcessor implements ItemProcessor<TripCsvLine, TripChosen> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledJobLauncher.class);

	@Override
	public TripChosen process(TripCsvLine item) {
		// LOGGER.info("Trip processing bikeID >>>>>>>>>>>>>>>>>>>>>>>>>>>{}", item.getBikeId());

		// lineTokenizer.setNames("bike ID", "Age", "Gender", "Trip Duration", "Start Station", "End Station");
        //                               22794,     63,    FEMALE,    00:14:49,       Howard St & Centre St,   South End Ave & Liberty St

		// Duration duration = Duration.ofSeconds(item.getDuration());

        // Integer bikeId,
        // Integer age,
        // String gender,
        // String durationTime,
        // String startStationName,
        // String endStationName

		// String formattedDuration= String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());

		
		return new TripChosen(item.getBikeId(), item.getAge(), item.getGender(), item.getDurationTime(), item.getStartStationName(), item.getEndStationName());
	}
}