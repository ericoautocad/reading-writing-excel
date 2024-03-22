package com.example.readingwritingexcel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
/*@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableMongoRepositories*/
@SpringBootApplication
public class ReadingWritingExcelApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadingWritingExcelApplication.class, args);
	}

}
