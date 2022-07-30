package com.pause.admin;

import android.content.Context;
import android.os.StrictMode;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSend {
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = " AAAAKhULbIU:APA91bGhjN7vWFfggGgyKXPKkQIJ9DMQAg2BpFrndnTBpAs21s3UbmX8ejxfglsVtLrmKnncY9FbINO4XNdJT7qkU-jpE2ay95hwDjHhivabrRTFhjNehSrG2kStlFM5GyKSjLLl6q2n";
    private static final String token = "c5QofccVTzCHsrkiyLSv3O:APA91bEYnVo4rubY9d00by0AaLzr4k322GHAS9NYKSYsWCMrEktyUqHAQpwqnQc8tbuBeIyUBzaXeAySO94Kgb3PSDvg2a0DMzqBTubOjQqM5sQi5g3tZ7J2oKcv6o3qLLRHIHtstI8G";

    public static void pushNotification(Context c, String token, String title, String message) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(c);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("title", title)
                    .put("body", message);
            jsonObject.put("notification", notification);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, jsonObject, response -> {

            }, error -> {

            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError{
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };
            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
