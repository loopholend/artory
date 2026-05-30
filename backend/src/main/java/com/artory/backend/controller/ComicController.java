package com.artory.backend.controller;

import com.artory.backend.model.Comic;
import com.artory.backend.model.User;
import com.artory.backend.repository.ComicRepository;
import com.artory.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/comics")
public class ComicController {

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Comic>> getAllComics() {
        return ResponseEntity.ok(comicRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createComic(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "coverImage", required = false) MultipartFile file,
            Authentication auth) {
        
        Comic comic = new Comic();
        comic.setTitle(title);
        comic.setDescription(description);
        comic.setGenre(genre);
        comic.setCoverImage("https://picsum.photos/400/600?random=" + System.currentTimeMillis());

        if (auth != null) {
            userRepository.findByEmail(auth.getName()).ifPresent(comic::setCreator);
        }

        Comic saved = comicRepository.save(comic);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeComic(@PathVariable String id, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        Optional<User> optUser = userRepository.findByEmail(auth.getName());
        if (optUser.isEmpty()) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

        Optional<Comic> optComic = comicRepository.findById(id);
        if (optComic.isEmpty()) return ResponseEntity.notFound().build();
        Comic comic = optComic.get();

        String userId = optUser.get().getId();
        boolean liked;
        if (comic.getLikes().contains(userId)) {
            comic.getLikes().remove(userId);
            liked = false;
        } else {
            comic.getLikes().add(userId);
            liked = true;
        }
        comicRepository.save(comic);
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
