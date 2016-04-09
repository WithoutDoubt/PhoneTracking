package com.olegdim.phonetracking;

/**
 * Created by Oleg on 27.03.2016.
 */
public final class Constants {
    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    public static final String LOG_TAG = "phoneTrack";

    public static final String LOCATION_CHANGED_ACTION = "com.olegdim.phonetracking.LOCATION_CHANGED";

}
