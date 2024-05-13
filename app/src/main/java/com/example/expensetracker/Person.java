package com.example.expensetracker;

public class Person {
    private String pId;
    private String personName;

    public Person() {
        // Default constructor required for Firebase
    }

    public Person(String pId, String personName) {
        this.pId = pId;
        this.personName = personName;
    }

    public String getPId() {
        return pId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}

