package com.example.applicationsecond.models;

public class Subscription {

    private String id;
    private String idUser;
    private String idProject;

    public Subscription(String idUser, String idProject) {
        this.idUser = idUser;
        this.idProject = idProject;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdProject() {
        return idProject;
    }

    public void setIdProject(String idProject) {
        this.idProject = idProject;
    }
}
