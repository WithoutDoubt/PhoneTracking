package com.olegdim.phonetracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Oleg on 27.03.2016.
 */

public class BootBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startIntent = new Intent(context, LocationService.class);
        startIntent.putExtra("fromBootReceiver", true);
        startWakefulService(context, startIntent);
    }
}
