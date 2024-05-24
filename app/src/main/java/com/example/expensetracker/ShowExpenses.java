package com.example.expensetracker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ShowExpenses extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GroupExpensesAdapter expensesAdapter;
    private List<Expenses> expenseList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView noExpensesText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_expenses);

        recyclerView = findViewById(R.id.recyclerExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        expensesAdapter = new GroupExpensesAdapter(expenseList);
        recyclerView.setAdapter(expensesAdapter);

        noExpensesText = findViewById(R.id.noExpensesText);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Check if user is authenticated
        if (auth.getCurrentUser() != null) {
            // Get the groupId from the intent
            String groupId = getIntent().getStringExtra("groupId");
            if (groupId != null) {
                checkUserAuthorization(groupId);
            } else {
                showToast("Group ID not found");
                Log.e("ShowExpenses", "Group ID is null");
            }
        } else {
            showToast("User is not authenticated");
            Log.e("ShowExpenses", "User is not authenticated");
        }
    }

    private void checkUserAuthorization(String groupId) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("groups").document(groupId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> authors = (List<String>) document.get("authors");
                    if (authors != null && authors.contains(userId)) {
                        fetchExpenses(groupId);
                    } else {
                        showToast("You are not authorized to view expenses for this group");
                        Log.e("ShowExpenses", "User is not authorized to view expenses for this group");
                    }
                } else {
                    showToast("Group document does not exist");
                    Log.e("ShowExpenses", "Group document does not exist");
                }
            } else {
                showToast("Error fetching group document: " + task.getException().getMessage());
                Log.e("ShowExpenses", "Error fetching group document", task.getException());
            }
        });
    }

    private void fetchExpenses(String groupId) {
        Log.d("ShowExpenses", "Fetching expenses for group ID: " + groupId);
        db.collection("groups").document(groupId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("ShowExpenses", "Group document exists");
                GroupExpenseModel groupExpenseModel = documentSnapshot.toObject(GroupExpenseModel.class);
                if (groupExpenseModel != null) {
                    List<Expenses> expenses = groupExpenseModel.getExpenses();
                    if (expenses != null && !expenses.isEmpty()) {
                        expenseList.clear();
                        expenseList.addAll(expenses);
                        expensesAdapter.notifyDataSetChanged();
                        noExpensesText.setVisibility(View.GONE);  // Hide the "No Expenses Found" message
                        recyclerView.setVisibility(View.VISIBLE);  // Show the RecyclerView
                        Log.d("ShowExpenses", "Expenses loaded successfully");
                    } else {
                        Log.e("ShowExpenses", "No expenses found in the group expense model");
                        showToast("No expenses found");
                        noExpensesText.setVisibility(View.VISIBLE);  // Show the "No Expenses Found" message
                        recyclerView.setVisibility(View.GONE);  // Hide the RecyclerView
                    }
                } else {
                    Log.e("ShowExpenses", "Group expense model is null");
                    showToast("Failed to load expenses");
                    noExpensesText.setVisibility(View.VISIBLE);  // Show the "No Expenses Found" message
                    recyclerView.setVisibility(View.GONE);  // Hide the RecyclerView
                }
            } else {
                Log.e("ShowExpenses", "Group document does not exist");
                showToast("Group document does not exist");
                noExpensesText.setVisibility(View.VISIBLE);  // Show the "No Expenses Found" message
                recyclerView.setVisibility(View.GONE);  // Hide the RecyclerView
            }
        }).addOnFailureListener(e -> {
            Log.e("ShowExpenses", "Error fetching expenses", e);
            showToast("Error fetching expenses: " + e.getMessage());
            noExpensesText.setVisibility(View.VISIBLE);  // Show the "No Expenses Found" message
            recyclerView.setVisibility(View.GONE);  // Hide the RecyclerView
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

