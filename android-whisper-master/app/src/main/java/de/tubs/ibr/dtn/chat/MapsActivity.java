package de.tubs.ibr.dtn.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.location.LocationListener;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.UiSettings;
import com.instacart.library.truetime.TrueTimeRx;

import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import de.tubs.ibr.dtn.SecurityService;
import de.tubs.ibr.dtn.SecurityUtils;
import de.tubs.ibr.dtn.Services;
import de.tubs.ibr.dtn.api.ServiceNotAvailableException;
import de.tubs.ibr.dtn.chat.core.Buddy;
import de.tubs.ibr.dtn.chat.service.ChatService;
import de.tubs.ibr.dtn.chat.service.Utils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;


import de.tubs.ibr.dtn.chat.core.Buddy;
import de.tubs.ibr.dtn.chat.core.Roster;
import de.tubs.ibr.dtn.chat.service.ChatService;

import static de.tubs.ibr.dtn.chat.R.drawable.away;
import static de.tubs.ibr.dtn.chat.R.drawable.busy;
//import static de.tubs.ibr.dtn.chat.R.drawable.car_symbol;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double my_lat[][] = new double[1000][1000];
    double my_lon[][] = new double[1000][1000];
    float zoom1;
    int j = 0, k = 0;
    private Roster mRoster;
    private final static String TAG = "MeFragment";
    int flag = 0;
     int l=0;
    void display(GoogleMap googleMap) {


        Calendar cal1 = Calendar.getInstance();
        long milis1 = cal1.getTimeInMillis();

        boolean hidemarker = false;
        boolean hidemarker1 = false;
        Marker marker;
        Marker marker1;

        Log.d(TAG, "onMAPREADY called");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
        //Contains My GPS co-ordinates
        String my_presence = preferences.getString("presencetag", "unavailable");
        //get the latitude and longitude from my presence
        final int mid = my_presence.length() / 2;//get the middle of the String
        String[] parts = {my_presence.substring(0, mid), my_presence.substring(mid)};
        //first part ::Latitude
        my_lat[k][j] = Latlong.lat;
        //second part ::Longitude
        my_lon[k][j] = Latlong.lng;
        //my_lat[-1]=my_lat[0];
        //my_lon[-1]=my_lon[0];
        LatLng startLatLng;
        if (flag == 0)
            startLatLng = new LatLng(my_lat[k][j], my_lon[k][j]);
        else
            startLatLng = new LatLng(my_lat[k][j - 1], my_lon[k][j - 1]);
        LatLng endLatLng = new LatLng(my_lat[k][j], my_lon[k][j]);
    /*     CalculateDistanceTime distance_task = new CalculateDistanceTime(this);

        distance_task.getDirectionsUrl(startLatLng, endLatLng);

        distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
         @Override
          public void taskCompleted(String[] time_distance) {
             Log.d("suhel", "Time:" + time_distance[1]);
            Log.d("suhel", "Distance:" + time_distance[0]);
        // approximate_time.setText("" + time_distance[1]);
        // approximate_diatance.setText("" + time_distance[0]);
        }

        });
        */


        //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(car_symbol);

        //   Date noReallyThisIsTheTrueDateAndTime = TrueTime.now();
        // Log.d(TAG,"Date="+noReallyThisIsTheTrueDateAndTime);

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.clear();
        mRoster = new Roster();
        mRoster.open(getApplicationContext());
        // Add a marker in Sydney and move the camera
         zoom1 = mMap.getCameraPosition().zoom;
         System.out.println("zoom1_1= "+zoom1);
        LatLng sydney = new LatLng(my_lat[k][j], my_lon[k][j]);
        //  List<LatLng> points=new ArrayList<LatLng>();
        //points.add(new LatLng(my_lat[i],my_lon[i]));
        //animateMarker(marker1, prev, sydney);
        //if(sydney!=null) {
        marker1 = mMap.addMarker(new MarkerOptions().position(endLatLng).title("My Location"));
        //}
        Cursor c = mRoster.queryBuddies();
        RosterAdapter.ColumnsMap cmap = new RosterAdapter.ColumnsMap();
        int i = 0;
        Log.d(TAG, "HIIIIIIIISUHEL");
        //double lat = Latlong.lat;
        //double lng = Latlong.lng;
        //   LatLng sydney = new LatLng(my_lat, my_lon);
        // mMap.addMarker(new MarkerOptions().position(startLatLng).title("My Location"));
        if(flag==1&&startLatLng!=endLatLng)
            animateMarker(marker1, startLatLng, endLatLng,k);

        k++;
        Log.d(TAG, String.valueOf(Latlong.lat));
        while (c.moveToNext()) {
            i++;
            Buddy buddy = new Buddy(this, c, cmap);
            String buddyPresence = buddy.getPresence();

            Log.d(TAG, "HIIIIIIIISUHEL123");
            final int b_mid = buddyPresence.length() / 2;//get the middle of the String
            String[] b_parts = {buddyPresence.substring(0, b_mid), buddyPresence.substring(b_mid)};
            //first part ::Latitude
            //    j=0;
            my_lat[k][j] = Double.parseDouble(b_parts[0]);
            // double b_lat =lat;
            //double b_lon=lng;
            //second part ::Longitude
            my_lon[k][j] = Double.parseDouble(b_parts[1]);
            Log.d(TAG, "Buddies lat="+String.valueOf(my_lat[k][j]));
            LatLng buddyLoc;
            LatLng buddyLoc1;
            if(flag==0)
                buddyLoc = new LatLng(my_lat[k][j], my_lon[k][j]);
                //if(buddyLoc!=null) {
            else
                buddyLoc = new LatLng(my_lat[k][j-1], my_lon[k][j-1]);

            marker = mMap.addMarker(new MarkerOptions().position(buddyLoc).title("Buddy ::" + i));
            buddyLoc1 = new LatLng(my_lat[k][j], my_lon[k][j]);
            if(flag==1&&buddyLoc!=buddyLoc1)
                animateMarker(marker, buddyLoc, buddyLoc1,k);
            //}
            k++;
        }

        System.out.println("zoom1_2= "+zoom1);
        System.out.println("l value is= "+l);
        if(l<=1) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(endLatLng));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(mMap.getCameraPosition().target)
                    .zoom(zoom1)
                    .bearing(30)
                    .tilt(45)
                    .build()));
        }
        Calendar cal2 = Calendar.getInstance();
        long milis2 = cal2.getTimeInMillis();
        long diff = milis2 - milis1;
        Log.d(TAG, "Time to display all nearby devices=" + diff);
        flag=1;
        j++;
        k=0;
        if(j>=800)
            j=0;


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
        //Contains My GPS co-ordinates
        //      String my_presence = preferences.getString("presencetag", "unavailable");
        //get the latitude and longitude from my presence
        //    final int mid = my_presence.length() / 2;//get the middle of the String
        //  String[] parts = {my_presence.substring(0, mid),my_presence.substring(mid)};
        //first part ::Latitude
        //my_lat = Latlong.lat;
        //second part ::Longitude
        //my_lon = Latlong.lng;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        TrueTimeRx.build()
                .withConnectionTimeout(31428)
                .withRetryCount(100)
                .withSharedPreferences(this)
                .withLoggingEnabled(true)
                .initializeRx("time.google.com")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Date>() {
                    @Override
                    public void accept(Date date) {
                        if (!TrueTimeRx.isInitialized()) {
                            //   Log.d(TAG, "Sorry TrueTime not yet initialized");
                            return;
                        }

                        Date trueTime = TrueTimeRx.now();
                        //Log.d(TAG, "Date: " + trueTime);
                        System.out.println("Date= " + trueTime.getTime());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        //Log.d(TAG, "Error: ", throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() {
                        //Log.d(TAG, "Completed!");
                    }
                });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
