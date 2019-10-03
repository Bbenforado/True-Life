package com.example.applicationsecond.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Project {

    private String id;
    private String title;
    private Date creationDate;
    private Date eventDate;
    private Date endDate;
    private String description;
    private String urlPhoto;
    private String idAddress;
    private String authorId;
    private boolean isPublished;
    private List<String> usersWhoSubscribed;

    public Project() {

    }


    public Project(String title, Date creationDate, Date eventDate, Date endDate,
                   String description, String urlPhoto, String idAddress) {
        this.title = title;
        this.creationDate = creationDate;
        this.eventDate = eventDate;
        this.endDate = endDate;
        this.description = description;
        this.urlPhoto = urlPhoto;
        this.idAddress = idAddress;
    }

    public Project(String title, String description, String authorId, Date creationDate) {
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.creationDate = creationDate;
    }

    public Project(String id, String title, String description, String authorId, Date creationDate, boolean isPublished) {
        this.usersWhoSubscribed = new ArrayList<>();
        this.id = id;
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.creationDate = creationDate;
        this.isPublished = isPublished;
        this.usersWhoSubscribed.add(authorId);
    }

    public List<String> getUsersWhoSubscribed() {
        return usersWhoSubscribed;
    }

    public void setUsersWhoSubscribed(List<String> usersWhoSubscribed) {
        this.usersWhoSubscribed = usersWhoSubscribed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public String getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(String idAddress) {
        this.idAddress = idAddress;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }
}
