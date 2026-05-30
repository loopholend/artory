package com.artory.backend.repository;

import com.artory.backend.model.Community;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommunityRepository extends MongoRepository<Community, String> {
}
