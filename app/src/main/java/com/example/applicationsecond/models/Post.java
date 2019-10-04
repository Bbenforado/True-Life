package com.example.applicationsecond.models;

import java.util.Date;

public class Post {

    private String id;
    private String title;
    private String content;
    private String authorName;
    private Date dateOfPublication;

    public Post() {

    }

    public Post(String id, String title, String content, String authorName, Date dateOfPublication) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.dateOfPublication = dateOfPublication;
    }

    /*public Post(String id, String title, String content, String authorName, Date dateOfPublication) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.dateOfPublication = dateOfPublication;
    }*/


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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getDateOfPublication() {
        return dateOfPublication;
    }

    public void setDateOfPublication(Date dateOfPublication) {
        this.dateOfPublication = dateOfPublication;
    }
}
