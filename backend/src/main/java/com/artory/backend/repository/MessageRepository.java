package com.artory.backend.repository;

import com.artory.backend.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findBySenderId(String senderId);
    List<Message> findByReceiverId(String receiverId);
}
