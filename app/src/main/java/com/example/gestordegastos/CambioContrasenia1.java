package com.example.gestordegastos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CambioContrasenia1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_contrasenia1);
    }

    public void Cambiocontrasenia2(View view){
        Intent intent = new Intent(view.getContext(), CambioContrasenia2.class);
        view.getContext().startActivity(intent);
    }
}