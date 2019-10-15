package com.example.applicationsecond.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Chat {

    private String message;
    private Date dateCreated;

    public Chat() { }

    public Chat(String message) {
        this.message = message;
    }

    // --- GETTERS ---
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }


    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
}
