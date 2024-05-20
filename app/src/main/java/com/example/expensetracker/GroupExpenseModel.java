package com.example.expensetracker;

import java.io.Serializable;
import java.util.List;

public class GroupExpenseModel implements Serializable {
    private List<Expenses> expenses;
    private List<Person> persons;
    private String groupName;
    private String groupId;
    private List<String> authors;

    public GroupExpenseModel() {}

    public List<Expenses> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expenses> expenses) {
        this.expenses = expenses;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public void addExpense(Expenses expense) {
        if (this.expenses != null) {
            this.expenses.add(expense);
        }
    }
}
