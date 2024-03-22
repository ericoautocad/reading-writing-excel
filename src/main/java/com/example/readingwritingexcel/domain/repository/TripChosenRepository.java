package com.example.readingwritingexcel.domain.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.readingwritingexcel.domain.model.TripChosen;

@Repository
public interface TripChosenRepository extends MongoRepository<TripChosen, Integer> {

    public List<TripChosen> findAll();
}

