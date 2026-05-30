package com.artory.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "competitions")
public class Competition {
    @Id
    private String id;
    private String title;
    private String description;
    private String category;
    private String deadline;
    private String prize;
    private String status = "pending"; // pending, approved, rejected

    @DBRef
    private User organizer;

    @DBRef
    private List<User> participants = new ArrayList<>();

    public static class Submission {
        @DBRef
        private User artist;
        private String imageUrl;
        private Date submittedAt = new Date();

        public Submission() {}

        public User getArtist() { return artist; }
        public void setArtist(User artist) { this.artist = artist; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public Date getSubmittedAt() { return submittedAt; }
        public void setSubmittedAt(Date submittedAt) { this.submittedAt = submittedAt; }
    }

    private List<Submission> submissions = new ArrayList<>();

    public static class Winner {
        private int rank;
        @DBRef
        private User artist;
        private String imageUrl;

        public Winner() {}

        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
        public User getArtist() { return artist; }
        public void setArtist(User artist) { this.artist = artist; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    private List<Winner> winners = new ArrayList<>();

    public Competition() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getPrize() { return prize; }
    public void setPrize(String prize) { this.prize = prize; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public User getOrganizer() { return organizer; }
    public void setOrganizer(User organizer) { this.organizer = organizer; }
    public List<User> getParticipants() { return participants; }
    public void setParticipants(List<User> participants) { this.participants = participants; }
    public List<Submission> getSubmissions() { return submissions; }
    public void setSubmissions(List<Submission> submissions) { this.submissions = submissions; }
    public List<Winner> getWinners() { return winners; }
    public void setWinners(List<Winner> winners) { this.winners = winners; }
}
