package com.example.applicationsecond.models;

import java.util.List;
import java.util.Map;

public class User {

    private String id;
    private String username;
    private String urlPhoto;
    private long idAddress;
    private boolean isAssociation;
    private List<String> projectsSubscribedId;
    private Map<String, Long> lastChatVisit;
    private List<String> associationSubscribedId;
    private List<String> publishedPostId;
    private String country;
    private String city;

    public User() {

    }

    public User(String username, String urlPhoto, long idAddress, boolean isAssociation) {
        this.username = username;
        this.urlPhoto = urlPhoto;
        this.idAddress = idAddress;
        this.isAssociation = isAssociation;
    }

    public User(String id, String username, boolean isAssociation) {
        this.id = id;
        this.username = username;
        this.isAssociation = isAssociation;
    }

    public User(String id, String username, boolean isAssociation, String city, String country) {
        this.id = id;
        this.username = username;
        this.isAssociation = isAssociation;
        this.city = city;
        this.country = country;
    }


    //-------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public long getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(long idAddress) {
        this.idAddress = idAddress;
    }

    public boolean isAssociation() {
        return isAssociation;
    }

    public void setAssociation(boolean association) {
        isAssociation = association;
    }

    public List<String> getAssociationSubscribedId() {
        return associationSubscribedId;
    }

    public void setAssociationSubscribedId(List<String> associationSubscribedId) {
        this.associationSubscribedId = associationSubscribedId;
    }

    public List<String> getProjectsSubscribedId() {
        return projectsSubscribedId;
    }

    public void setProjectsSubscribedId(List<String> projectsSubscribedId) {
        this.projectsSubscribedId = projectsSubscribedId;
    }

    public List<String> getPublishedPostId() {
        return publishedPostId;
    }

    public void setPublishedPostId(List<String> publishedPostId) {
        this.publishedPostId = publishedPostId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Map<String, Long> getLastChatVisit() {
        return lastChatVisit;
    }

    public void setLastChatVisit(Map<String, Long> lastChatVisit) {
        this.lastChatVisit = lastChatVisit;
    }
}
