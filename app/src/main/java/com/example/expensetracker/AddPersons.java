package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AddPersons extends AppCompatActivity {

    private EditText personNameInput;
    private Button addPersonButton;
    private Group selectedGroup;
    private List<Person> persons;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_persons);

        personNameInput = findViewById(R.id.personNameInput);
        addPersonButton = findViewById(R.id.addPersonButton);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        selectedGroup = (Group) intent.getSerializableExtra("selectedGroup");
        persons = (List<Person>) intent.getSerializableExtra("personsList");

        if (selectedGroup == null || persons == null) {
            showToast("No group selected or persons list is empty");
            finish();
            return;
        }

        addPersonButton.setOnClickListener(v -> {
            String personName = personNameInput.getText().toString().trim();
            if (!personName.isEmpty()) {
                addPersonToGroup(personName);
            } else {
                showToast("Please enter a person name");
            }
        });
    }

    private void addPersonToGroup(String personName) {
        Person newPerson = new Person(personName, 0.0);
        persons.add(newPerson);

        db.collection("groups").document(selectedGroup.getGroupId())
                .update("persons", persons)
                .addOnSuccessListener(aVoid -> {
                    showToast("Person added successfully");
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("newPerson", newPerson);
                    resultIntent.putExtra("selectedGroup", selectedGroup);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> showToast("Error adding person: " + e.getMessage()));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
