package com.artory.backend.repository;

import com.artory.backend.model.Artwork;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkRepository extends MongoRepository<Artwork, String> {
}
