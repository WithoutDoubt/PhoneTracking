package com.olegdim.phonetracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {
    public LocationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.LOG_TAG, intent.getAction());
        if (intent.getAction().equals("android.location.PROVIDERS_CHANGED")) {
            Log.d(Constants.LOG_TAG, "Provider changed!");
        }
        if (intent.getAction().equals("android.location.MODE_CHANGED")) {
            Log.d(Constants.LOG_TAG, "Mode changed!");
        }
    }
}
