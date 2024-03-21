package com.example.expensetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        type = getIntent().getStringExtra("type");
        expenseModel = (ExpenseModel) getIntent().getSerializableExtra("model");

        if (expenseModel != null) {
            type = expenseModel.getType();
            binding.amount.setText(String.valueOf(expenseModel.getAmount()));
            binding.category.setText(expenseModel.getCategory());
            binding.note.setText(expenseModel.getNote());

            if (type.equals("Income")) {
                binding.incomeRadio.setChecked(true);
            } else {
                binding.expenseRadio.setChecked(true);
            }
        }

        binding.incomeRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Income";
            }
        });

        binding.expenseRadio.setOnClickListener(new View.OnClickListener() {
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
                Intent resultIntent = new Intent();
                resultIntent.putExtra("model", expenseModel);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                updateExpense();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("model", expenseModel);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
            return true;
        }
        if (id == R.id.deleteExpense) {
            deleteExpense();
            return true; // Return true to indicate that the event has been consumed
        }
        return super.onOptionsItemSelected(item); // Return false if the menu item is not recognized
    }


//    private void deleteExpense() {
//        FirebaseFirestore
//                .getInstance()
//                .collection("expenses")
//                .document(expenseModel.getExpenseId())
//                .delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        setResult(Activity.RESULT_OK); // Set result to indicate success
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Handle failure
//                    }
//                });
//    }
//
//    private void createExpense() {
//        // Your existing code...
//        FirebaseFirestore
//                .getInstance()
//                .collection("expenses")
//                .document(expenseId)
//                .set(expenseModel)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        setResult(Activity.RESULT_OK); // Set result to indicate success
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Handle failure
//                    }
//                });
//    }
//
//    private void updateExpense() {
//        // Your existing code...
//        FirebaseFirestore
//                .getInstance()
//                .collection("expenses")
//                .document(expenseId)
//                .set(model)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        setResult(Activity.RESULT_OK); // Set result to indicate success
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Handle failure
//                    }
//                });
//    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        type = getIntent().getStringExtra("type");
//        expenseModel = (ExpenseModel) getIntent().getSerializableExtra("model");
//
//        if (type == null) {
//            type = expenseModel.getType();
//            binding.amount.setText(String.valueOf(expenseModel.getAmount()));
//            binding.category.setText(expenseModel.getCategory());
//            binding.note.setText(expenseModel.getNote());
//        }
//
//
//        if (type.equals("Income")){
//            binding.incomeRadio.setChecked(true);
//        }
//        else {
//            binding.expenseRadio.setChecked(true);
//        }
//
//        binding.incomeRadio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                type = "Income";
//            }
//        });
//
//        binding.expenseRadio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                type = "Expense";
//            }
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater  menuInflater = getMenuInflater();
//        if (expenseModel == null){
//            menuInflater.inflate(R.menu.add_menu,menu);
//        }else{
//            menuInflater.inflate(R.menu.update_menu,menu);
//        }
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.saveExpense) {
//            if (expenseModel == null) {
//                createExpense();
//            } else {
//                updateExpense();
//            }
//            return true;
//        }
//        if (id == R.id.deleteExpense) {
//            deleteExpense();
//            return true; // Return true to indicate that the event has been consumed
//        }
//        return super.onOptionsItemSelected(item); // Return false if the menu item is not recognized
//    }
//


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
        String amount = binding.amount.getText().toString();
        String note = binding.note.getText().toString();
        String category = binding.category.getText().toString();

        boolean incomeChecked = binding.incomeRadio.isChecked();

        if (incomeChecked){
            type = "Income";
        }
        else {
            type = "Expense";
        }

        if (amount.trim().length() == 0){
            binding.amount.setError("Empty");
            return;
        }
        ExpenseModel expenseModel = new ExpenseModel(expenseId, note, category, type, Long.parseLong(amount), Calendar.getInstance().getTimeInMillis(), FirebaseAuth.getInstance().getUid());

        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseId)
                .set(expenseModel)
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
    private void updateExpense() {

        String expenseId = expenseModel.getExpenseId();
        String amount = binding.amount.getText().toString();
        String note = binding.note.getText().toString();
        String category = binding.category.getText().toString();

        boolean incomeChecked = binding.incomeRadio.isChecked();

        if (incomeChecked){
            type = "Income";
        }
        else {
            type = "Expense";
        }

        if (amount.trim().length() == 0){
            binding.amount.setError("Empty");
            return;
        }
        ExpenseModel model = new ExpenseModel(expenseId, note, category, type, Long.parseLong(amount), expenseModel.getTime(), FirebaseAuth.getInstance().getUid());

        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseId)
                .set(model)
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