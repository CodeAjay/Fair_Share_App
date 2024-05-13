package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.R;

public class GroupMainActivity extends AppCompatActivity {

    private Button leftButton, rightButton, expensesBtn;
    private Button addExpenseBtn; // declare addExpenseBtn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main);

        leftButton = findViewById(R.id.summaryBtn);
        rightButton = findViewById(R.id.expensesBtn);
        addExpenseBtn = findViewById(R.id.addExpenseBtn); // initialize addExpenseBtn

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Left Button Clicked");
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Right Button Clicked");
            }
        });

        // Set OnClickListener for addExpenseBtn
        addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AddExpenseGroup activity
                Intent intent = new Intent(GroupMainActivity.this, AddExpenseGroup.class);
                startActivity(intent);
            }
        });
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
        } else if (id == R.id.group1 || id == R.id.group2) {
            // Handle group selection from dropdown menu
            showToast("Group Selected: " + item.getTitle());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
