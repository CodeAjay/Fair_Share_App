package com.example.expensetracker;

import java.io.Serializable;
import java.util.List;

public class Expenses implements Serializable {
    private String description;
    private double amount;
    private List<Person> participants;
    private Person paidBy;
    private double share;

    public Expenses() {}

    public Expenses(String description, double amount, List<Person> participants, Person paidBy, double share) {
        this.description = description;
        this.amount = amount;
        this.participants = participants;
        this.paidBy = paidBy;
        this.share = share;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<Person> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Person> participants) {
        this.participants = participants;
    }

    public Person getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(Person paidBy) {
        this.paidBy = paidBy;
    }

    public double getShare() {
        return share;
    }

    public void setShare(double share) {
        this.share = share;
    }
}
