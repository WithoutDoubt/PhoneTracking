package com.olegdim.phonetracking;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class LocationService extends Service {
    private LocationClient locationClient;
    private boolean mInProgress;

    @Override
    public void onCreate() {
        Log.d(Constants.LOG_TAG, "LocationService.onCreate");
        super.onCreate();

        mInProgress = false;
        locationClient = new LocationClient(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constants.LOG_TAG, "LocationService.onStartCommand");
//
        CrashManager.register(this);

        try {
            if (intent != null) {
                if (intent.getBooleanExtra("fromBootReceiver", false)) {
                    WakefulBroadcastReceiver.completeWakefulIntent(intent);
                }
            }

            if (locationClient == null) {
                mInProgress = false;
                locationClient = new LocationClient(this);
            }

            if (!locationClient.isConnected() || (!locationClient.isConnected() && !mInProgress)) {
                mInProgress = true;
                locationClient.connect();
            }

        } catch (Exception e) {
            Log.d(Constants.LOG_TAG, "LocationService.onStartCommand error: " + e.getMessage());
            throw e;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        locationClient.removeLocationUpdates();

        super.onDestroy();
    }

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
