package com.example.applicationsecond.models;

import java.util.List;

public class User {

    private String id;
    private String username;
    private String urlPhoto;
    private long idAddress;
    private boolean isAssociation;
    private List<String> projectsSubscribedId;
    private List<String> associationSubscribedId;

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
}
