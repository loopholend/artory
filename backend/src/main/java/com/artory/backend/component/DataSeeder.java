package com.artory.backend.component;

import com.artory.backend.model.*;
import com.artory.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private ArtworkRepository artworkRepository;
    @Autowired private MessageRepository messageRepository;
    @Autowired private CommunityRepository communityRepository;
    @Autowired private CompetitionRepository competitionRepository;
    @Autowired private ComicRepository comicRepository;
    @Autowired private ArtHistoryRepository artHistoryRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        userRepository.deleteAll();
        artworkRepository.deleteAll();
        messageRepository.deleteAll();
        communityRepository.deleteAll();
        competitionRepository.deleteAll();
        comicRepository.deleteAll();
        artHistoryRepository.deleteAll();

        seedUsers();
        seedArtworks();
        seedMessages();
        seedCommunities();
        seedCompetitions();
        seedComics();
        seedArtHistory();
    }

    private void seedUsers() {
        System.out.println("Seeding users...");

        User admin = new User("admin", "admin@artory.com", passwordEncoder.encode("admin"), Role.ROLE_ADMIN);
        admin.setBio("Platform administrator of Artory.");
        userRepository.save(admin);

        String[] usernames   = {"picasso",          "davinci",           "vangogh",            "art_lover",         "collector_bob"};
        String[] bios        = {"Cubism pioneer.",  "Renaissance master.","Post-impressionist.","Art enthusiast.",   "Art collector."};
        String[] skills[]    = {{"Cubism","Oil"},   {"Sculpture","Paint"},{"Watercolor","Oil"}, {"Photography"},     {"Collecting"}};
        Role[]   roles       = {Role.ROLE_ARTIST,   Role.ROLE_ARTIST,    Role.ROLE_ARTIST,     Role.ROLE_USER,      Role.ROLE_USER};

        for (int i = 0; i < 5; i++) {
            User u = new User(usernames[i], usernames[i] + "@artory.com", passwordEncoder.encode("password123"), roles[i]);
            u.setBio(bios[i]);
            u.setSkills(Arrays.asList(skills[i]));
            u.setProfileImage("https://api.dicebear.com/7.x/avataaars/svg?seed=" + usernames[i]);
            userRepository.save(u);
        }
        System.out.println("Users seeded successfully!");
    }

    private void seedArtworks() {
        System.out.println("Seeding 50 artworks...");
        List<Artwork> artworks = new ArrayList<>();
        Random random = new Random();
        String[] artists    = {"picasso", "davinci", "vangogh", "art_lover"};
        String[] categories = {"Painting", "Digital Art", "Sculpture", "Photography", "Historical"};
        String[] titles     = {"Whispers of Light","Neon Reverie","Crimson Echo","The Void Within","Shattered Dreams",
                               "Golden Horizon","Midnight Garden","Electric Storm","Silent Ruins","Crystal Pulse"};

        for (int i = 1; i <= 50; i++) {
            String title    = titles[(i - 1) % titles.length] + " " + i;
            String artist   = artists[random.nextInt(artists.length)];
            String category = (i % 5 == 0) ? "Historical" : categories[random.nextInt(categories.length - 1)];
            String desc     = (i % 5 == 0)
                ? "An ancient artifact of great historical significance. Originally created centuries ago."
                : "A stunning piece blending emotion and technique. Series piece #" + i + ".";
            double price    = Math.round((100.0 + random.nextDouble() * 900.0) * 100.0) / 100.0;
            String imageUrl = "https://picsum.photos/500/400?random=" + (i * 7 + 13);

            artworks.add(new Artwork(title, artist, desc, price, imageUrl, category));
        }
        artworkRepository.saveAll(artworks);
        System.out.println("Artworks seeded successfully!");
    }

    private void seedMessages() {
        System.out.println("Seeding messages...");
        List<User> users = userRepository.findAll();
        if (users.size() < 3) return;

        User u1 = users.get(1);
        User u2 = users.get(2);
        User collector = users.get(4);

        List<Message> msgs = Arrays.asList(
            new Message(u1.getId(), u2.getId(), "Hey Leo, I love your new sketch!", new Date(System.currentTimeMillis() - 100000)),
            new Message(u2.getId(), u1.getId(), "Thanks Pablo! Your cubism work is inspiring.", new Date(System.currentTimeMillis() - 50000)),
            new Message(collector.getId(), u1.getId(), "Is 'Artwork 5' still available?", new Date(System.currentTimeMillis() - 20000)),
            new Message(u1.getId(), collector.getId(), "Yes! Let me know if you need more details.", new Date())
        );
        messageRepository.saveAll(msgs);
        System.out.println("Messages seeded successfully!");
    }

    private void seedCommunities() {
        System.out.println("Seeding communities...");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) return;

        Object[][] data = {
            {"Digital Dreamers",      "A hub for digital artists to share tips, tutorials, and feedback.",         "Digital Art",  false},
            {"Photography Society",   "Capturing the world one frame at a time. Join fellow photographers.",       "Photography",  false},
            {"Concept Art Guild",     "Private elite group for concept artists working in games and films.",        "Concept Art",  true},
            {"3D Sculptors Lounge",   "Everything about 3D modeling, sculpting, and rendering techniques.",        "3D Modeling",  false},
            {"Abstract Minds",        "Explore the boundaries of abstract and experimental art forms together.",   "Abstract Art", false},
            {"Street Art Collective", "Urban and street art community. Share your work from around the world.",    "Street Art",   true},
        };

        for (int i = 0; i < data.length; i++) {
            Community c = new Community();
            c.setName((String) data[i][0]);
            c.setDescription((String) data[i][1]);
            c.setCategory((String) data[i][2]);
            c.setPrivate((boolean) data[i][3]);
            c.setCoverImage("https://picsum.photos/800/400?random=" + (i * 31 + 101));
            c.setUpdatedAt(new Date());

            List<User> members = new ArrayList<>();
            members.add(users.get(0));
            for (int j = 1; j < Math.min(users.size(), i + 2); j++) members.add(users.get(j));
            c.setMembers(members);

            communityRepository.save(c);
        }
        System.out.println("Communities seeded successfully!");
    }

    private void seedCompetitions() {
        System.out.println("Seeding competitions...");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) return;

        Object[][] data = {
            {"Cyberpunk Cityscape Challenge",  "Design a futuristic city bathed in neon. Show your cyberpunk vision!",             "Digital Art",   "2027-06-30", "5,000 Credits + Verification Badge"},
            {"Fantasy Character Design",       "Create an original fantasy character — hero, villain or creature.",                "Concept Art",   "2027-07-31", "3,000 Credits + Feature on Homepage"},
            {"Abstract Expressions Open",      "Purely abstract — no rules, just emotion. Surprise the judges.",                  "Abstract Art",  "2027-08-15", "2,000 Credits + Art History Feature"},
            {"Photography: Urban Life",        "Capture life in the city. Street photography, architecture, people.",             "Photography",   "2027-09-01", "1,500 Credits + Portfolio Review"},
            {"Historical Art Reimagined",      "Take a famous historical artwork and reimagine it in a modern digital style.",    "Historical",    "2027-10-01", "4,000 Credits + Mentor Session"},
        };

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < data.length; i++) {
            Competition c = new Competition();
            c.setTitle((String) data[i][0]);
            c.setDescription((String) data[i][1]);
            c.setCategory((String) data[i][2]);
            c.setDeadline((String) data[i][3]);
            c.setPrize((String) data[i][4]);
            c.setStatus("approved");
            c.setOrganizer(users.get(0));

            List<User> participants = new ArrayList<>();
            for (int j = 1; j < Math.min(users.size(), i + 3); j++) participants.add(users.get(j));
            c.setParticipants(participants);

            competitionRepository.save(c);
        }
        System.out.println("Competitions seeded successfully!");
    }

    private void seedComics() {
        System.out.println("Seeding comics...");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) return;

        Object[][] data = {
            {"Neon Samurai",         "A disgraced samurai reborn as a cyberpunk warrior seeks revenge in Neo-Tokyo.",   "Sci-Fi / Action",  0},
            {"The Last Alchemist",   "Magic is dying, and only one young alchemist can reignite the ancient flame.",     "Fantasy",          1},
            {"Galactic Wanderer",    "A lone space explorer discovers an ancient alien civilization at the edge of time.", "Sci-Fi",          2},
            {"Street Chronicles",   "Stories from the underground art scene — passion, struggle, and expression.",       "Drama",            3},
            {"Pixel Phantom",        "A digital ghost haunts the internet, correcting injustices one hack at a time.",   "Thriller",         1},
            {"Dragon Architects",   "Builders and dragons must unite to construct a bridge between two warring worlds.", "Fantasy / Adventure", 2},
        };

        for (int i = 0; i < data.length; i++) {
            Comic c = new Comic();
            c.setTitle((String) data[i][0]);
            c.setDescription((String) data[i][1]);
            c.setGenre((String) data[i][2]);
            c.setCoverImage("https://picsum.photos/400/600?random=" + (i * 17 + 201));
            c.setCreator(users.get((int) data[i][3] % users.size()));

            List<Comic.Episode> eps = new ArrayList<>();
            for (int e = 1; e <= 3; e++) {
                eps.add(new Comic.Episode("Episode " + e + ": " + getTitleForEp(i, e),
                        "https://picsum.photos/500/800?random=" + (i * 17 + e * 5 + 300)));
            }
            c.setEpisodes(eps);
            comicRepository.save(c);
        }
        System.out.println("Comics seeded successfully!");
    }

    private String getTitleForEp(int comic, int ep) {
        String[][] eps = {
            {"The Fall","Rise of the Shadow","Final Circuit"},
            {"The Spark","Forbidden Formula","The Great Blast"},
            {"First Contact","The Signal","Warp Drive"},
            {"Concrete Jungle","The Gallery","Recognition"},
            {"Ghost Protocol","The Firewall","Digital Justice"},
            {"Blueprint","Stone & Scale","The Grand Span"},
        };
        return eps[comic % eps.length][(ep - 1) % 3];
    }

    private void seedArtHistory() {
        System.out.println("Seeding art history...");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Object[][] data = {
            {"The Starry Night",         "Vincent van Gogh painted this iconic masterpiece in June 1889 while staying at the Saint-Paul-de-Mausole asylum. The swirling night sky over a village has become one of the most recognized works of Western art.",     "https://picsum.photos/900/500?random=501", "1889-06-18"},
            {"The Mona Lisa",            "Leonardo da Vinci's enigmatic portrait of Lisa Gherardini is believed to have been painted between 1503 and 1519. Known for its subject's mysterious smile and use of sfumato, it now hangs in the Louvre.",            "https://picsum.photos/900/500?random=502", "1503-01-01"},
            {"The Last Supper",          "Painted by Leonardo da Vinci from 1495 to 1498, this mural depicts the moment Jesus announces that one of his apostles will betray him. It is housed in the refectory of Santa Maria delle Grazie, Milan.",             "https://picsum.photos/900/500?random=503", "1495-01-01"},
            {"Guernica",                 "Pablo Picasso created this powerful anti-war statement in 1937 in response to the Nazi bombing of the Basque town of Guernica. In grey, black, and white, it captures suffering and chaos with cubist fragmentation.",    "https://picsum.photos/900/500?random=504", "1937-04-26"},
            {"The Birth of Venus",       "Sandro Botticelli's masterpiece from around 1484–1486 depicts the goddess Venus emerging from the sea as a fully grown woman. It is celebrated as one of the greatest works of the Italian Renaissance.",               "https://picsum.photos/900/500?random=505", "1485-01-01"},
            {"The Persistence of Memory","Salvador Dalí's iconic 1931 Surrealist work features melting watches draped over a barren landscape, exploring themes of time and the unconscious mind. It is now in the Museum of Modern Art, New York.",              "https://picsum.photos/900/500?random=506", "1931-01-01"},
            {"Girl with a Pearl Earring","Johannes Vermeer's 1665 masterpiece is often called the 'Mona Lisa of the North.' The mysterious girl, her large pearl earring, and direct gaze make it one of the most captivating portraits in Western art.",         "https://picsum.photos/900/500?random=507", "1665-01-01"},
            {"The Scream",               "Edvard Munch created four versions of this iconic image between 1893 and 1910. The agonized figure against a blood-red sky has become a universal symbol of anxiety and existential dread in modern civilization.",     "https://picsum.photos/900/500?random=508", "1893-01-01"},
        };

        for (Object[] d : data) {
            ArtHistory h = new ArtHistory();
            h.setTitle((String) d[0]);
            h.setDescription((String) d[1]);
            h.setImageUrl((String) d[2]);
            try { h.setHistoryDate(sdf.parse((String) d[3])); } catch (Exception ignored) {}
            h.setAuthor("admin");
            artHistoryRepository.save(h);
        }
        System.out.println("Art history seeded successfully!");
    }
}
