package com.example.readingwritingexcel.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.example.readingwritingexcel.domain.model.TripChosen;
import com.example.readingwritingexcel.domain.repository.TripChosenRepository;

@Component
public class TripChosenService {
    
    @Autowired
    private TripChosenRepository repository;

    @Bean
    public String saveAll(List<TripChosen> trips) {

        return "Modified record quantity: " + repository.saveAll(trips).size();
    }


}
