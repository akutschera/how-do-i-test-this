package com.github.akutschera.howdoitestthis.functions.springcloudmessaging;

/**
 * Created by Andreas Kutschera.
 */
public class Address {

    private String city;

    public Address() {
    }

    public Address( String city ) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity( String city ) {
        this.city = city;
    }

    public String toString() {
        return this.city;
    }
}
