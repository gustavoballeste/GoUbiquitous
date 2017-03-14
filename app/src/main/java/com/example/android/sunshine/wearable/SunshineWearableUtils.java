package com.example.android.sunshine.wearable;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class SunshineWearableUtils {
    private static final String TAG = "SunshineWearableUtils";

    // Forecast data columns
    public static final String[] FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
    };

    public static final int INDEX_WEATHER_DATE          = 0;
    public static final int INDEX_WEATHER_MAX_TEMP      = 1;
    public static final int INDEX_WEATHER_MIN_TEMP      = 2;
    public static final int INDEX_WEATHER_ID            = 3;

    // DataMap
    public static final String FORECAST_PATH   = "/forecast";
    public static final String MAX_TEMP_KEY    = "max_temp";
    public static final String MIN_TEMP_KEY    = "min_temp";
    public static final String WEATHER_ID_KEY  = "weather_id";
    public static final String TIMESTAMP_KEY   = "timestamp";

    public static void sendTodaysForecast(Context context, GoogleApiClient googleApiClient) {
        PutDataMapRequest dataMap = loadTodaysForecast(context);
        PutDataRequest request = dataMap.asPutDataRequest();

        Wearable.DataApi.putDataItem(googleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        Log.d(TAG, "Sending DataItem was successful: " + dataItemResult.getStatus()
                                .isSuccess());

                    }
                });

    }

    public static PutDataMapRequest loadTodaysForecast(Context context) {
        Uri todaysForecastUri = WeatherContract.WeatherEntry
                .buildWeatherUriWithDate(SunshineDateUtils.normalizeDate(System.currentTimeMillis()));
        PutDataMapRequest dataMap = PutDataMapRequest.create(FORECAST_PATH);
        dataMap.setUrgent();
        Cursor todaysWeatherCursor = context.getContentResolver().query(
                todaysForecastUri,
                FORECAST_PROJECTION,
                null,
                null,
                null);

        if(todaysWeatherCursor != null && todaysWeatherCursor.moveToNext()) {
            dataMap.getDataMap().putString(MAX_TEMP_KEY,
                    SunshineWeatherUtils.formatTemperature(context,
                            todaysWeatherCursor.getDouble(INDEX_WEATHER_MAX_TEMP)));
            dataMap.getDataMap().putString(MIN_TEMP_KEY,
                    SunshineWeatherUtils.formatTemperature(context,
                            todaysWeatherCursor.getDouble(INDEX_WEATHER_MIN_TEMP)));
            dataMap.getDataMap().putInt(WEATHER_ID_KEY,
                    todaysWeatherCursor.getInt(INDEX_WEATHER_ID));
            dataMap.getDataMap().putLong(TIMESTAMP_KEY, System.currentTimeMillis());
            todaysWeatherCursor.close();
        }
        return dataMap;
    }
}
