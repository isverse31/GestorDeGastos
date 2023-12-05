package com.example.gestordegastos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private EditText rEditTextContrasenia3;
    private EditText rEditTextNombre;
    private Button rButtonRegistrar;

    //Variables de DAtos que se van a utilizar
    private String Nombre = "";
    private String Email = "";
    private String Contrasenia = "";

    private ImageButton toggleButton;

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
        rEditTextContrasenia3 = findViewById(R.id.Contrasenia3);
        rEditTextNombre = findViewById(R.id.Nombre);
        rButtonRegistrar = findViewById(R.id.AceptarCuenta);
        toggleButton = findViewById(R.id.toggleButton);  // Inicializa el botón de alternancia

        // Agrega un TextWatcher al EditText para mostrar la contraseña en tiempo real
        rEditTextContrasenia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No es necesario implementar este método
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Muestra la contraseña en tiempo real
                Contrasenia = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No es necesario implementar este método
            }
        });

        // Agrega un OnClickListener al botón de alternancia
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordVisibility();
            }
        });

        rButtonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Nombre = rEditTextNombre.getText().toString();
                Email = rEditTextEmail.getText().toString();

                String contraseniaConfirmacion = rEditTextContrasenia3.getText().toString();


                if (!Nombre.isEmpty() && !Email.isEmpty() && !Contrasenia.isEmpty()) {
                    if (Contrasenia.length() >= 8) {
                        if (Contrasenia.equals(contraseniaConfirmacion)) {
                            // Las contraseñas coinciden, procede con el registro
                            RegistarUsuario();
                        } else {
                            Toast.makeText(CrearCuenta.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CrearCuenta.this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CrearCuenta.this, "Hay campos vacíos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button cancelarButton = findViewById(R.id.CancelarCuenta);
        cancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle cancellation here, for example, go back to the previous activity
                finish();
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

    private void togglePasswordVisibility() {
        // Lógica para alternar la visibilidad de la contraseña
        int inputType = rEditTextContrasenia.getInputType();
        if ((inputType & android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0) {
            // La contraseña está oculta, así que la hacemos visible
            rEditTextContrasenia.setInputType(inputType & ~android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleButton.setImageResource(R.drawable.ic_visibility_off);
        } else {
            // La contraseña está visible, así que la hacemos oculta
            rEditTextContrasenia.setInputType(inputType | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleButton.setImageResource(R.drawable.ic_visibility_off);
        }

        // Mueve el cursor al final después de cambiar el tipo de entrada
        rEditTextContrasenia.setSelection(rEditTextContrasenia.length());
    }
}