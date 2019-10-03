package com.example.applicationsecond.models;

public class CurrentNeed {

    private String id;
    private String name;
    private String idAuthor;

    public CurrentNeed(String name, String idAuthor) {
        this.name = name;
        this.idAuthor = idAuthor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(String idAuthor) {
        this.idAuthor = idAuthor;
    }
}
