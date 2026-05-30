package com.artory.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "comics")
public class Comic {
    @Id
    private String id;
    private String title;
    private String description;
    private String genre;
    private String coverImage;

    @DBRef
    private User creator;

    private List<String> likes = new ArrayList<>(); // User IDs
    
    public static class Episode {
        private String title;
        private String imageUrl;
        public Episode() {}
        public Episode(String title, String imageUrl) {
            this.title = title;
            this.imageUrl = imageUrl;
        }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
    
    private List<Episode> episodes = new ArrayList<>();

    public Comic() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
    public List<String> getLikes() { return likes; }
    public void setLikes(List<String> likes) { this.likes = likes; }
    public List<Episode> getEpisodes() { return episodes; }
    public void setEpisodes(List<Episode> episodes) { this.episodes = episodes; }
}