//car_symbol mycar



    public void animateMarker(final Marker myMarker, final LatLng prevCoordinates, final LatLng cloudCoordinates, final int k) {
        Log.d("suhel", "interpolation started from " + prevCoordinates + " " + cloudCoordinates);

        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float durationInMs = 1000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        prevCoordinates.latitude * (1 - t) + cloudCoordinates.latitude * t,
                        prevCoordinates.longitude * (1 - t) + cloudCoordinates.longitude * t);
                Log.d("suhel", "current interpolate position " + currentPosition);
                myMarker.setPosition(currentPosition);
                myMarker.setPosition(currentPosition);
                // Set camera position to current person who is using the app.
                // mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
               // UiSettings.setZoomControlsEnabled(true);
                if(k==0) {
                    if(l<=1) {
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(currentPosition)
                                .zoom(zoom1)
                                .bearing(30)
                                .tilt(45)
                                .build()));
                    }
                }
                if (t < 1) {
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }

            }
        });

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        final Handler handler = new Handler();
        final int delay = 2000  ; //milliseconds
        Log.d(TAG, "Before Runnable");
        final MeFragment mf=new MeFragment();
        Log.d(TAG, "Before Runnable problem");
        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                Log.d(TAG, "Before onMapready");
                l++;
                if(l>=1000)
                    l=0;
                display(googleMap);
                Log.d(TAG, "Before CreateView");
                mf.createView();

                handler.postDelayed(this, delay);
            }
        }, delay);

    }

}