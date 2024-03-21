package com.example.expensetracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

//import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker.databinding.ActivityMainBinding;
import com.example.expensetracker.databinding.ActivityMainBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemsClick{
    ActivityMainBinding binding;
    private ExpensesAdapter expenseAdapter;
    private long income = 0, expense = 0;
    Intent intent;

    private static final int ADD_EXPENSE_REQUEST_CODE = 1; // Define a request code
//    ActivityMainBinding binding;
//    private ExpensesAdapter expenseAdapter;
//    private long income = 0, expense = 0;
//    Intent intent;
    private ExpenseModel selectedExpenseModel; // To hold the selected expense for updating

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expenseAdapter = new ExpensesAdapter(this, this);
        binding.recycler.setAdapter(expenseAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        intent = new Intent(MainActivity.this, AddExpenseActivity.class);

        binding.addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                intent.putExtra("type", "Income");
                intent.putExtra("model", selectedExpenseModel); // Pass the selected expense model
                startActivityForResult(intent, ADD_EXPENSE_REQUEST_CODE);
            }
        });
        binding.addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                intent.putExtra("type", "Expense");
                intent.putExtra("model", selectedExpenseModel); // Pass the selected expense model
                startActivityForResult(intent, ADD_EXPENSE_REQUEST_CODE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EXPENSE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Update the selected expense model if an expense was modified in AddExpenseActivity
            selectedExpenseModel = (ExpenseModel) data.getSerializableExtra("model");
            // Refresh UI or perform any other necessary actions
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == ADD_EXPENSE_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
//            // Reset expenseModel if AddExpenseActivity was canceled
//            selectedExpenseModel = null;
//        }
//    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding =  ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        expenseAdapter = new ExpensesAdapter(this, this);
//        binding.recycler.setAdapter(expenseAdapter);
//        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
//
//        intent = new Intent(MainActivity.this, AddExpenseActivity.class);
//
//        binding.addIncome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intent.putExtra("type", "Income");
//                startActivity(intent);
//            }
//        });
//        binding.addExpense.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intent.putExtra("type", "Expense");
//                startActivity(intent);
//            }
//        });
//    }

    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please");
        progressDialog.setMessage("Wait");
        progressDialog.setCancelable(false);
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            progressDialog.show();
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressDialog.cancel();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.cancel();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        income = 0;
        expense = 0;
        getData();
    }
    private void getData(){
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        expenseAdapter.clear();
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot ds:dsList){
                            ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);
                            if (expenseModel.getType().equals("Income")){
                                income += expenseModel.getAmount();
                            }else{
                                expense += expenseModel.getAmount();
                            }
                            expenseAdapter.add(expenseModel);
                        }
                        setUpGraph();
                    }
                });
    }

    private void setUpGraph() {
        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorsList = new ArrayList<>();
        if (income != 0){
            pieEntryList.add(new PieEntry(income, "Income"));
            colorsList.add(getResources().getColor(R.color.green));
        }
        if (expense != 0){
            pieEntryList.add(new PieEntry(expense, "Expense"));
            colorsList.add(getResources().getColor(R.color.yellow));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, String.valueOf(income - expense));
        pieDataSet.setColors(colorsList);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.white));
        PieData pieDat = new PieData( pieDataSet);

        binding.pieChart.setData(pieDat);
        binding.pieChart.invalidate();
    }

    @Override
    public void onClick(ExpenseModel expenseModel) {
        intent.putExtra("model",expenseModel);
        startActivity(intent);
    }
}