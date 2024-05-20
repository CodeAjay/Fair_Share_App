package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupMainActivity extends AppCompatActivity {
    private List<Group> groups = new ArrayList<>();
    private List<Person> persons = new ArrayList<>();
    private RecyclerView recyclerView;
    private PersonAdapter personAdapter;
    private Spinner groupSpinner;
    private Button leftButton, rightButton, addExpenseBtn;
    private Group selectedGroup; // Variable to store the selected group
    private boolean isGroupSelected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main);

        recyclerView = findViewById(R.id.recycler);
        groupSpinner = findViewById(R.id.groupSpinner);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        personAdapter = new PersonAdapter(persons);
        recyclerView.setAdapter(personAdapter);

        // Fetch groups from Firestore and populate the dropdown menu
        fetchGroups();
        updateRecyclerView(persons);
        // Set up spinner listener
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected group
                selectedGroup = groups.get(position);
                // Update RecyclerView with persons from selected group
                updateRecyclerView(selectedGroup.getPersons());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        leftButton = findViewById(R.id.summaryBtn);
        rightButton = findViewById(R.id.expensesBtn);
        addExpenseBtn = findViewById(R.id.addExpenseBtn); // initialize addExpenseBtn

        // Inside your onCreate method
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupMainActivity.this, ShowExpenses.class); // Correctly configured to start ShowExpenses activity
                intent.putExtra("groupId", selectedGroup.getGroupId());
                startActivity(intent);
                showToast("Right Button Clicked");
            }
        });


        // Set OnClickListener for addExpenseBtn
        addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AddExpenseGroup activity with the selected group
                if (selectedGroup != null) {
                    Intent intent = new Intent(GroupMainActivity.this, AddExpenseGroup.class);
                    intent.putExtra("selectedGroup", selectedGroup);
                    intent.putExtra("groupId", selectedGroup.getGroupId());
                    startActivity(intent);
                } else {
                    showToast("Please select a group first");
                }
            }
        });
    }

    private void showPopupMenu(View view) {
        // Create a PopupMenu
        PopupMenu popupMenu = new PopupMenu(this, view);
        // Inflate the menu resource
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());
        // Set the menu item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the selected item based on its ID
                if (item.getItemId() == R.id.add_person) {

                    //TODO: Add new person to existing group
                    int selectedGroupPosition = groupSpinner.getSelectedItemPosition();
                    selectedGroup = groups.get(selectedGroupPosition);
                    // Open NewGroup activity with selected group details
                    openNewGroupWithGroupDetails(selectedGroup);
                    return true;

                } else if (item.getItemId() == R.id.join_group) {
                    // Handle Join a Group
                    showToast("Join a Group Selected");
                    return true;
                } else if (item.getItemId() == R.id.delete_group) {
                    // Handle Delete a Group
                    showToast("Delete a Group Selected");
                    return true;
                } else {
                    return false;
                }
            }
        });
        // Show the popup menu
        popupMenu.show();
    }

    private void openNewGroupWithGroupDetails(Group group) {
        // Create an intent to open NewGroup activity
        Intent intent = new Intent(GroupMainActivity.this, NewGroup.class);
        // Pass the selected group details to the intent
        intent.putExtra("selectedGroup", group);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Only fetch groups if the groups list is empty
        if (groups.isEmpty()) {
            fetchGroups();
        }
    }

    private void fetchGroups() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("groups")
                .whereArrayContains("authors", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        groups.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            groups.add(group);
                        }
                        // Populate the dropdown menu with group names
                        populateSpinner();
                        // Set the selected group if not already set and groups list is not empty
                        if (!isGroupSelected && !groups.isEmpty()) {
                            groupSpinner.setSelection(0);
                            this.selectedGroup = groups.get(0); // Set the first group as default
                            isGroupSelected = true; // Set the flag to true to indicate that the group has been selected
                        }
                    } else {
                        showToast("Failed to fetch groups");
                    }
                });
    }


    private void populateSpinner() {
        List<String> groupNames = new ArrayList<>();
        for (Group group : groups) {
            groupNames.add(group.getGroupName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter);

        // Check if the selected group has already been set
        if (!isGroupSelected) {
            // Set the selected group if not already set and groups list is not empty
            if (selectedGroup != null && groups.contains(selectedGroup)) {
                int selectedIndex = groups.indexOf(selectedGroup);
                groupSpinner.setSelection(selectedIndex);
                isGroupSelected = true; // Set the flag to true to indicate that the group has been selected
            } else if (!groups.isEmpty()) {
                groupSpinner.setSelection(0);
                this.selectedGroup = groups.get(0); // Set the first group as default
                isGroupSelected = true; // Set the flag to true to indicate that the group has been selected
            }
        }
    }
//    private void fetchGroups() {
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        FirebaseFirestore.getInstance().collection("groups")
//                .whereArrayContains("authors", userId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        groups.clear();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Group group = document.toObject(Group.class);
//                            groups.add(group);
//                        }
//                        // Populate the dropdown menu with group names
//                        populateSpinner(selectedGroup); // Pass selectedGroup here
//                    } else {
//                        showToast("Failed to fetch groups");
//                    }
//                });
//    }
//
//
//    private void populateSpinner(Group selectedGroup) {
//        List<String> groupNames = new ArrayList<>();
//        for (Group group : groups) {
//            groupNames.add(group.getGroupName());
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupNames);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        groupSpinner.setAdapter(adapter);
//
//        // Check if selectedGroup is not null and if it exists in the groups list
//        if (selectedGroup != null && groups.contains(selectedGroup)) {
//            // Set the selected group in the spinner
//            int selectedIndex = groups.indexOf(selectedGroup);
//            groupSpinner.setSelection(selectedIndex);
//        } else {
//            // If selectedGroup is null or not found, set the first group as default
//            if (!groups.isEmpty()) {
//                groupSpinner.setSelection(0);
//                this.selectedGroup = groups.get(0); // Update selectedGroup
//            }
//        }
//    }


    private void updateRecyclerView(List<Person> persons) {
        this.persons.clear();
        this.persons.addAll(persons);
        personAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.newGroup) {
            // Handle new group button click
            showToast("New Group Button Clicked");
            // Start NewGroup activity
            Intent intent = new Intent(GroupMainActivity.this, NewGroup.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
