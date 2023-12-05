package com.example.gestordegastos;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class SplashScreen extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 1;
    private static final int ALARM_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Crea el canal de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        // Programa la notificación a las 15:04:15
        scheduleNotification();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    SystemClock.sleep(3000);
                    startActivity(new Intent(SplashScreen.this, InicioSesion.class));
                    finish();
                } catch (Exception e) {
                    // Manejar la excepción
                }
            }
        };
        thread.start();
    }

    private void scheduleNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Comprueba si la hora programada ya ha pasado hoy, en ese caso, programa para mañana
        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Crea un intent para la tarea programada
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                ALARM_REQUEST_CODE,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Configura AlarmManager para que se ejecute a la hora programada
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createNotificationChannel() {
        // Crea un NotificationChannel solo en dispositivos con Android 8.0 (Oreo) o superior
        String channelId = "channel_id";
        CharSequence channelName = "Nombre del canal";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

        // Registra el canal en el sistema
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    // Método estático para crear y mostrar la notificación
    public static void createNotification(Context context) {
        // Crea un objeto NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.comida) // Icono de la notificación
                .setContentTitle("¡¡Hora de anotar los gastos!!")
                .setContentText("¿Tiene gastos que agregar hoy?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Intent para abrir la actividad principal al hacer clic en la notificación
        Intent mainIntent = new Intent(context, InicioSesion.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Asocia el PendingIntent al clic de la notificación
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);  // Cierra la notificación después de hacer clic

        // Obtiene el NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Muestra la notificación
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
