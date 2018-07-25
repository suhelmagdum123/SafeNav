package de.tubs.ibr.dtn.chat;

import android.util.Log;

/**
 * Created by mehul on 18/12/17.
 */

public class Cloud_VehicleInfo {
    public double latitude;
    public double longitude;
    public double speed;
    public String id;

    public Cloud_VehicleInfo(){

    }

    public Cloud_VehicleInfo(String id, double latitude, double longitude, double speed) {
        Log.d("universal","VehicleInfo: Setting VehicleInfo");
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
    }

    public void setId(String id) { this.id = id; }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
