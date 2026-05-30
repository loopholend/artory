package com.artory.backend.component;

import com.artory.backend.model.Artwork;
import com.artory.backend.model.Message;
import com.artory.backend.model.Role;
import com.artory.backend.model.User;
import com.artory.backend.model.Community;
import com.artory.backend.model.Competition;
import com.artory.backend.model.Comic;
import com.artory.backend.repository.ArtworkRepository;
import com.artory.backend.repository.MessageRepository;
import com.artory.backend.repository.UserRepository;
import com.artory.backend.repository.CommunityRepository;
import com.artory.backend.repository.CompetitionRepository;
import com.artory.backend.repository.ComicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        userRepository.deleteAll();
        artworkRepository.deleteAll();
        messageRepository.deleteAll();
        communityRepository.deleteAll();
        competitionRepository.deleteAll();
        comicRepository.deleteAll();
        
        seedUsers();
        seedArtworks();
        seedMessages();
        seedCommunities();
        seedCompetitions();
        seedComics();
    }

    private void seedUsers() {
        System.out.println("Seeding users...");
        
        // 1. Admin
        User admin = new User("admin", "admin@artory.com", passwordEncoder.encode("admin"), Role.ROLE_ADMIN);
        userRepository.save(admin);

        // 2. Mock Users (Artists and normal users)
        String[] usernames = {"picasso", "davinci", "vangogh", "art_lover", "collector_bob"};
        Role[] roles = {Role.ROLE_ARTIST, Role.ROLE_ARTIST, Role.ROLE_ARTIST, Role.ROLE_USER, Role.ROLE_USER};

        for (int i = 0; i < 5; i++) {
            User u = new User(usernames[i], usernames[i] + "@artory.com", passwordEncoder.encode("password123"), roles[i]);
            userRepository.save(u);
        }
        
        System.out.println("Users seeded successfully!");
    }

    private void seedArtworks() {
        System.out.println("Seeding 50 artworks...");
        List<Artwork> artworks = new ArrayList<>();
        Random random = new Random();

        String[] artists = {"picasso", "davinci", "vangogh", "modern_artist_99"};
        String[] categories = {"Painting", "Digital Art", "Sculpture", "Photography"};

        for (int i = 1; i <= 50; i++) {
            String title = "Artwork " + i;
            String artist = artists[random.nextInt(artists.length)];
            String description = "A beautiful masterpiece representing abstract thoughts. #" + i;
            double price = 100.0 + (random.nextDouble() * 900.0); // Random price between 100 and 1000
            String category = categories[random.nextInt(categories.length)];
            String imageUrl = "https://picsum.photos/400/300?random=" + i;

            // Make about 20% of them "Historical" category
            if (i % 5 == 0) {
                category = "Historical";
                description = "An ancient artifact of great historical significance. Originally created centuries ago.";
            }

            Artwork artwork = new Artwork(title, artist, description, Math.round(price * 100.0) / 100.0, imageUrl, category);
            artworks.add(artwork);
        }

        artworkRepository.saveAll(artworks);
        System.out.println("Artworks seeded successfully!");
    }

    private void seedMessages() {
        System.out.println("Seeding mock messages between users...");
        
        // Let's get the users to send messages between them
        List<User> users = userRepository.findAll();
        if(users.size() < 2) return;

        User u1 = users.get(1); // picasso
        User u2 = users.get(2); // davinci

        List<Message> messages = new ArrayList<>();
        
        messages.add(new Message(u1.getId(), u2.getId(), "Hey Leo, I really love your new sketch!", new Date(System.currentTimeMillis() - 100000)));
        messages.add(new Message(u2.getId(), u1.getId(), "Thanks Pablo! Your cubism work is inspiring too.", new Date(System.currentTimeMillis() - 50000)));
        
        User collector = users.get(4); // art_lover or collector_bob
        messages.add(new Message(collector.getId(), u1.getId(), "Hello, is 'Artwork 5' still available for purchase?", new Date(System.currentTimeMillis() - 20000)));
        messages.add(new Message(u1.getId(), collector.getId(), "Yes it is! Let me know if you need more details.", new Date()));

        messageRepository.saveAll(messages);
        System.out.println("Messages seeded successfully!");
    }

    private void seedCommunities() {
        System.out.println("Seeding communities...");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) return;

        String[] categories = {"Digital Art", "Photography", "Concept Art", "3D Modeling"};
        Random random = new Random();

        for (int i = 1; i <= 5; i++) {
            Community c = new Community();
            c.setName("Community " + i);
            c.setDescription("A great place for " + categories[random.nextInt(categories.length)] + " artists.");
            c.setCategory(categories[random.nextInt(categories.length)]);
            c.setPrivate(i % 3 == 0);
            c.setCoverImage("https://picsum.photos/800/400?random=" + (i + 100));
            
            List<User> members = new ArrayList<>();
            members.add(users.get(0));
            if (users.size() > 1) members.add(users.get(random.nextInt(users.size() - 1) + 1));
            c.setMembers(members);
            
            communityRepository.save(c);
        }
        System.out.println("Communities seeded successfully!");
    }

    private void seedCompetitions() {
        System.out.println("Seeding competitions...");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) return;

        String[] titles = {"Cyberpunk Cityscape", "Fantasy Character Design", "Abstract Expressions"};
        
        for (int i = 0; i < titles.length; i++) {
            Competition c = new Competition();
            c.setTitle(titles[i]);
            c.setDescription("Create your best artwork based on this theme to win amazing prizes!");
            c.setCategory("Digital Art");
            c.setDeadline("2027-12-31");
            c.setPrize((i + 1) * 1000 + " Credits");
            c.setStatus("approved");
            c.setOrganizer(users.get(0));
            
            List<User> participants = new ArrayList<>();
            participants.add(users.get(0));
            if (users.size() > 1) participants.add(users.get(1));
            c.setParticipants(participants);
            
            competitionRepository.save(c);
        }
        System.out.println("Competitions seeded successfully!");
    }

    private void seedComics() {
        System.out.println("Seeding comics...");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) return;

        for (int i = 1; i <= 6; i++) {
            Comic c = new Comic();
            c.setTitle("Epic Saga " + i);
            c.setDescription("Follow the adventures of a young hero in a digital world.");
            c.setGenre("Sci-Fi / Action");
            c.setCoverImage("https://picsum.photos/400/600?random=" + (i + 200));
            c.setCreator(users.get(i % users.size()));
            
            List<Comic.Episode> episodes = new ArrayList<>();
            episodes.add(new Comic.Episode("Episode 1", "https://picsum.photos/400/800?random=" + (i + 300)));
            episodes.add(new Comic.Episode("Episode 2", "https://picsum.photos/400/800?random=" + (i + 301)));
            c.setEpisodes(episodes);
            
            comicRepository.save(c);
        }
        System.out.println("Comics seeded successfully!");
    }
}
