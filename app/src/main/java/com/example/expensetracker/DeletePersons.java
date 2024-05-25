package com.example.expensetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DeletePersons extends AppCompatActivity {

    private static final int ADD_PERSON_REQUEST_CODE = 1;

    private Button deleteSelectedPersonsButton;
    private RecyclerView personsRecyclerView;
    private SelectablePersonAdapter selectablePersonAdapter;
    private List<Person> persons = new ArrayList<>();
    private Group selectedGroup;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog; // Declare ProgressDialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_persons);

        deleteSelectedPersonsButton = findViewById(R.id.delete_person_button);
        personsRecyclerView = findViewById(R.id.persons_show_recycler);

        db = FirebaseFirestore.getInstance();

        personsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectablePersonAdapter = new SelectablePersonAdapter(persons);
        personsRecyclerView.setAdapter(selectablePersonAdapter);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting...");
        progressDialog.setCancelable(false);

        // Receive the selected group from the Intent
        selectedGroup = (Group) getIntent().getSerializableExtra("selectedGroup");
        if (selectedGroup != null) {
            persons.addAll(selectedGroup.getPersons());
            selectablePersonAdapter.notifyDataSetChanged();
        } else {
            showToast("No group selected");
            finish();
        }

        deleteSelectedPersonsButton.setOnClickListener(v -> deleteSelectedPersonsFromGroup());
    }

    private void deleteSelectedPersonsFromGroup() {
        List<Person> selectedPersons = selectablePersonAdapter.getSelectedPersons();
        if (!selectedPersons.isEmpty()) {
            progressDialog.show(); // Show ProgressDialog
            persons.removeAll(selectedPersons);
            selectablePersonAdapter.notifyDataSetChanged();
            updateGroupInFirestore();
        } else {
            showToast("No persons selected");
        }
    }

    private void updateGroupInFirestore() {
        db.collection("groups").document(selectedGroup.getGroupId())
                .update("persons", persons)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss(); // Dismiss ProgressDialog
                    showToast("Group updated successfully");
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedGroup", selectedGroup);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss(); // Dismiss ProgressDialog
                    showToast("Error updating group: " + e.getMessage());
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
