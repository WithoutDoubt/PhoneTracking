package com.olegdim.phonetracking;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import net.hockeyapp.android.CrashManager;

/**
 * Created by Oleg on 27.03.2016.
 */
public class LocationClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LocationManager locationManager;    //  Old way of location
    private GoogleApiClient mGoogleApiClient;   //  New way of location using GoogleAPIClient. Old android version does not support

    private final Context mContext;
    private Location location;
//
    public boolean isGoogleAPIClientAvailable;
    public boolean canGetLocation;

    private com.google.android.gms.location.LocationListener locationListenerAPI;
    private LocationListener locationListener;

    private boolean mInProgress;

    private Boolean servicesAvailable = false;

    public LocationClient(Context context) {
        Log.d(Constants.LOG_TAG, "LocationClient Create");

        CrashManager.register(context);

        this.mContext = context;
        this.isGoogleAPIClientAvailable = false;

        locationListenerAPI = new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(Constants.LOG_TAG, "Google API Client onLocationChanged");
//
                DatabaseHelper db = new DatabaseHelper(mContext);
                db.saveLocationData(new LocationData(location));
            }
        };

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(Constants.LOG_TAG, "onLocationChanged");
//
                DatabaseHelper db = new DatabaseHelper(mContext);
                db.saveLocationData(new LocationData(location));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
//                Log.d(Constants.LOG_TAG, "Provider '" + provider + "' status changed: " + String.valueOf(status));
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
//
        buildGoogleApiClient();
    }

    public void connect() {
        Log.d(Constants.LOG_TAG, "Connect to Google API Client");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void removeLocationUpdates() {
        Log.d(Constants.LOG_TAG, "remove location updates");
        if (isGoogleAPIClientAvailable) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListenerAPI);
        } else {
            locationManager.removeUpdates(locationListener);
        }
    }

    public boolean isConnected() {
        return canGetLocation || isGoogleAPIClientAvailable;
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d(Constants.LOG_TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this.mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public boolean requestLocationUpdates() {
        Log.d(Constants.LOG_TAG, "requestLocationUpdates");
//
//  If Google API Client is available and is connected, then request location updates using Google API Client
//
        if (isGoogleAPIClientAvailable) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                        .setInterval(Constants.MIN_TIME_BW_UPDATES)
                        .setFastestInterval(Constants.MIN_TIME_BW_UPDATES)
                        .setSmallestDisplacement(Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES)
                        , locationListenerAPI);

                location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } catch (Exception e) {
                Log.e(Constants.LOG_TAG, "Error requesting location update using Google API Client: " + e.getMessage());
            }
//  If Google API Client is not available or failed to connect, then request location updates using Google API Client
//
//
        } else {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
            if (!isGPSEnabled && !isNetworkEnabled) {
                canGetLocation = false;
            } else {
                this.canGetLocation = true;
                if (locationManager != null) {
                    locationManager.requestLocationUpdates(
                            isGPSEnabled ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER,
                            Constants.MIN_TIME_BW_UPDATES,
                            Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    // First get location from Network Provider
    //
                    location = locationManager.getLastKnownLocation(isGPSEnabled ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER);
                }
            }

        }
        canGetLocation = location != null;
        return canGetLocation;
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(Constants.LOG_TAG, "Google API Client connected");
//
        this.isGoogleAPIClientAvailable = true;
/*
        Intent intent = new Intent(this, LocationReceiver.class);
        PendingIntent locationIntent = PendingIntent.getBroadcast(getApplicationContext(), 14872, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationRequest, locationIntent);
*/
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(Constants.LOG_TAG, "Google API Client connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(Constants.LOG_TAG, "Google API Client is not available!!!");
        this.isGoogleAPIClientAvailable = false;
        this.canGetLocation = true;
//

        this.locationManager = (LocationManager) this.mContext.getSystemService(Context.LOCATION_SERVICE);
        this.locationManager.addNmeaListener(new GpsStatus.NmeaListener() {
            public void onNmeaReceived(long timestamp, String nmea) {
                DatabaseHelper db = new DatabaseHelper(mContext);
                db.saveNMEAData(new NMEAData(nmea));
            }
        });
        this.locationManager.addGpsStatusListener(new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
 //               Log.d(Constants.LOG_TAG, "GPS Status changed: " + String.valueOf(event));
            }
        });

        requestLocationUpdates();
    }
}
