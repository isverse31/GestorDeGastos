package com.example.gestordegastos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    // Constructor sin argumentos necesario para instanciación por Android
    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Lógica para manejar la recepción de la notificación
        SplashScreen.createNotification(context);

    }
}


