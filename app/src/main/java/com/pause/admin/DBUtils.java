package com.pause.admin;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class DBUtils {
    private static final String header1 = "Bearer " + HomeActivity.API_KEY;
    private static final String header2 = "Content-Type: application/json";

    public static void getData() {
        AndroidNetworking.get(HomeActivity.dataURL)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null) {
                            Log.d("ff", "onResponse: " + "null");
                            return;
                        }
                        Log.d("ff", "onResponse: " + response);
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    public static void postData() {
        JSONObject records = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            JSONObject data = new JSONObject();
            JSONObject fields = new JSONObject();
            fields.put("Name", "hello");
            fields.put("Status", "Done");
            fields.put("Start date", "2022-07-13");
            fields.put("Deadline", "2022-07-14");
            data.put("fields", fields);
            arr.put(data);
            records.put("records", arr);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("INSERT", "oops");
        }
        AndroidNetworking.post(HomeActivity.dataURL)
                .addHeaders("Authorization", header1)
                .addHeaders("Content-Type", header2)
                .setPriority(Priority.HIGH)
                .addJSONObjectBody(records)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("insert", "onResponse: " + response.toString());
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
    public static void postTask(JSONObject task){
        //TODO: Post to DB
    }

}
