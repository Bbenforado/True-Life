package com.example.applicationsecond.models;

public class Photo {

    private String uri;

    public Photo(String uri) {
        this.uri = uri;
    }

    //------------------------------------
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
