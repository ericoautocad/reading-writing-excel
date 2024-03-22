package com.example.readingwritingexcel.job.step;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.example.readingwritingexcel.domain.model.TripChosen;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TripItemFileWriter implements ItemWriter<TripChosen>, StepExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TripItemFileWriter.class);

    private int totalWriteTrip = 0;

    private final List<TripChosen> writeTrips = new ArrayList<>();

    @Autowired
    private MongoTemplate template;
    

    public TripItemFileWriter(@Autowired MongoTemplate template) {

        super();

        this.template = template;
    }



    @Override
    public void write(Chunk<? extends TripChosen> tripsChunk) {
        totalWriteTrip += tripsChunk.getItems().size();
        writeTrips.addAll(tripsChunk.getItems());

        LOGGER.info(">>>>>>>>>>>>>>> Total quantity of items in the batch: {} >>>>>>>>>>>>>>>>>>>>", totalWriteTrip);
        
        template.insertAll(writeTrips);

    }

}