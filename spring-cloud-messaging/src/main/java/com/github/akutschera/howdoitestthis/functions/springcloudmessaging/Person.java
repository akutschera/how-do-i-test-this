package com.github.akutschera.howdoitestthis.functions.springcloudmessaging;

/**
 * Created by Andreas Kutschera.
 */
public class Person {

    private String name;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
