package com.example.expensetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

//import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.expensetracker.databinding.ActivityAddExpenseBinding;
import com.example.expensetracker.databinding.ActivityAddExpenseBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.UUID;

public class AddExpenseActivity extends AppCompatActivity {
    ActivityAddExpenseBinding binding;
    private String type;
    private ExpenseModel expenseModel;
    private EditText amountEditText, noteEditText;
    private RadioGroup typeRadioGroup;
    private RadioButton incomeRadio, expenseRadio;
    private Spinner categorySpinner;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        amountEditText = findViewById(R.id.amount);
        noteEditText = findViewById(R.id.note);
        typeRadioGroup = findViewById(R.id.typeRadioGroup);
        incomeRadio = findViewById(R.id.incomeRadio);
        expenseRadio = findViewById(R.id.expenseRadio);
        categorySpinner = findViewById(R.id.categorySpinner);

        // Populate Spinner with categories from resources
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Set default type to Expense
        type = "Expense";
        expenseModel = (ExpenseModel) getIntent().getSerializableExtra("model");
        if (expenseModel != null) {
            type = expenseModel.getType();
            amountEditText.setText(String.valueOf(expenseModel.getAmount()));
            noteEditText.setText(expenseModel.getNote());

            if (type.equals("Income")) {
                incomeRadio.setChecked(true);
            } else {
                expenseRadio.setChecked(true);
            }
        }

        // Radio button click listeners to set the expense type
        incomeRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Income";
            }
        });

        expenseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Expense";
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (expenseModel == null) {
            menuInflater.inflate(R.menu.add_menu, menu);
        } else {
            menuInflater.inflate(R.menu.update_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.saveExpense) {
            if (expenseModel == null) {
                createExpense();
            } else {
                updateExpense();
            }
            return true;
        }
        if (id == R.id.deleteExpense) {
            deleteExpense();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Reset expenseModel to null when navigating back without updating expense

        Log.d("AddExpenseActivity", "onBackPressed() called");
        expenseModel = null;
        Log.d("expense model after pressing back "," expense model after pressing back button "+expenseModel);
        super.onBackPressed();
    }


    private void deleteExpense() {
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseModel.getExpenseId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setResult(Activity.RESULT_OK); // Set result to indicate success
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });
    }
//
private void createExpense() {
    String expenseId = UUID.randomUUID().toString();
    String amount = amountEditText.getText().toString();
    String note = noteEditText.getText().toString();
    String category = categorySpinner.getSelectedItem().toString();

    // Check which radio button is checked and assign the type accordingly
    String type;
    if (incomeRadio.isChecked()) {
        type = "Income";
    } else {
        type = "Expense";
    }

    if (amount.trim().isEmpty()) {
        amountEditText.setError("Empty");
        return;
    }

    ExpenseModel expenseModel = new ExpenseModel(expenseId, note, category, type, Long.parseLong(amount), Calendar.getInstance().getTimeInMillis(), FirebaseAuth.getInstance().getUid());

    FirebaseFirestore.getInstance().collection("expenses").document(expenseId).set(expenseModel)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("AddExpenseActivity", "Expense created successfully");
                    setResult(Activity.RESULT_OK); // Set result to indicate success
                    finish(); // Finish the activity and return to the main activity
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("AddExpenseActivity", "Failed to create expense: " + e.getMessage());
                    // Handle failure
                }
            });
    startActivity(new Intent(this, MainActivity.class));
}


    private void updateExpense() {
        String expenseId = expenseModel.getExpenseId();
        String amount = amountEditText.getText().toString();
        String note = noteEditText.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();

        // Check which radio button is checked and assign the type accordingly
        String type;
        if (incomeRadio.isChecked()) {
            type = "Income";
        } else {
            type = "Expense";
        }

        if (amount.trim().isEmpty()) {
            amountEditText.setError("Empty");
            return;
        }

        ExpenseModel model = new ExpenseModel(expenseId, note, category, type, Long.parseLong(amount), expenseModel.getTime(), FirebaseAuth.getInstance().getUid());

        FirebaseFirestore.getInstance().collection("expenses").document(expenseId).set(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setResult(Activity.RESULT_OK); // Set result to indicate success
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });
    }

}