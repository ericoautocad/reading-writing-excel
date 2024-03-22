package com.example.readingwritingexcel.job;

import static com.example.readingwritingexcel.domain.constant.BatchConstants.*;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.readingwritingexcel.domain.document.Trips;
import com.example.readingwritingexcel.domain.model.TripChosen;
import com.example.readingwritingexcel.domain.model.TripCsvLine;
import com.example.readingwritingexcel.job.step.TripItemFileProcessor;
import com.example.readingwritingexcel.job.step.TripItemFileWriter;
import com.example.readingwritingexcel.job.step.TripItemProcessor;
import com.example.readingwritingexcel.job.step.TripItemReader;
import com.example.readingwritingexcel.job.step.TripItemWriter;
import com.example.readingwritingexcel.job.step.TripStepListener;


@Configuration
public class JobConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobConfig.class);


    @Bean
    public DataSource getDataSource() {
        return new EmbeddedDatabaseBuilder()
                .addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
                .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }


    @Bean
    public Job tripJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                       MongoTemplate mongoTemplate, @Value("#jobExecutionContext['fileName']") String fileName) {
        return new JobBuilder("tripJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tripJobStep(jobRepository, transactionManager, mongoTemplate))
                .next(tripJobInsertStep(jobRepository, transactionManager, mongoTemplate, fileName))
                .listener(new TripJobCompletionListener())
                .build();
    }

    @Bean
    public Step tripJobStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                            MongoTemplate mongoTemplate) {
        return new StepBuilder("tripJobCSVGenerator", jobRepository)
                .startLimit(DEFAULT_LIMIT_SIZE)
                .<Trips, TripCsvLine>chunk(DEFAULT_CHUNK_SIZE, transactionManager)

                .reader(new TripItemReader(mongoTemplate))
                .processor(new TripItemProcessor())
                .writer(new TripItemWriter())
                .listener(new TripStepListener())
                .build();
    }

    @Bean
    public Step tripJobInsertStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                            MongoTemplate mongoTemplate, String fileName) {

        return new StepBuilder("tripJobCSVInsert", jobRepository)
                .startLimit(DEFAULT_LIMIT_SIZE)
                .<TripCsvLine, TripChosen>chunk(DEFAULT_CHUNK_SIZE, transactionManager)

                // .reader(new TripItemFileReader())
                .reader(itemReader(fileName))
                .processor(new TripItemFileProcessor())
                .writer(new TripItemFileWriter(mongoTemplate))
                .listener(new TripStepListener())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<TripCsvLine> itemReader(@Value("#{jobParameters['fileName']}") String fileName) {
        
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
}