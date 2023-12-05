package com.example.gestordegastos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AgregarGastos extends AppCompatActivity {

    private static final String PREFS_NAME = "ExpensesPrefs";
    private static final String EXPENSES_KEY = "expenses";
    private EditText expenseEditText;
    private EditText descriptionEditText;
    private Spinner imageSpinner;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private static final List<Integer> imageList = new ArrayList<Integer>() {{
        add(R.drawable.comida);
        add(R.drawable.ropa);
        add(R.drawable.salud);
        add(R.drawable.otro);
        // Agrega más imágenes según tus necesidades
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_gastos);

        expenseEditText = findViewById(R.id.CantidadMovimiento);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        imageSpinner = findViewById(R.id.spinnerImage);

        // Inicializar Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Inicializar Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

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

    public void BotonAgregarGasto(View view) {

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        String expense = expenseEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        int selectedImage = imageSpinner.getSelectedItemPosition();

        if (!expense.isEmpty() && !description.isEmpty()) {
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                // Si el usuario está autenticado, obtener el UID
                String userID = user.getUid();

                // Obtener la referencia al nodo del usuario en la base de datos
                DatabaseReference userRef = mDatabase.child(userID).child("Gastos");

                // Obtener la fecha actual
                String currentDate = getCurrentDate();

                // Crear un objeto para los datos a guardar
                Gasto gasto = new Gasto(currentDate, expense, description, selectedImage);

                // Guardar los datos en Realtime Database
                String gastoKey = userRef.push().getKey();
                userRef.child(gastoKey).setValue(gasto);

                // Resetear los campos después de agregar un gasto
                expenseEditText.setText("");
                descriptionEditText.setText("");
                imageSpinner.setSelection(0); // Resetear el spinner al primer elemento después de agregar un gasto
            }
            String currentDate = getCurrentDate();
            String currentExpenses = preferences.getString(EXPENSES_KEY, "");

            String imageString = String.valueOf(selectedImage);

            // Restar la cantidad del nuevo gasto a CantidadInicial1
            restarCantidadInicial(Double.parseDouble(expense));

            String newExpense = currentDate + "  " + ": -$" + expense + " " + description +" (Image: " + imageString +   ")\n";
            currentExpenses += newExpense;
            preferences.edit().putString(EXPENSES_KEY, currentExpenses).apply();
            expenseEditText.setText("");
            descriptionEditText.setText("");

            finish();

        }
        Intent intent = new Intent(view.getContext(), InterfazGastos.class);
        intent.putExtra("gasto Actualizado", true);
        view.getContext().startActivity(intent);
    }

    private void restarCantidadInicial(double gasto) {
        // Obtener el usuario actualmente autenticado
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Si el usuario está autenticado, obtener el UID
            String userID = user.getUid();

            // Obtener la referencia al nodo del usuario en la base de datos
            DatabaseReference userRef = mDatabase.child(userID);

            // Obtener la cantidad inicial actual desde la base de datos
            userRef.child("CantidadInicial1").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        double cantidadInicial = dataSnapshot.getValue(Double.class);

                        // Restar la cantidad del nuevo gasto
                        cantidadInicial -= gasto;

                        // Actualizar la CantidadInicial1 en la base de datos
                        userRef.child("CantidadInicial1").setValue(cantidadInicial);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Manejar errores de lectura desde la base de datos
                    // Puedes agregar un Toast o log para indicar el error si es necesario
                }
            });
        }
    }




    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void AgregarIngreso(View view){
        Intent intent = new Intent(view.getContext(), AgregarIngresos.class);
        view.getContext().startActivity(intent);
    }
}
