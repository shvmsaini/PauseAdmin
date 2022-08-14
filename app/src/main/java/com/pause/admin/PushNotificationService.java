package com.pause.admin;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PushNotificationService extends FirebaseMessagingService {
    public static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String SERVER_KEY = "key=AAAAKhULbIU:APA91bGhjN7vWFfggGgyKXPKkQIJ9DMQAg2BpFrndnTBpAs21s3UbmX8ejxfglsVtLrmKnncY9FbINO4XNdJT7qkU-jpE2ay95hwDjHhivabrRTFhjNehSrG2kStlFM5GyKSjLLl6q2n";
    private static final String TAG = PushNotificationService.class.getSimpleName();
    private static int id = 0;

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String title = Objects.requireNonNull(message.getNotification()).getTitle();
        String text = message.getNotification().getBody();
        String CHANNEL_ID = "MESSAGE";
        Intent i = new Intent(this, TasksActivity.class);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
            pendingIntent = PendingIntent.getActivity(this, 0, i,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);
        else pendingIntent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Message Notification",
                NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(id++, notification.build());

        super.onMessageReceived(message);
    }

    public void pushNotification(Context c, String token, String title, String message) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(c);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("title", title).put("body", message);
            jsonObject.put("notification", notification);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL,
                    jsonObject, response -> Log.d(TAG, response.toString()),
                    error -> Log.e(TAG, "Unable to request")) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };
            Log.d(TAG, "pushNotification: to Token" + token);
            queue.add(request); // send notification
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        HomeActivity.dbUtils.postToken(token);
    }
}
