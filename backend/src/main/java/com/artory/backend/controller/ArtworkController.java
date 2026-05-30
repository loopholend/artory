package com.artory.backend.controller;

import com.artory.backend.model.Artwork;
import com.artory.backend.repository.ArtworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    @Autowired
    private ArtworkRepository artworkRepository;

    @GetMapping
    public ResponseEntity<List<Artwork>> getAllArtworks() {
        try {
            List<Artwork> artworks = artworkRepository.findAll();
            if (artworks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(artworks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artwork> getArtworkById(@PathVariable("id") String id) {
        Optional<Artwork> artworkData = artworkRepository.findById(id);
        if (artworkData.isPresent()) {
            return new ResponseEntity<>(artworkData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Artwork> createArtwork(@RequestBody Artwork artwork) {
        try {
            Artwork _artwork = artworkRepository.save(new Artwork(artwork.getTitle(), artwork.getArtist(), artwork.getDescription(), artwork.getPrice(), artwork.getImageUrl(), artwork.getCategory()));
            return new ResponseEntity<>(_artwork, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artwork> updateArtwork(@PathVariable("id") String id, @RequestBody Artwork artwork) {
        Optional<Artwork> artworkData = artworkRepository.findById(id);

        if (artworkData.isPresent()) {
            Artwork _artwork = artworkData.get();
            _artwork.setTitle(artwork.getTitle());
            _artwork.setArtist(artwork.getArtist());
            _artwork.setDescription(artwork.getDescription());
            _artwork.setPrice(artwork.getPrice());
            _artwork.setImageUrl(artwork.getImageUrl());
            _artwork.setCategory(artwork.getCategory());
            return new ResponseEntity<>(artworkRepository.save(_artwork), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteArtwork(@PathVariable("id") String id) {
        try {
            artworkRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
