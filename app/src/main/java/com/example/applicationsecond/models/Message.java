package com.example.applicationsecond.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {
    private String message;
    private String id;
    private Date dateCreated;
    private User userSender;
    private String urlImage;
    private String chatName;
    private long dateInMilliseconds;

    public Message() { }

    public Message(String id, String message, User userSender, String chatName, long dateInMilliseconds) {
        this.id = id;
        this.message = message;
        this.userSender = userSender;
        this.chatName = chatName;
        this.dateInMilliseconds = dateInMilliseconds;
    }

    // --- GETTERS ---
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    public User getUserSender() { return userSender; }
    public String getUrlImage() { return urlImage; }


    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(User userSender) { this.userSender = userSender; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public long getDateInMilliseconds() {
        return dateInMilliseconds;
    }

    public void setDateInMilliseconds(long dateInMilliseconds) {
        this.dateInMilliseconds = dateInMilliseconds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
