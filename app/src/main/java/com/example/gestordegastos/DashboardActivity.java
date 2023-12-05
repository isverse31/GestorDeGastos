package com.example.gestordegastos;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestordegastos.Gasto;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        } else {
            // Manejar el caso en que el usuario no esté autenticado
        }

        BarChart barChart = findViewById(R.id.barChart);

        // Obtener los gastos desde Firebase y mostrar el gráfico
        obtenerGastosYMostrarGrafico(barChart);
    }

    private void obtenerGastosYMostrarGrafico(BarChart barChart) {
        DatabaseReference gastosRef = mDatabase.child("Users").child(userID).child("Gastos");

        gastosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Gasto> gastos = new ArrayList<>();

                for (DataSnapshot gastoSnapshot : dataSnapshot.getChildren()) {
                    Gasto gasto = gastoSnapshot.getValue(Gasto.class);
                    gastos.add(gasto);
                }

                // Crear el gráfico de barras con la frecuencia de imágenes
                mostrarGraficoFrecuenciaImagenes(gastos, barChart);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DashboardActivity", "Error al obtener gastos desde Firebase: " + databaseError.getMessage());
            }
        });
    }

    private void mostrarGraficoFrecuenciaImagenes(List<Gasto> gastos, BarChart barChart) {
        // Contar la frecuencia de cada posición de imagen
        Map<Integer, Integer> frecuenciaImagenes = contarFrecuenciaImagenes(gastos);

        // Crear una lista de entradas de datos para el gráfico
        List<BarEntry> entries = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : frecuenciaImagenes.entrySet()) {
            entries.add(new BarEntry(entry.getKey(), entry.getValue()));
        }

        // Configurar el conjunto de datos y el gráfico de barras
        BarDataSet barDataSet = new BarDataSet(entries, "Frecuencia de Imágenes");
        BarData barData = new BarData(barDataSet);

        // Configurar el eje X (posiciones de imagen)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(45f);

        // Configurar el gráfico y mostrar
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.invalidate(); // Actualizar el gráfico
    }

    private Map<Integer, Integer> contarFrecuenciaImagenes(List<Gasto> gastos) {
        Map<Integer, Integer> frecuenciaImagenes = new HashMap<>();

        for (Gasto gasto : gastos) {
            int posicionImagen = gasto.getImagen();

            // Incrementar la frecuencia para la posición de imagen actual
            frecuenciaImagenes.put(posicionImagen, frecuenciaImagenes.getOrDefault(posicionImagen, 0) + 1);
        }

        return frecuenciaImagenes;
    }
}
