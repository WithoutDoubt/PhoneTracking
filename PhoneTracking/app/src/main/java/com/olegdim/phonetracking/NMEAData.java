package com.olegdim.phonetracking;

import java.util.Date;

/**
 * Created by Oleg on 14.03.2016.
 */
public class NMEAData {

    private int _id;
    private Date _time;
    private String _data;

    public NMEAData(int id, Date time, String data) {
        this._id = id;
        this._time = time;
        this._data = data;
    }

    public NMEAData(String data) {
        this._id = 0;
        this._time = new Date(new Date().getTime());
        this._data = data;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Date get_time() {
        return _time;
    }

    public void set_time(Date _time) {
        this._time = _time;
    }

    public String get_data() {
        return _data;
    }

    public void set_data(String _data) {
        this._data = _data;
    }
}
