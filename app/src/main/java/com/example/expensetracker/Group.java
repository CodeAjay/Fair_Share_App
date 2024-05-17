package com.example.expensetracker;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {
    private String groupId;
    private String groupName;
    private List<Person> persons;
    private GroupExpenseModel groupExpenseModel;
    private List<String> authors;


    public Group() {
        // Default constructor required for Firebase
    }

    public Group(String groupId, String groupName, List<Person> persons, List<String> authors) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.persons = persons;
//        this.groupExpenseModel = groupExpenseModel;
        this.authors = authors;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public GroupExpenseModel getGroupExpenseModel() {
        return groupExpenseModel;
    }

    public void setGroupExpenseModel(GroupExpenseModel groupExpenseModel) {
        this.groupExpenseModel = groupExpenseModel;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
