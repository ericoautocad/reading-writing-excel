package com.example.readingwritingexcel.job.step;


import java.time.Duration;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.readingwritingexcel.domain.document.Trips;
import com.example.readingwritingexcel.domain.enums.UserGender;
import com.example.readingwritingexcel.domain.model.TripCsvLine;
import com.example.readingwritingexcel.runner.ScheduledJobLauncher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TripItemProcessor implements ItemProcessor<Trips, TripCsvLine> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledJobLauncher.class);

	@Override
	public TripCsvLine process(Trips item) {
		//LOGGER.info("Trips processor {}", item.toString());

		var age = LocalDate.now().getYear() - item.getBirthYear();
		var gender = UserGender.getType(item.getGender()).name();
		Duration duration = Duration.ofSeconds(item.getDuration());
		String formattedDuration= String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());

		return new TripCsvLine(item.getBikeId(), age, gender, formattedDuration, item.getStartStationName(), item.getEndStationName());
	}
}