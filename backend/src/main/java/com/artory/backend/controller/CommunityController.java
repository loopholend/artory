package com.artory.backend.controller;

import com.artory.backend.model.Community;
import com.artory.backend.model.User;
import com.artory.backend.repository.CommunityRepository;
import com.artory.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Community>> getAllCommunities() {
        return ResponseEntity.ok(communityRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createCommunity(@RequestBody Community community) {
        community.setCoverImage("https://picsum.photos/800/400?random=" + System.currentTimeMillis());
        Community saved = communityRepository.save(community);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinCommunity(@PathVariable String id, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        
        Optional<User> optionalUser = userRepository.findByEmail(auth.getName());
        if (optionalUser.isEmpty()) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        User user = optionalUser.get();

        Optional<Community> optionalCommunity = communityRepository.findById(id);
        if (optionalCommunity.isEmpty()) return ResponseEntity.status(404).body(Map.of("message", "Community not found"));
        Community community = optionalCommunity.get();

        boolean isMember = community.getMembers().stream().anyMatch(m -> m.getId().equals(user.getId()));
        
        if (isMember) {
            community.getMembers().removeIf(m -> m.getId().equals(user.getId()));
            communityRepository.save(community);
            return ResponseEntity.ok(Map.of("joined", false));
        } else {
            if (community.isPrivate()) {
                boolean hasRequested = community.getJoinRequests().stream().anyMatch(r -> r.getUser().getId().equals(user.getId()));
                if (hasRequested) {
                    community.getJoinRequests().removeIf(r -> r.getUser().getId().equals(user.getId()));
                    communityRepository.save(community);
                    return ResponseEntity.ok(Map.of("cancelled", true, "joined", false));
                } else {
                    community.getJoinRequests().add(new Community.JoinRequest(user));
                    communityRepository.save(community);
                    return ResponseEntity.ok(Map.of("requested", true, "joined", false));
                }
            } else {
                community.getMembers().add(user);
                communityRepository.save(community);
                return ResponseEntity.ok(Map.of("joined", true));
            }
        }
    }
}
