package br.com.facio.labs.avro.model;

import java.io.Serializable;

/**
 *
 * @author fabianocp
 */
public class Address implements Serializable {
    
    private String street;
    private int number;
    private String complement;
    private String city;
    private String state;
    private String country;

    public Address() {
        
    }
    
    public Address(String street, int number, String complement, String city, String state, String country) {
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.city = city;
        this.state = state;
        this.country = country;
    }
        

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    
    
    
}
