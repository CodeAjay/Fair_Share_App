package com.example.expensetracker;

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

    private Button addPersonButton;
    private Button deleteSelectedPersonsButton;
    private RecyclerView personsRecyclerView;
    private SelectablePersonAdapter selectablePersonAdapter;
    private List<Person> persons = new ArrayList<>();
    private Group selectedGroup;
    private FirebaseFirestore db;

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

        // Receive the selected group from the Intent
        selectedGroup = (Group) getIntent().getSerializableExtra("selectedGroup");
        if (selectedGroup != null) {
            persons.addAll(selectedGroup.getPersons());
            selectablePersonAdapter.notifyDataSetChanged();
        } else {
            showToast("No group selected");
            finish();
        }

        addPersonButton.setOnClickListener(v -> {
            Intent intent = new Intent(DeletePersons.this, GroupMainActivity.class);
            intent.putExtra("selectedGroup", selectedGroup);
            intent.putExtra("personsList", new ArrayList<>(persons));
            startActivityForResult(intent, ADD_PERSON_REQUEST_CODE);
        });

        deleteSelectedPersonsButton.setOnClickListener(v -> deleteSelectedPersonsFromGroup());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PERSON_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("newPerson")) {
                Person newPerson = (Person) data.getSerializableExtra("newPerson");
                if (newPerson != null) {
                    persons.add(newPerson);
                    selectablePersonAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void deleteSelectedPersonsFromGroup() {
        List<Person> selectedPersons = selectablePersonAdapter.getSelectedPersons();
        if (!selectedPersons.isEmpty()) {
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
                .addOnSuccessListener(aVoid -> showToast("Group updated successfully"))
                .addOnFailureListener(e -> showToast("Error updating group: " + e.getMessage()));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
