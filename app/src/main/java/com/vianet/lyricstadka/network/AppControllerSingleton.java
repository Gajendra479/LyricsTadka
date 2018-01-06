package com.vianet.lyricstadka.network;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by editing2 on 28-Nov-17.
 */

public class AppControllerSingleton extends Application {
    public static final String Tag=AppControllerSingleton.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static AppControllerSingleton minstance;

    @Override
    public void onCreate() {
        super.onCreate();
        minstance=this;
    }

    public static AppControllerSingleton getMinstance(){
        return minstance;
    }

    public RequestQueue getmRequestQueue() {

        if (mRequestQueue==null){
            mRequestQueue= Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(Tag);
        getmRequestQueue().add(req);
    }
}
