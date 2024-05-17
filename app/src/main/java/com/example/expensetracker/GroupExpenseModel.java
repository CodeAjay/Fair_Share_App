package com.example.expensetracker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupExpenseModel implements Serializable {
    private List<Expenses> expenses;


    public GroupExpenseModel() {
        // Default constructor required for Firebase
        this.expenses = new ArrayList<>();
    }

    public GroupExpenseModel(List<Expenses> expenses) {
        this.expenses = expenses;
    }

    public List<Expenses> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expenses> expenses) {
        this.expenses = expenses;
    }

    // Method to add an expense to the list of expenses
    public void addExpense(Expenses expense) {
        if (expenses == null) {
            expenses = new ArrayList<>();
        }
        expenses.add(expense);
    }
}
