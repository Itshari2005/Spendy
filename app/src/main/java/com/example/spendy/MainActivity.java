package com.example.spendy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private EditText editAmount, editDescription;
    private Button addButton;
    private ListView listView;
    private TextView textTotalExpense;
    private ArrayList<String> expenses;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;
    private int totalExpense = 0; // Variable to store total expense

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing UI components
        editAmount = findViewById(R.id.editAmount);
        editDescription = findViewById(R.id.editDescription);
        addButton = findViewById(R.id.buttonAddExpense);
        listView = findViewById(R.id.listViewExpenses);
        textTotalExpense = findViewById(R.id.textTotalExpense);

        // Load saved expenses
        sharedPreferences = getSharedPreferences("ExpenseTracker", Context.MODE_PRIVATE);
        expenses = new ArrayList<>(loadExpenses());

        // Calculate total expense
        calculateTotalExpense();

        // Set up ListView Adapter
        adapter = new ArrayAdapter<>(this, R.layout.list_item_expense, R.id.textExpenseItem, expenses);
        listView.setAdapter(adapter);

        // Add Expense Button Click
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = editAmount.getText().toString().trim();
                String description = editDescription.getText().toString().trim();

                if (!amountStr.isEmpty() && !description.isEmpty()) {
                    int amount = Integer.parseInt(amountStr);
                    totalExpense += amount; // Add to total

                    String expense = "₹" + amount + " - " + description;
                    expenses.add(expense);
                    adapter.notifyDataSetChanged();
                    saveExpenses();
                    updateTotalExpense();

                    editAmount.setText("");
                    editDescription.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Enter amount and description!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Long-press to delete expense
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            String expense = expenses.get(position);
            int amount = extractAmount(expense); // Extract amount from string

            totalExpense -= amount; // Deduct from total
            expenses.remove(position);
            adapter.notifyDataSetChanged();
            saveExpenses();
            updateTotalExpense();

            Toast.makeText(MainActivity.this, "Expense removed", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    // Save expenses to SharedPreferences
    private void saveExpenses() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> expenseSet = new HashSet<>(expenses);
        editor.putStringSet("expenses", expenseSet);
        editor.apply();
    }

    // Load expenses from SharedPreferences
    private Set<String> loadExpenses() {
        return sharedPreferences.getStringSet("expenses", new HashSet<>());
    }

    // Calculate total expense from saved data
    private void calculateTotalExpense() {
        totalExpense = 0;
        for (String expense : expenses) {
            totalExpense += extractAmount(expense);
        }
        updateTotalExpense();
    }

    // Extract the amount from expense string (₹100 - Food)
    private int extractAmount(String expense) {
        try {
            String amountStr = expense.split(" - ")[0].replace("₹", "").trim();
            return Integer.parseInt(amountStr);
        } catch (Exception e) {
            return 0;
        }
    }

    // Update the total expense TextView
    private void updateTotalExpense() {
        textTotalExpense.setText("Total: ₹" + totalExpense);
    }
}
