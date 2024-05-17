package com.example.expensetracker;

import java.io.Serializable;

public class Person implements Serializable {
    private String personName;
    private double balance;

    public Person(){}

    public Person(String personName, double balance) {
        this.personName = personName;
        this.balance = balance;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return personName != null ? personName.equals(person.personName) : person.personName == null;
    }

    @Override
    public int hashCode() {
        return personName != null ? personName.hashCode() : 0;
    }
}
