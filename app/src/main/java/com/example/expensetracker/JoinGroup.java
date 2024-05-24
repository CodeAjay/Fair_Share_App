package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class JoinGroup extends AppCompatActivity {

    private EditText groupIdInput;
    private Button joinGroupButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_group);

        groupIdInput = findViewById(R.id.groupIdInput);
        joinGroupButton = findViewById(R.id.joinGroupButton);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        joinGroupButton.setOnClickListener(v -> {
            String groupId = groupIdInput.getText().toString().trim();
            if (!groupId.isEmpty()) {
                joinGroup(groupId);
            } else {
                showToast("Please enter a group ID");
            }
        });
    }

    private void joinGroup(String groupId) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("groups").document(groupId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Group group = documentSnapshot.toObject(Group.class);
                        if (group != null) {
                            // Add the current user to the group's authors list
                            if (!group.getAuthors().contains(userId)) {
                                group.getAuthors().add(userId);
                                db.collection("groups").document(groupId)
                                        .update("authors", group.getAuthors())
                                        .addOnSuccessListener(aVoid -> {
                                            showToast("Successfully joined the group");
                                            Intent intent = new Intent(JoinGroup.this, GroupMainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> showToast("Failed to join the group: " + e.getMessage()));
                            } else {
                                showToast("You are already a member of this group");
                            }
                        } else {
                            showToast("Group not found");
                        }
                    } else {
                        showToast("Invalid Group ID");
                    }
                })
                .addOnFailureListener(e -> showToast("Error: " + e.getMessage()));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
