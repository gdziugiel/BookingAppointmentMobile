package com.example.androidphpmysql.broadcast;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.androidphpmysql.R;
import com.example.androidphpmysql.reservedservice.ReservedServiceActivity;

public class CancelServiceBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int reservedServiceId = intent.getIntExtra("id", 0);
        String serviceName = intent.getStringExtra("service_name");
        String subServiceName = intent.getStringExtra("sub_service_name");
        String dateTime = intent.getStringExtra("date_time");
        String clientEmail = intent.getStringExtra("client_email");
        Intent notifyIntent = new Intent(context, ReservedServiceActivity.class);
        notifyIntent.putExtra("id", reservedServiceId);
        notifyIntent.putExtra("email", clientEmail);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyReservation")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Rezerwacja została anulowana")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(serviceName + " - " + subServiceName + "\n" + dateTime))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(notifyPendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(reservedServiceId, builder.build());
    }
}
