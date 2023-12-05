package com.example.gestordegastos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AgregarIngresos extends AppCompatActivity {
    private static final String PREFS_NAME = "ExpensesPrefs2";
    private static final String EXPENSES_KEY = "expenses2";

    private EditText expenseEditText;
    private EditText descriptionEditText;
    private Spinner imageSpinner;
    private SharedPreferences preferences;

    private static final List<Integer> imageList = new ArrayList<Integer>() {{
        add(R.drawable.trabajo);
        add(R.drawable.prestamo);
        add(R.drawable.regalo);
        add(R.drawable.otro);
        // Agrega más imágenes según tus necesidades
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_ingresos);

        expenseEditText = findViewById(R.id.CantidadMovimiento);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        imageSpinner = findViewById(R.id.spinnerImage);
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        setupImageSpinner();
    }

    private void setupImageSpinner() {
        ImageAdapter adapter = new ImageAdapter(this, imageList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageSpinner.setAdapter(adapter);

        imageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }


    public void BotonAgregarIngreso(View view) {
        String expense = expenseEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        int selectedImage = imageSpinner.getSelectedItemPosition();


        if (!expense.isEmpty() && !description.isEmpty()) {
            String currentDate = getCurrentDate();
            String currentExpenses = preferences.getString(EXPENSES_KEY, "");

            String imageString = String.valueOf(selectedImage);

            String newExpense = currentDate + "  " + ": -$" + expense + " " + description +" (Image: " + imageString +   ")\n";
            currentExpenses += newExpense;
            preferences.edit().putString(EXPENSES_KEY, currentExpenses).apply();
            expenseEditText.setText("");
            descriptionEditText.setText("");// Reset spinner to the first item after adding an expense
        }

        Intent intent = new Intent(view.getContext(), InterfazIngresos.class);
        view.getContext().startActivity(intent);
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ", Locale.getDefault()); //HH:mm:ss
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void AgregarGasto(View view){
        Intent intent = new Intent(view.getContext(), AgregarGastos.class);
        view.getContext().startActivity(intent);
    }
}
