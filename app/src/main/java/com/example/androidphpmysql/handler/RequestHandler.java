package com.example.androidphpmysql.handler;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestHandler {
    private RequestHandler instance;
    private RequestQueue requestQueue;
    private final Context ctx;

    public RequestHandler(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public synchronized RequestHandler getInstance(Context context) {
        if (this.instance == null) {
            this.instance = new RequestHandler(context);
        }
        return this.instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this.ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}