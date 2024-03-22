package com.example.readingwritingexcel.job.step;

import java.rmi.UnexpectedException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import com.example.readingwritingexcel.domain.model.TripCsvLine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TripItemFileReader implements ItemReader<FlatFileItemReader<TripCsvLine>>, StepExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TripItemFileReader.class);

    private String inputFile;


    @Override
    public FlatFileItemReader<TripCsvLine>  read() throws Exception, UnexpectedException, ParseException, NonTransientResourceException {

        
        // FlatFileItemReader<TripCsvLine> itemReader = new FlatFileItemReader<>();
        // itemReader.setResource(new FileSystemResource("data-out/" + this.inputFile));
        // itemReader.setName("csvReader");
        // itemReader.setLinesToSkip(1);

        // DefaultLineMapper<TripCsvLine> lineMapper = new DefaultLineMapper<TripCsvLine>();
        // DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        // lineTokenizer.setDelimiter(",");
        // lineTokenizer
        //         .setNames(new String[] { "bike ID", "Age", "Gender", "Trip Duration", "Start Station", "End Station" });
        // // 22794, 63, FEMALE, 00:14:49, Howard St & Centre St,South End Ave & Liberty St
        // BeanWrapperFieldSetMapper<TripCsvLine> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        // fieldSetMapper.setTargetType(TripCsvLine.class);

        // lineMapper.setLineTokenizer(lineTokenizer);
        // lineMapper.setFieldSetMapper(fieldSetMapper);

        // itemReader.setLineMapper(lineMapper);

        // return itemReader;
        String fileName = this.inputFile;
        
        return new FlatFileItemReaderBuilder<TripCsvLine>()
            .name("csvReader")
            .resource(new FileSystemResource("data-out/" + fileName))
            .delimited()
            .delimiter(",")
            .names(new String[] {"bikeID", "Age", "Gender", "TripDuration", "StartStation", "EndStation"})
            .linesToSkip(1)
            .fieldSetMapper(fieldSet -> new TripCsvLine(
                fieldSet.readInt("bikeID"),
                fieldSet.readInt("Age"),
                fieldSet.readString("Gender"),
                fieldSet.readString("TripDuration"),
                fieldSet.readString("StartStation"),
                fieldSet.readString("EndStation")
            )).beanMapperStrict(false)

            .build();
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

        JobParameters jobParameters = stepExecution.getJobParameters();
        String filePath = jobParameters.getString("fileName");
        LOGGER.info("filePath = [{}].", filePath);

        this.inputFile = filePath;

    }


}
