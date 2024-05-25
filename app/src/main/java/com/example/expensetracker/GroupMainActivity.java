package com.example.expensetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupMainActivity extends AppCompatActivity {
    private static final int NEW_GROUP_REQUEST_CODE = 1;
    private static final int ADD_PERSON_REQUEST_CODE = 2;
    private static final int DELETE_PERSON_REQUEST_CODE = 3;
    private static final int ADD_EXPENSE_REQUEST_CODE=4;

    private List<Group> groups = new ArrayList<>();
    private List<Person> persons = new ArrayList<>();
    private RecyclerView recyclerView;
    private PersonAdapter personAdapter;
    private Spinner groupSpinner;
    private Button leftButton, rightButton, addExpenseBtn;
    private Button createGroupButton, joinGroupButton;
    private LinearLayout noGroupsLayout;
    private Group selectedGroup;
    private ProgressDialog progressDialog; // Declare ProgressDialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main);

        recyclerView = findViewById(R.id.recycler);
        groupSpinner = findViewById(R.id.groupSpinner);
        noGroupsLayout = findViewById(R.id.noGroupsLayout);
        createGroupButton = findViewById(R.id.createGroupButton);
        joinGroupButton = findViewById(R.id.joinGroupButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        personAdapter = new PersonAdapter(persons);
        recyclerView.setAdapter(personAdapter);

        leftButton = findViewById(R.id.summaryBtn);
        rightButton = findViewById(R.id.expensesBtn);
        addExpenseBtn = findViewById(R.id.addExpenseBtn);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        // Initialize anonymous login
        initializeAnonymousLogin();

        createGroupButton.setOnClickListener(v -> {
            Intent intent = new Intent(GroupMainActivity.this, NewGroup.class);
            startActivityForResult(intent, NEW_GROUP_REQUEST_CODE);
        });

        joinGroupButton.setOnClickListener(v -> {
            Intent intent = new Intent(GroupMainActivity.this, JoinGroup.class);
            startActivity(intent);
        });

        leftButton.setOnClickListener(v -> showPopupMenu(v));
        rightButton.setOnClickListener(v -> {
            if (selectedGroup != null) {
                Intent intent = new Intent(GroupMainActivity.this, ShowExpenses.class);
                intent.putExtra("groupId", selectedGroup.getGroupId());
                startActivity(intent);
            } else {
                showToast("Please select a group first");
            }
        });

        addExpenseBtn.setOnClickListener(v -> {
            if (selectedGroup != null) {
                Intent intent = new Intent(GroupMainActivity.this, AddExpenseGroup.class);
                intent.putExtra("selectedGroup", selectedGroup);
                intent.putExtra("groupId", selectedGroup.getGroupId());
                startActivityForResult(intent, ADD_EXPENSE_REQUEST_CODE);
            } else {
                showToast("Please select a group first");
            }
        });

    }

    private void initializeAnonymousLogin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            auth.signInAnonymously()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            fetchGroups(auth.getCurrentUser().getUid());
                        } else {
                            showToast("Authentication failed.");
                        }
                    });
        } else {
            fetchGroups(currentUser.getUid());
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
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
            } else if(item.getItemId() == R.id.delete_person){
                if (selectedGroup != null) {
                    Intent intent = new Intent(GroupMainActivity.this, DeletePersons.class);
                    intent.putExtra("selectedGroup", selectedGroup);
                    intent.putExtra("personsList", new ArrayList<>(selectedGroup.getPersons()));
                    startActivityForResult(intent, DELETE_PERSON_REQUEST_CODE);
                } else {
                    showToast("No group selected");
                }
                return true;
            }else if (item.getItemId() == R.id.join_group) {
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
        });
        popupMenu.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch groups when the activity is resumed
        fetchGroups(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private void fetchGroups(String userId) {
        progressDialog.show(); // Show ProgressDialog before starting data fetch
        FirebaseFirestore.getInstance().collection("groups")
                .whereArrayContains("authors", userId)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss(); // Dismiss ProgressDialog once data is fetched
                    if (task.isSuccessful()) {
                        groups.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            groups.add(group);
                        }
                        if (groups.isEmpty()) {
                            showNoGroupsLayout();
                        } else {
                            hideNoGroupsLayout();
                            populateSpinner();
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

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = groups.get(position);
                updateRecyclerView(selectedGroup.getPersons());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        if (!groups.isEmpty()) {
            groupSpinner.setSelection(0);
            selectedGroup = groups.get(0);
        }
    }

    private void updateRecyclerView(List<Person> persons) {
        this.persons.clear();
        this.persons.addAll(persons);
        personAdapter.notifyDataSetChanged();
    }

    private void showNoGroupsLayout() {
        noGroupsLayout.setVisibility(View.VISIBLE);
        groupSpinner.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideNoGroupsLayout() {
        noGroupsLayout.setVisibility(View.GONE);
        groupSpinner.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
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
                fetchGroups(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        } else if (requestCode == DELETE_PERSON_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("selectedGroup")) {
                selectedGroup = (Group) data.getSerializableExtra("selectedGroup");
                fetchGroups(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }if (requestCode == ADD_EXPENSE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("selectedGroup")) {
                selectedGroup = (Group) data.getSerializableExtra("selectedGroup");
                fetchGroups(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }
    }

    private void fetchAndSetNewGroup(String groupId) {
        progressDialog.show(); // Show ProgressDialog while fetching the new group
        FirebaseFirestore.getInstance().collection("groups").document(groupId).get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss(); // Dismiss ProgressDialog once the new group is fetched
                    if (task.isSuccessful() && task.getResult() != null) {
                        Group newGroup = task.getResult().toObject(Group.class);
                        if (newGroup != null) {
                            groups.add(newGroup);
                            populateSpinner();
                            groupSpinner.setSelection(groups.size() - 1);
                            selectedGroup = newGroup;
                        }
                    } else {
                        showToast("Failed to fetch the new group");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

