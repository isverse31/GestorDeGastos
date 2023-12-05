package com.example.gestordegastos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class CambioContrasenia1 extends AppCompatActivity {

    private EditText correoEditText;
    private Button aceptarButton;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_contrasenia1);

        correoEditText = findViewById(R.id.Correo);
        aceptarButton = findViewById(R.id.AceptarInicio);

        firebaseAuth = FirebaseAuth.getInstance();

        aceptarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = correoEditText.getText().toString().trim();

                if (!correo.isEmpty()) {
                    // Aquí podrías enviar un correo de restablecimiento de contraseña
                    enviarCorreoRestablecimiento(correo);
                } else {
                    Toast.makeText(CambioContrasenia1.this, "Ingresa tu correo o usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void enviarCorreoRestablecimiento(String correo) {
        firebaseAuth.sendPasswordResetEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CambioContrasenia1.this, "Se ha enviado un correo para restablecer la contraseña", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(CambioContrasenia1.this, InicioSesion.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CambioContrasenia1.this, "Error al enviar el correo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
