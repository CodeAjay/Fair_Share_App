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
    private static final int NEW_GROUP_REQUEST_CODE = 1;
    private static final int ADD_PERSON_REQUEST_CODE = 2;

    private List<Group> groups = new ArrayList<>();
    private List<Person> persons = new ArrayList<>();
    private RecyclerView recyclerView;
    private PersonAdapter personAdapter;
    private Spinner groupSpinner;
    private Button leftButton, rightButton, addExpenseBtn;
    private Group selectedGroup;
    private boolean isGroupSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main);

        recyclerView = findViewById(R.id.recycler);
        groupSpinner = findViewById(R.id.groupSpinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        personAdapter = new PersonAdapter(persons);
        recyclerView.setAdapter(personAdapter);

        fetchGroups();
        updateRecyclerView(persons);

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = groups.get(position);
                updateRecyclerView(selectedGroup.getPersons());
                showToast("Group selected: " + selectedGroup.getGroupName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        leftButton = findViewById(R.id.summaryBtn);
        rightButton = findViewById(R.id.expensesBtn);
        addExpenseBtn = findViewById(R.id.addExpenseBtn);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, selectedGroup);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupMainActivity.this, ShowExpenses.class);
                intent.putExtra("groupId", selectedGroup.getGroupId());
                startActivity(intent);
                showToast("Right Button Clicked");
            }
        });

        addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void showPopupMenu(View view, Group selectedGroup) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.add_person) {
                    if (selectedGroup != null) {
                        Intent intent = new Intent(GroupMainActivity.this, AddPersons.class);
                        intent.putExtra("selectedGroup", selectedGroup);
                        intent.putExtra("personsList", new ArrayList<>(selectedGroup.getPersons()));
                        startActivityForResult(intent, ADD_PERSON_REQUEST_CODE);
                    } else {
                        showToast("No group selected");
                    }
                    return true;
                } else if (item.getItemId() == R.id.join_group) {
                    Intent intent = new Intent(GroupMainActivity.this, JoinGroup.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.delete_group) {
                    Intent intent = new Intent(GroupMainActivity.this, DeletePersons.class);
                    startActivity(intent);
                    showToast("Delete a Group Selected");
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ?if (groups.isEmpty()) {
            fetchGroups();
//        }
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
                        populateSpinner(null);
                    } else {
                        showToast("Failed to fetch groups");
                    }
                });
    }

    private void populateSpinner(Group selectedGroup) {
        List<String> groupNames = new ArrayList<>();
        for (Group group : groups) {
            groupNames.add(group.getGroupName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter);

        if (selectedGroup != null) {
            int selectedIndex = groups.indexOf(selectedGroup);
            groupSpinner.setSelection(selectedIndex);
            this.selectedGroup = selectedGroup;
        } else if (!groups.isEmpty()) {
            groupSpinner.setSelection(0);
            this.selectedGroup = groups.get(0);
        }
    }

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
            showToast("New Group Button Clicked");
            Intent intent = new Intent(GroupMainActivity.this, NewGroup.class);
            startActivityForResult(intent, NEW_GROUP_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_GROUP_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("newGroupId")) {
                String newGroupId = data.getStringExtra("newGroupId");
                if (newGroupId != null) {
                    fetchAndSetNewGroup(newGroupId);
                }
            }
        } else if (requestCode == ADD_PERSON_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("selectedGroup")) {
                selectedGroup = (Group) data.getSerializableExtra("selectedGroup");
                fetchGroups();
            }
        }
    }

    private void fetchAndSetNewGroup(String groupId) {
        FirebaseFirestore.getInstance().collection("groups").document(groupId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Group newGroup = documentSnapshot.toObject(Group.class);
                        if (newGroup != null) {
                            groups.add(newGroup);
                            populateSpinner(newGroup);
                        }
                    } else {
                        showToast("New group not found");
                    }
                })
                .addOnFailureListener(e -> showToast("Failed to fetch new group"));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
