package com.example.expensetracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            getData();
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

                        // Sort the list based on 'time' field in descending order
                        Collections.sort(dsList, new Comparator<DocumentSnapshot>() {
                            @Override
                            public int compare(DocumentSnapshot o1, DocumentSnapshot o2) {
                                Long time1 = (Long) o1.get("time");
                                Long time2 = (Long) o2.get("time");
                                return time2.compareTo(time1);
                            }
                        });

                        for (DocumentSnapshot ds : dsList) {
                            ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);
                            if (expenseModel.getType().equals("Income")) {
                                income += expenseModel.getAmount();
                            } else {
                                expense += expenseModel.getAmount();
                            }
                            expenseAdapter.add(expenseModel);
                        }
                        setUpGraph();
                    }
                });
    }

    private void setUpGraph() {
        // Get expenses grouped by category
        Map<String, Long> categoryExpenses = new HashMap<>();
        for (ExpenseModel expenseModel : expenseAdapter.getItems()) {
            if (!expenseModel.getType().equals("Income")) { // Exclude income expenses
                String category = expenseModel.getCategory();
                long amount = expenseModel.getAmount();

                // Aggregate expenses by category
                if (categoryExpenses.containsKey(category)) {
                    categoryExpenses.put(category, categoryExpenses.get(category) + amount);
                } else {
                    categoryExpenses.put(category, amount);
                }
            }
        }

        // Create pie entries for each category
        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Long> entry : categoryExpenses.entrySet()) {
            String category = entry.getKey();
            long amount = entry.getValue();

            pieEntries.add(new PieEntry(amount, category)); // Add category as label
        }

        // Create pie data set
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");

        // Customize pie data set
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setDrawValues(false); // Disable drawing values
        pieDataSet.setValueLinePart1OffsetPercentage(80.f); // Position of first line (line break)
        pieDataSet.setValueLinePart2Length(0.6f); // Length of second line
        pieDataSet.setValueLineColor(Color.BLACK); // Color of value line

        // Create pie data
        PieData pieData = new PieData(pieDataSet);

        // Set data to pie chart
        binding.pieChart.setData(pieData);
        binding.pieChart.invalidate();
    }





    @Override
    public void onClick(ExpenseModel expenseModel) {
        intent.putExtra("model",expenseModel);
        startActivity(intent);
    }
}