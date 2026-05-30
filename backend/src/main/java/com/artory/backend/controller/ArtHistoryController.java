package com.artory.backend.controller;

import com.artory.backend.model.ArtHistory;
import com.artory.backend.repository.ArtHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/arthistory")
public class ArtHistoryController {

    @Autowired
    private ArtHistoryRepository artHistoryRepository;

    @GetMapping
    public ResponseEntity<List<ArtHistory>> getAll() {
        return ResponseEntity.ok(artHistoryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ArtHistory artHistory) {
        ArtHistory saved = artHistoryRepository.save(artHistory);
        return ResponseEntity.ok(saved);
    }
}
