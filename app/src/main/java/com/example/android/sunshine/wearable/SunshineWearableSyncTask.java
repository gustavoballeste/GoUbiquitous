package com.example.android.sunshine.wearable;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

public class SunshineWearableSyncTask extends AsyncTask<Void, Void, Void> {
    public static final int CONNECT_TIMEOUT = 30; // seconds
    private Context mContext;

    public SunshineWearableSyncTask(Context context) {
        this.mContext = context;
    }

    protected Void doInBackground(Void... voids) {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Wearable.API).build();
        googleApiClient.blockingConnect(CONNECT_TIMEOUT, TimeUnit.SECONDS);

        if(googleApiClient.isConnected()) {
            SunshineWearableUtils.sendTodaysForecast(mContext, googleApiClient);
            googleApiClient.disconnect();
        }

        return null;
    }

}
