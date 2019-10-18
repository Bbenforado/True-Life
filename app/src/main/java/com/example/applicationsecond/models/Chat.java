package com.example.applicationsecond.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Chat {

    private Message lastMessage;
    private Date dateCreated;
    private List<String> involvedUsers;

    public Chat() { }

    public Chat(Message message) {
        this.lastMessage = message;
    }

    // --- GETTERS ---
    public Message getLastMessage() { return lastMessage; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }


    // --- SETTERS ---
    public void setMessage(Message message) { this.lastMessage = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public List<String> getInvolvedUsers() {
        return involvedUsers;
    }

    public void setInvolvedUsers(List<String> involvedUsers) {
        this.involvedUsers = involvedUsers;
    }
}
