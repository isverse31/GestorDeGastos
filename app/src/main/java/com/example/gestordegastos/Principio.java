package com.example.gestordegastos;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class Principio extends AppCompatActivity {

    private EditText cantidadInicialEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principio);

        cantidadInicialEditText = findViewById(R.id.CantidadInicial1);
        Button continuarButton = findViewById(R.id.ButtonIntroducir);

        mAuth = FirebaseAuth.getInstance();
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        continuarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleContinuarButtonClick();
            }
        });
    }

    private void handleContinuarButtonClick() {
        String cantidadInicialStr = cantidadInicialEditText.getText().toString();

        if (!cantidadInicialStr.isEmpty()) {
            double nuevaCantidadInicial = Double.parseDouble(cantidadInicialStr);

            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                String userID = user.getUid();
                sumarCantidadInicial(userID, nuevaCantidadInicial);
            } else {
                showAuthenticationError();
            }
        }
    }

    private void sumarCantidadInicial(String userID, final double nuevaCantidadInicial) {
        DatabaseReference userRef = usersDatabaseReference.child(userID);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    updateExistingUser(userID, dataSnapshot, nuevaCantidadInicial);
                } else {
                    createNewUser(userID, nuevaCantidadInicial);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showDatabaseReadError(databaseError);
            }
        });
    }

    private void updateExistingUser(String userID, DataSnapshot dataSnapshot, double nuevaCantidadInicial) {
        Double cantidadActual = dataSnapshot.child("CantidadInicial1").getValue(Double.class);

        // Verifica si la cantidad actual es nula y asigna un valor predeterminado
        if (cantidadActual == null) {
            cantidadActual = 0.0; // O el valor inicial adecuado
        }

        double nuevaCantidadTotal = cantidadActual + nuevaCantidadInicial;

        Map<String, Object> map = new HashMap<>();
        map.put("CantidadInicial1", nuevaCantidadTotal);

        usersDatabaseReference.child(userID).updateChildren(map)
                .addOnSuccessListener(aVoid -> showSuccessMessage("Datos actualizados exitosamente"))
                .addOnFailureListener(e -> showErrorMessage("Error al actualizar datos: " + e.getMessage()));
    }

    private void createNewUser(String userID, double nuevaCantidadInicial) {
        Map<String, Object> data = new HashMap<>();
        data.put("CantidadInicial1", nuevaCantidadInicial);

        usersDatabaseReference.child(userID).setValue(data)
                .addOnSuccessListener(aVoid -> showSuccessMessage("Datos guardados exitosamente"))
                .addOnFailureListener(e -> showErrorMessage("Error al guardar datos: " + e.getMessage()));
    }

    private void showAuthenticationError() {
        Toast.makeText(Principio.this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
    }

    private void showSuccessMessage(String message) {
        Toast.makeText(Principio.this, message, Toast.LENGTH_SHORT).show();
        startNextActivity();
    }

    private void showErrorMessage(String message) {
        Toast.makeText(Principio.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showDatabaseReadError(DatabaseError databaseError) {
        Toast.makeText(Principio.this, "Error al leer datos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void startNextActivity() {
        Intent intent = new Intent(Principio.this, InterfazGastos.class);
        intent.putExtra("CANTIDAD_INICIAL", Double.parseDouble(cantidadInicialEditText.getText().toString()));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        startActivity(intent);
    }
}
