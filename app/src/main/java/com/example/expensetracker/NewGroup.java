package com.example.expensetracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewGroup extends AppCompatActivity {

    private EditText etGroupName, etPersonName;
    private Button btnAddPerson;
    private RecyclerView rvPersons;
    private PersonAdapter personAdapter;
    private List<Person> personList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_group);

        // TODO: To add new Person to e
//        setContentView(R.layout.new_group);
//
//        // Retrieve the selected group details from the intent extras
//        Group selectedGroup = getIntent().getParcelableExtra("selectedGroup");
//
//        // Load and populate the UI fields with the selected group details
//        if (selectedGroup != null) {
//            // Load data corresponding to the selected group (e.g., from Firebase Firestore)
//            // Populate the UI fields with the loaded data
//            // For example:
//            EditText groupNameEditText = findViewById(R.id.etGroupName);
//            groupNameEditText.setText(selectedGroup.getGroupName());
//            // Populate other fields similarly
//        }
        etGroupName = findViewById(R.id.etGroupName);
        etPersonName = findViewById(R.id.etPersonName);
        btnAddPerson = findViewById(R.id.btnAddPerson);
        rvPersons = findViewById(R.id.rvPersons);

        db = FirebaseFirestore.getInstance();

        personList = new ArrayList<>();
        personAdapter = new PersonAdapter(personList);
        rvPersons.setLayoutManager(new LinearLayoutManager(this));
        rvPersons.setAdapter(personAdapter);

        btnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPerson();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.createGroup) {
            if (validateInputs()) {
                createGroup();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addPerson() {
        String personName = etPersonName.getText().toString().trim();
        if (TextUtils.isEmpty(personName)) {
            showToast("Person name cannot be empty");
        } else {
            personList.add(new Person(personName, 0));
            personAdapter.notifyItemInserted(personList.size() - 1);
            etPersonName.setText("");
        }
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(etGroupName.getText().toString().trim())) {
            showToast("Group name cannot be empty");
            return false;
        }
        if (personList.isEmpty()) {
            showToast("At least one person should be added");
            return false;
        }
        return true;
    }

    private void createGroup() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            // User is not logged in, create anonymous user
            auth.signInAnonymously()
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser anonymousUser = authResult.getUser();
                        // Continue with group creation
                        performGroupCreation();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        showToast("Failed to create anonymous user");
                    });
        } else {
            // User is already logged in, continue with group creation
            performGroupCreation();
        }
    }

    private void performGroupCreation() {
        String groupName = etGroupName.getText().toString().trim();
        String groupId = generateGroupId();

        // Create the Group object first
        Group group = new Group(groupId, groupName, personList, new ArrayList<>());

        // Add the current user as an author
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> authors = group.getAuthors();
        authors.add(userId);
        group.setAuthors(authors);

        // Now save the group to Firestore
        db.collection("groups").document(groupId)
                .set(group)
                .addOnSuccessListener(aVoid -> {
                    showToast("Group created successfully");
                    finish();
                })
                .addOnFailureListener(e -> showToast("Failed to create group"));
    }


    private String generateGroupId() {
        Random random = new Random();
        int groupId = random.nextInt(900000) + 100000; // Generates a number between 100000 and 999999
        return String.valueOf(groupId);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
