package com.olegdim.phonetracking;

import android.location.Location;

import java.util.Date;

/**
 * Created by Oleg on 14.03.2016.
 */
public class LocationData {

    private int _id;
    private Date _time;
    private Location _location;

    public LocationData(int id, Date time, Location location) {
        this._id = id;
        this._time = time;
        this._location = location;
    }

    public LocationData(Location location) {
        this._id = 0;
        this._time = new Date(new Date().getTime());
        this._location = location;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Date get_time() { return _time; }

    public void set_time(Date _time) {
        this._time = _time;
    }

    public Location get_location() {
        return _location;
    }

    public void set_location(Location _location) {
        this._location = _location;
    }
}
