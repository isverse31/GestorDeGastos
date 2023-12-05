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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class InicioSesion extends AppCompatActivity {

    private EditText rEditTextEmail;
    private EditText rEditTextContrasenia;
    private Button rButtonLogin;

    private String Email = "";
    private String Contrasenia = "";
    FirebaseAuth rAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        rAuth = FirebaseAuth.getInstance();

        rEditTextEmail = findViewById(R.id.Correo);
        rEditTextContrasenia = findViewById(R.id.Contrasenia);
        rButtonLogin = findViewById(R.id.AceptarInicio);

        rButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Email = rEditTextEmail.getText().toString();
                Contrasenia = rEditTextContrasenia.getText().toString();

                if(!Email.isEmpty() && !Contrasenia.isEmpty()){
                    loginUser();
                }
                else {
                    Toast.makeText(InicioSesion.this, "Complete los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void CrearCuenta(View view){
        Intent intent = new Intent(view.getContext(), CrearCuenta.class);
        view.getContext().startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void Cambiocontrasenia1(View view){
        Intent intent = new Intent(view.getContext(), CambioContrasenia1.class);
        view.getContext().startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    public void loginUser(){
        rAuth.signInWithEmailAndPassword(Email, Contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(InicioSesion.this, Principio.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }
                else {
                    Toast.makeText(InicioSesion.this, "No se pudo iniciar sesión, compruebe los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(rAuth.getCurrentUser() !=null){
            startActivity(new Intent(InicioSesion.this, InterfazGastos.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            finish();
        }
    }
}