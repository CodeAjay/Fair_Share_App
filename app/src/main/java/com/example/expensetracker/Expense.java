package com.example.expensetracker;

import java.util.List;

public class Expense {
    private String expenseName;
    private double expenseAmount;
    private String paidBy;
    private List<Person> persons;

    public Expense() {
        // Default constructor required for Firebase
    }

    public Expense(String expenseName, double expenseAmount, String paidBy, List<Person> persons) {
        this.expenseName = expenseName;
        this.expenseAmount = expenseAmount;
        this.paidBy = paidBy;
        this.persons = persons;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public double getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(double expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}

