package com.artory.backend.controller;

import com.artory.backend.model.Competition;
import com.artory.backend.model.User;
import com.artory.backend.repository.CompetitionRepository;
import com.artory.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/competition")
public class CompetitionController {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Competition>> getAllCompetitions() {
        return ResponseEntity.ok(competitionRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createCompetition(@RequestBody Competition competition, Authentication auth) {
        if (auth != null) {
            userRepository.findByEmail(auth.getName()).ifPresent(competition::setOrganizer);
        }
        competition.setStatus("pending");
        Competition saved = competitionRepository.save(competition);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveCompetition(@PathVariable String id, @RequestBody Map<String, String> body) {
        Optional<Competition> opt = competitionRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Competition c = opt.get();
        c.setStatus(body.get("status"));
        return ResponseEntity.ok(competitionRepository.save(c));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinCompetition(@PathVariable String id, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        Optional<User> optUser = userRepository.findByEmail(auth.getName());
        if (optUser.isEmpty()) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        
        Optional<Competition> optComp = competitionRepository.findById(id);
        if (optComp.isEmpty()) return ResponseEntity.notFound().build();
        Competition c = optComp.get();
        
        if (c.getParticipants().stream().noneMatch(p -> p.getId().equals(optUser.get().getId()))) {
            c.getParticipants().add(optUser.get());
            competitionRepository.save(c);
        }
        return ResponseEntity.ok(Map.of("joined", true));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitToCompetition(@PathVariable String id, @RequestParam("submissionImage") MultipartFile file, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        Optional<User> optUser = userRepository.findByEmail(auth.getName());
        if (optUser.isEmpty()) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

        Optional<Competition> optComp = competitionRepository.findById(id);
        if (optComp.isEmpty()) return ResponseEntity.notFound().build();
        Competition c = optComp.get();

        Competition.Submission submission = new Competition.Submission();
        submission.setArtist(optUser.get());
        submission.setImageUrl("https://picsum.photos/600/600?random=" + System.currentTimeMillis());
        submission.setSubmittedAt(new Date());

        c.getSubmissions().add(submission);
        competitionRepository.save(c);
        return ResponseEntity.ok(Map.of("submitted", true));
    }

    @GetMapping("/{id}/submissions")
    public ResponseEntity<?> getSubmissions(@PathVariable String id) {
        Optional<Competition> optComp = competitionRepository.findById(id);
        if (optComp.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(optComp.get().getSubmissions());
    }

    @PostMapping("/{id}/winners")
    public ResponseEntity<?> pickWinners(@PathVariable String id, @RequestBody Map<String, List<Map<String, Object>>> body) {
        Optional<Competition> optComp = competitionRepository.findById(id);
        if (optComp.isEmpty()) return ResponseEntity.notFound().build();
        Competition c = optComp.get();

        List<Map<String, Object>> winnersData = body.get("winners");
        List<Competition.Winner> winnersList = winnersData.stream().map(w -> {
            Competition.Winner winner = new Competition.Winner();
            winner.setRank((Integer) w.get("rank"));
            userRepository.findById((String) w.get("artistId")).ifPresent(winner::setArtist);
            return winner;
        }).collect(Collectors.toList());

        c.setWinners(winnersList);
        Competition saved = competitionRepository.save(c);
        return ResponseEntity.ok(Map.of("winners", saved.getWinners()));
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<?> endCompetition(@PathVariable String id) {
        Optional<Competition> optComp = competitionRepository.findById(id);
        if (optComp.isEmpty()) return ResponseEntity.notFound().build();
        Competition c = optComp.get();
        // set deadline to now to end it
        c.setDeadline(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        competitionRepository.save(c);
        return ResponseEntity.ok(Map.of("comp", c));
    }
}
