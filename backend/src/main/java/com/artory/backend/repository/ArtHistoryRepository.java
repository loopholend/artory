package com.artory.backend.repository;

import com.artory.backend.model.ArtHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArtHistoryRepository extends MongoRepository<ArtHistory, String> {
}
