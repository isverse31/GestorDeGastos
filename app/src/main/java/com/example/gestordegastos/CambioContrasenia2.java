package com.example.gestordegastos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CambioContrasenia2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_contrasenia2);
    }
    public void Sesion(View view){
        Intent intent = new Intent(view.getContext(), InicioSesion.class);
        view.getContext().startActivity(intent);
    }
}