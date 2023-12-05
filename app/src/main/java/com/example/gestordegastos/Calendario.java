package com.example.gestordegastos;

import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Calendario extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView verGastosTextView;
    private DatabaseReference mDatabase;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        // Inicializar Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Obtener el ID del usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }

        // Inicializar componentes de la interfaz de usuario
        calendarView = findViewById(R.id.calendarView);
        verGastosTextView = findViewById(R.id.VerGastos);

        // Configurar el listener para el cambio de fecha en el calendario
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Formatear la fecha seleccionada
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);

                // Mostrar los gastos asociados a la fecha seleccionada
                mostrarGastosPorFecha(selectedDate);
            }
        });
    }

    private void mostrarGastosPorFecha(String selectedDate) {
        DatabaseReference gastosRef = mDatabase.child("Users").child(userID).child("Gastos");

        gastosRef.orderByChild("fecha").equalTo(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder gastosPorFecha = new StringBuilder();

                for (DataSnapshot gastoSnapshot : dataSnapshot.getChildren()) {
                    // Ajusta el nombre de la clase Gasto a lo que tengas en tu código
                    // Aquí estoy asumiendo que tu clase Gasto tiene un constructor sin argumentos (necesario para Firebase)
                    Gasto gasto = gastoSnapshot.getValue(Gasto.class);

                    if (gasto != null) {
                        // Agregar la información del gasto al StringBuilder
                        gastosPorFecha.append(gasto.getFecha())
                                .append(": -$")
                                .append(gasto.getCantidad())
                                .append(" ")
                                .append(gasto.getDescripcion())
                                .append(" (Image: ")
                                .append(gasto.getImagen())
                                .append(")\n");
                    }
                }

                // Mostrar los gastos asociados a la fecha seleccionada en el TextView VerGastos
                verGastosTextView.setText("Gastos para " + selectedDate + ":\n" + gastosPorFecha.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores de lectura desde la base de datos
                Log.e("Calendario", "Error al obtener gastos desde Firebase: " + databaseError.getMessage());
            }
        });
    }

}
