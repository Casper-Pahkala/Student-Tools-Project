package com.example.kalenteri;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender {

    String userFcmToken;
    String title, firstName, lastName;
    String body;
    Context mContext;
    Activity mActivity;
    JSONObject notiObject;
    private DatabaseReference reference;
    private String userID;
    String name;


    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey ="AAAA7sW_2kA:APA91bEl3YNH4lk1-QBth8Z5MWNRFBhROSJu5u0z9ck6hLR647IZCz9v8VvsP9WPu0n_tCHJQwsvKdUHO6UQ8dNyrm1Bpv9hG14aDNKmRrK1Xt45qPgZyE7myZ6zRucXq3bGPKF7XMCS";

    public FcmNotificationsSender(String userFcmToken, String title, String body, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;


    }

    public void SendNotifications(String token,String name,String message) {
                    requestQueue = Volley.newRequestQueue(mActivity);
                    JSONObject mainObj = new JSONObject();
                    try {
                        mainObj.put("to", token);
                        notiObject = new JSONObject();
                        notiObject.put("title", name);
                        notiObject.put("body", message);
                        notiObject.put("tag", "tag_id");
                        mainObj.put("notification", notiObject);
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                // code run is got response

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // code run is got error

                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {


                                Map<String, String> header = new HashMap<>();
                                header.put("content-type", "application/json");
                                header.put("authorization", "key=" + fcmServerKey);
                                return header;


                            }
                        };
                        requestQueue.add(request);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }










    }
}
