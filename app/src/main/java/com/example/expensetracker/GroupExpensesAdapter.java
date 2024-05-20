package com.example.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupExpensesAdapter extends RecyclerView.Adapter<GroupExpensesAdapter.ExpenseViewHolder> {

    private List<Expenses> expensesList;

    public GroupExpensesAdapter(List<Expenses> expensesList) {
        this.expensesList = expensesList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expenses expense = expensesList.get(position);
        holder.expenseDescription.setText(expense.getDescription());
        holder.expenseAmount.setText(String.valueOf(expense.getAmount()));
        holder.expensePaidBy.setText(expense.getPaidBy().getPersonName());
        // Display participants
        StringBuilder participants = new StringBuilder();
        for (Person participant : expense.getParticipants()) {
            participants.append(participant.getPersonName()).append(", ");
        }
        // Remove last comma and space
        if (participants.length() > 0) {
            participants.setLength(participants.length() - 2);
        }
        // Ensure expenseParticipants is not null before setting text
        if (holder.expenseParticipants != null) {
            holder.expenseParticipants.setText(participants.toString());
        }
    }


    @Override
    public int getItemCount() {
        return expensesList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView expenseDescription, expenseAmount, expensePaidBy, expenseParticipants;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseDescription = itemView.findViewById(R.id.expenseName);
            expenseAmount = itemView.findViewById(R.id.expenseAmount);
            expensePaidBy = itemView.findViewById(R.id.paidBy);
//            expenseParticipants = itemView.findViewById(R.id.expenseParticipants);
        }
    }
}

