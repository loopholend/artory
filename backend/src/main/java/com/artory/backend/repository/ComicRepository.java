package com.artory.backend.repository;

import com.artory.backend.model.Comic;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComicRepository extends MongoRepository<Comic, String> {
}
