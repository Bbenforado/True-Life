package com.example.applicationsecond.models;

import androidx.annotation.NonNull;

public class Address {

    private String id;
    private int streetNumber;
    private String streetName;
    private String complement;
    private String city;
    private String country;
    private String postalCode;

    public Address(int streetNumber, String streetName, String complement, String city,
                   String country, String postalCode) {
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.complement = complement;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
