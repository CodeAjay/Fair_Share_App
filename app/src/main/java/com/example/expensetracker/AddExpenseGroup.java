package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddExpenseGroup extends AppCompatActivity {

    private List<Person> persons = new ArrayList<>();
    private RecyclerView recyclerView;
    private SelectablePersonAdapter selectablePersonAdapter;
    private Spinner personsSpinner;
    private EditText expenseDescription;
    private EditText expenseAmount;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense_group);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerParticipants);
        personsSpinner = findViewById(R.id.paidBy);
        expenseDescription = findViewById(R.id.expenseNameGroup);
        expenseAmount = findViewById(R.id.amountGroup);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectablePersonAdapter = new SelectablePersonAdapter(persons);
        recyclerView.setAdapter(selectablePersonAdapter);

        // Receive the selected group from the Intent
        Intent intent = getIntent();
        Group selectedGroup = (Group) intent.getSerializableExtra("selectedGroup");
        if (selectedGroup != null) {
            updatePersonsSpinner(selectedGroup.getPersons());
        } else {
            showToast("No group selected");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_expense, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addExpense) {
            if (validateFields()) {
                List<Person> selectedParticipants = selectablePersonAdapter.getSelectedPersons();
                double amount = Double.parseDouble(expenseAmount.getText().toString());
                Person paidByPerson = persons.get(personsSpinner.getSelectedItemPosition());

                double share = calculateShare(amount, selectedParticipants, paidByPerson);

                Expenses expense = new Expenses(
                        expenseDescription.getText().toString(),
                        amount,
                        selectedParticipants,
                        paidByPerson,
                        share
                );

                handleExpense(expense);
                showToast("Expense added successfully");
                finish(); // Close the activity
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateFields() {
        if (expenseDescription.getText().toString().isEmpty() || expenseAmount.getText().toString().isEmpty()) {
            showToast("Please enter all fields");
            return false;
        }

        if (selectablePersonAdapter.getSelectedPersons().isEmpty()) {
            showToast("Please select at least one participant");
            return false;
        }

        return true;
    }



    private double calculateShare(double amount, List<Person> participants, Person paidBy) {
        int numParticipants = participants.size();
        boolean isPayerIncluded = participants.contains(paidBy);
        return amount / (isPayerIncluded ? numParticipants : (numParticipants + 1));
    }

    private void handleExpense(Expenses expense) {
        // Retrieve the group document from Firestore
        String groupId = getIntent().getStringExtra("groupId");
        if (groupId != null) {
            DocumentReference groupRef = db.collection("groups").document(groupId);
            groupRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    GroupExpenseModel groupExpenseModel = documentSnapshot.toObject(GroupExpenseModel.class);
                    if (groupExpenseModel != null) {
                        List<Expenses> expensesList = groupExpenseModel.getExpenses();
                        if (expensesList == null) {
                            expensesList = new ArrayList<>();
                        }
                        expensesList.add(expense);

                        // Update only the 'expenses' field in Firestore
                        groupRef.update("expenses", expensesList)
                                .addOnSuccessListener(aVoid -> showToast("Expense added successfully"))
                                .addOnFailureListener(e -> showToast("Error adding expense: " + e.getMessage()));
                    } else {
                        showToast("Group expense model not found");
                    }
                } else {
                    showToast("Group document does not exist");
                }
            }).addOnFailureListener(e -> showToast("Failed to retrieve group document: " + e.getMessage()));
        } else {
            showToast("Group ID not found");
        }
    }

//    private void handleExpense(Expenses expense) {
//        // Update balances
//        for (Person person : expense.getParticipants()) {
//            if (person.equals(expense.getPaidBy())) {
//                person.setBalance(person.getBalance() + expense.getAmount() - expense.getShare());
//            } else {
//                person.setBalance(person.getBalance() - expense.getShare());
//            }
//        }
//
//        if (!expense.getParticipants().contains(expense.getPaidBy())) {
//            expense.getPaidBy().setBalance(expense.getPaidBy().getBalance() + expense.getAmount());
//        }
//
//        // Retrieve the group document from Firestore
//        String groupId = getIntent().getStringExtra("groupId");
//        if (groupId != null) {
//            DocumentReference groupRef = db.collection("groups").document(groupId);
//            groupRef.get().addOnSuccessListener(documentSnapshot -> {
//                if (documentSnapshot.exists()) {
//                    // Retrieve the current group expense model
//                    GroupExpenseModel groupExpenseModel = documentSnapshot.toObject(GroupExpenseModel.class);
//                    if (groupExpenseModel != null) {
//                        // Add the new expense to the group expense model
//                        groupExpenseModel.addExpense(expense);
//
//                        // Update the group document in Firestore with the updated group expense model
//                        groupRef.update("groupExpenseModel", groupExpenseModel)
//                                .addOnSuccessListener(aVoid -> showToast("Expense added successfully"))
//                                .addOnFailureListener(e -> showToast("Error adding expense: " + e.getMessage()));
//                    } else {
//                        showToast("Group expense model not found");
//                    }
//                } else {
//                    showToast("Group document does not exist");
//                }
//            }).addOnFailureListener(e -> showToast("Failed to retrieve group document: " + e.getMessage()));
//        } else {
//            showToast("Group ID not found");
//        }
//    }


    private void updatePersonsSpinner(List<Person> persons) {
        this.persons.clear();
        this.persons.addAll(persons);
        List<String> personNames = new ArrayList<>();
        for (Person person : persons) {
            personNames.add(person.getPersonName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, personNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        personsSpinner.setAdapter(adapter);
        selectablePersonAdapter.notifyDataSetChanged();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
