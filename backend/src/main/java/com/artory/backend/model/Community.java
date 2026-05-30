package com.artory.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "communities")
public class Community {
    @Id
    private String id;
    private String name;
    private String description;
    private String category;
    private boolean isPrivate;
    private String coverImage;
    private Date updatedAt = new Date();

    @DBRef
    private List<User> members = new ArrayList<>();

    public static class JoinRequest {
        @DBRef
        private User user;
        private Date requestedAt;

        public JoinRequest() {}
        public JoinRequest(User user) {
            this.user = user;
            this.requestedAt = new Date();
        }

        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        public Date getRequestedAt() { return requestedAt; }
        public void setRequestedAt(Date requestedAt) { this.requestedAt = requestedAt; }
    }

    private List<JoinRequest> joinRequests = new ArrayList<>();

    public Community() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public List<User> getMembers() { return members; }
    public void setMembers(List<User> members) { this.members = members; }
    public List<JoinRequest> getJoinRequests() { return joinRequests; }
    public void setJoinRequests(List<JoinRequest> joinRequests) { this.joinRequests = joinRequests; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
