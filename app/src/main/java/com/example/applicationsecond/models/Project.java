package com.example.applicationsecond.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Project {

    private String id;
    private String title;
    private Date creationDate;
    private long eventDate;
    private String description;
    private String urlPhoto;
    private String idAddress;
    private String authorId;
    private boolean isPublished;
    private List<String> usersWhoSubscribed;
    private String streetNumber;
    private String streetName;
    private String postalCode;
    private String city;
    private String country;
    private String latLng;

    public Project() {

    }

    public Project(String title, String description, String authorId, Date creationDate) {
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.creationDate = creationDate;
    }

    public Project(String id, String title, String description, String authorId, Date creationDate, long eventDate,
                   boolean isPublished, String streetNumber, String streetName,
                   String postalCode, String city, String country, String latLng) {
        this.usersWhoSubscribed = new ArrayList<>();
        this.id = id;
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.creationDate = creationDate;
        this.eventDate = eventDate;
        this.isPublished = isPublished;
        this.usersWhoSubscribed.add(authorId);
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
        this.latLng = latLng;
    }

    public Project(String id, String title, String description, String authorId, Date creationDate,
                   long eventDate, boolean isPublished, String urlPhoto,
                   String streetNumber, String streetName,String postalCode,
                   String city, String country, String latLng) {
        this.usersWhoSubscribed = new ArrayList<>();
        this.id = id;
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.creationDate = creationDate;
        this.isPublished = isPublished;
        this.usersWhoSubscribed.add(authorId);
        this.urlPhoto = urlPhoto;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
        this.eventDate = eventDate;
        this.latLng = latLng;
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

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
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

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }
}
