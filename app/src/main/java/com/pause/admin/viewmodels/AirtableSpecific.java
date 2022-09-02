package com.pause.admin.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AirtableSpecific extends ViewModel {
    private static final String TAG = AirtableSpecific.class.getSimpleName();
    MutableLiveData<Map<String, Object>> usage;

    public MutableLiveData<Map<String, Object>> getUsage(android.content.Context c) {
        if (usage == null) {
            usage = new MutableLiveData<>();
            getUsageHelper(usage, c);
        }
        return usage;
    }

    private void getUsageHelper(MutableLiveData<Map<String, Object>> usage, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        Map<String, String> params = new HashMap<>();
        params.put("records", String.valueOf(1));
        String url = "https://api.airtable.com/v0/appJekLjTTEi54qov/Table%201?api_key=keygMcUekLsvrCTMT";
//        String url = "https://api.airtable.com/v0/appc90PoRgSlyivRp/Tasks?api_key=keyl4sIVi90Epm4VL";
        JSONObject paramaters = new JSONObject(params);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, paramaters,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONArray obj = (JSONArray) response.get("records");
                        JSONObject o = (JSONObject) obj.get(2);
                        JSONObject p = (JSONObject) o.get("fields");
                        Log.d(TAG, "getUsageHelper: " + p);
                        HashMap<String, Object> map = new Gson().fromJson(String.valueOf(p), HashMap.class);
                        usage.postValue(map);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }, error -> {
            error.printStackTrace();
            Log.d(TAG, "getUsageHelper: lmao");
        });
        Volley.newRequestQueue(context).add(request);
    }


}
