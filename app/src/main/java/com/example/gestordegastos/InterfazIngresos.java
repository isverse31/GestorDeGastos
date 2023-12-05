package com.example.gestordegastos;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class InterfazIngresos extends AppCompatActivity {

    private static final String PREFS_NAME = "ExpensesPrefs";
    private static final String EXPENSES_KEY = "expenses";

    private TextView expensesTextView;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz_ingresos);
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

       // expensesTextView = findViewById(R.id.textViewNombre); // Corrección: inicializar expensesTextView
        displayExpenses();
    }

    private void displayExpenses() {
        String expenseList = preferences.getString(EXPENSES_KEY, "");
        expensesTextView.setText(expenseList);
    }

    public void AgregarMovimiento(View view){
        Intent intent = new Intent(view.getContext(), AgregarGastos.class);
        view.getContext().startActivity(intent);
    }

    public void IrGastos(View view){
        Intent intent = new Intent(view.getContext(), InterfazGastos.class);
        view.getContext().startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}