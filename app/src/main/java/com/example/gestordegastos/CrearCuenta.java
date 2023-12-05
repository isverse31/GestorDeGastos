package com.example.gestordegastos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;

public class CrearCuenta extends AppCompatActivity {

    private EditText rEditTextEmail;
    private EditText rEditTextContrasenia;
    private EditText rEditTextNombre;
    private Button rButtonRegistrar;

    //Variables de DAtos que se van a utilizar
    private String Nombre = "";
    private String Email = "";
    private String Contrasenia = "";

    FirebaseAuth rAuth;
    DatabaseReference rDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cuenta);

        rAuth = FirebaseAuth.getInstance();
        rDatabase = FirebaseDatabase.getInstance().getReference();

        rEditTextEmail = findViewById(R.id.Correo2);
        rEditTextContrasenia = findViewById(R.id.Contrasenia2);
        rEditTextNombre = findViewById(R.id.Nombre);
        rButtonRegistrar = findViewById(R.id.AceptarCuenta);

        rButtonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Nombre = rEditTextNombre.getText().toString();
                Email = rEditTextEmail.getText().toString();
                Contrasenia = rEditTextContrasenia.getText().toString();

                if (!Nombre.isEmpty() && !Email.isEmpty() && !Contrasenia.isEmpty()){
                    if (Contrasenia.length() >= 8){
                        RegistarUsuario();
                    }
                    else {
                        Toast.makeText(CrearCuenta.this, "La contraseña debe tener almenos 8 caracteres", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(CrearCuenta.this, "Hay campos vacios", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void RegistarUsuario(){
        rAuth.createUserWithEmailAndPassword(Email, Contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Map<String, Object>map = new HashMap<>();
                    map.put("Nombre", Nombre);
                    map.put("Email", Email);
                    map.put("Contraseña", Contrasenia);

                    String id = rAuth.getCurrentUser().getUid();
                    rDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(CrearCuenta.this, Principio.class));
                                finish();
                            }
                            else {
                                Toast.makeText(CrearCuenta.this, "No se pudierón crear los datos correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(CrearCuenta.this, "No se pudo registrar este usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}