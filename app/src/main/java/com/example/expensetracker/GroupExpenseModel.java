package com.example.expensetracker;

import java.io.Serializable;
import java.util.List;

public class GroupExpenseModel implements Serializable {
    private List<Expense> expenses;

    public GroupExpenseModel() {
        // Default constructor required for Firebase
    }

    public GroupExpenseModel(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
}
