package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class FirstScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_screen);

        // Get references to the buttons
        Button personalButton = findViewById(R.id.personal_button);
        Button groupButton = findViewById(R.id.group_button);

        // Set click listeners for the buttons
        personalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(FirstScreenActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

//        groupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to GroupActivity
//                Intent intent = new Intent(FirstScreenActivity.this, GroupActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
