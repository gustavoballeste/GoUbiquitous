package com.example.android.sunshine.wearable;


import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class SunshineWearableListenerService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    private boolean mConnected = false;

    private static final String TAG = "SunshineListenerService";

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "Connected to Google API client");
        mConnected = true;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Connection to Google API client was suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "Connection to Google API client has failed");
        mConnected = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mConnected)
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "data events: " + dataEvents);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(SunshineWearableUtils.FORECAST_PATH)) {
            SunshineWearableUtils.sendTodaysForecast(getApplicationContext(), mGoogleApiClient);
        }
        else {
            super.onMessageReceived(messageEvent);
            Log.d(TAG, "onMessageReceived - wrong path");
        }
    }
}
