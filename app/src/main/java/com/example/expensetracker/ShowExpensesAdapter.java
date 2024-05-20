package com.example.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShowExpensesAdapter extends RecyclerView.Adapter<ShowExpensesAdapter.ExpenseViewHolder> {

    private List<Expenses> expenseList;

    public ShowExpensesAdapter(List<Expenses> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expenses expense = expenseList.get(position);
        holder.expenseName.setText(expense.getDescription());
        holder.expenseAmount.setText(String.valueOf(expense.getAmount()));
        holder.paidBy.setText(expense.getPaidBy().getPersonName());  // Assuming paidBy is of type Person
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView expenseName;
        TextView expenseAmount;
        TextView paidBy;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseName = itemView.findViewById(R.id.expenseName);
            expenseAmount = itemView.findViewById(R.id.expenseAmount);
            paidBy = itemView.findViewById(R.id.paidBy);
        }
    }
}
