package de.tubs.ibr.dtn.chat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class Cloud_MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Cloud_MapActivity";
    private FirebaseUser firebaseUser;
    private GoogleMap mMap;
    LatLng myLocation;          // stores location of current user who is using the app.
    String UserId;
    String key;
    Marker myMarker = null;

    // Define hash map to store markers of individual users.
    HashMap<String, Marker> markerMap;

    // hash map to store cloud coordinates of individual users.
    HashMap<String, LatLng> cloudCoordinatesMap;

    // hash map to store cloud coordinates of individual users.
    HashMap<String, LatLng> prevCoordinatesMap;

    // hash map to store flags for initializing the markers for individual users.
    HashMap<String, Boolean> checkMap;

    // create object of CalculateDistanceTime class to calculate distance.
    CalculateDistanceTime distance_task = new CalculateDistanceTime(this);


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("universal","MapActivity: Inside onDestroy");
        // This the last call of this activity. Here we will stop and the service and delete data from database in onDestroy.
        // Location Tracking is no longer required.
        // Now you no longer wants to push data. So, change the value of shared variable as false.
        Log.d("universal","MapActivity: Stoping Service");
        Intent removeData = new Intent(this,Cloud_ServiceCloudData.class);
        stopService(removeData);    // This will call onDestroy method of Cloud_ServiceCloudData.
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("lastcall","onPause called inside mapActivity");
        Log.d("universal","MapActivity: Inside onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("lastcall","onResume called inside mapActivity");
        Log.d("universal","MapActivity: Inside onResume");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d("lastcall","onPostResume called inside mapActivity");
        Log.d("universal","MapActivity: Inside onPostResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("lastcall","onStart called inside mapActivity");
        Log.d("universal","MapActivity: Inside onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("lastcall","onStop called inside mapActivity");
        Log.d("universal","MapActivity: Inside onStop");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("universal","MapActivity: Inside onCreate");
        setContentView(R.layout.cloud_map_activity);

        markerMap = new HashMap<String, Marker>();
        cloudCoordinatesMap = new HashMap<String, LatLng>();
        prevCoordinatesMap = new HashMap<String, LatLng>();
        checkMap = new HashMap<String, Boolean>();

        // Initialize myLocation with some random location which is far far away.(Just for the sake of initialization).
        myLocation = new LatLng(82.8628,135.0000);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void animateMarker(final String key1) {
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                    Log.d("flow", "Thread Running");
                    elapsed = SystemClock.uptimeMillis() - start;
                    t = elapsed / durationInMs;
                    v = interpolator.getInterpolation(t);

                    LatLng currentPosition = new LatLng(
                            prevCoordinatesMap.get(key1).latitude * (1 - t) + cloudCoordinatesMap.get(key1).latitude * t,
                            prevCoordinatesMap.get(key1).longitude * (1 - t) + cloudCoordinatesMap.get(key1).longitude * t);
                    Log.d("mehul", "current interpolate position " + currentPosition);

                    // Here check for collisions with the person who is using the app.

               /* if(UserId.equals(key)) {
                    // just update the global myLocation variable.
                    myLocation = currentPosition;
                }

                else {
                    // You are here means, this is not you.
                    // Compare your current Location with myLocation.
                    LatLng copyCurrentPosition = new LatLng(currentPosition.latitude,currentPosition.longitude);
                    distance_task.getDirectionsUrl(copyCurrentPosition, myLocation);
                    // One you get the distance, results will returned by setLoadListener

                    distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                        @Override
                        public void taskCompleted(String[] time_distance) {
                            Log.d("collision", "Distance between you and " +key + "is " +time_distance[0]);
                            Log.d("collision","Expected time between you and " +key + "is" +time_distance[1]);

                            // Now you have to blink the marker to alert the collision
                            Timer markerTimer = new Timer();
                            markerTimer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(myMarker.equals(true)) {

                                            }
                                        }
                                    });
                                }
                            },0,500);
                        }

                    });
                }*/

                    myMarker.setPosition(currentPosition);
                    //prevCoordinatesMap.put(key, currentPosition);
                    //Log.d("flow","Adding marker at "+currentPosition);
                    // Set camera position to current person who is using the app.
                    if (UserId.equals(key)) {
                        // mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(currentPosition)
                                .zoom(17)
                                .bearing(30)
                                .tilt(45)
                                .build()));
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
    public void onMapReady(final GoogleMap mGoogleMap) {
        mMap = mGoogleMap;
        Log.d(TAG, "OnMapReady called in Cloud_MapActivity");
        Log.d("universal","MapActivity: Inside onMapReady");
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Permission already granted in main activity.
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Vehicle_Info");
        Log.d(TAG, "Database reference created");
        final GeoFire geoFire = new GeoFire(databaseReference);
        databaseReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                UserId = firebaseUser.getUid();  // userId of current user who is using the app.
                Log.d("flow", "MapActivity: Inside onChildChanged " + UserId);

                key = dataSnapshot.getKey();     // key of current user whose data got changed.

                boolean flag_check = checkMap.containsKey(key);
                if (!flag_check) {
                    Log.d("flow","New User. Empty flag_check. Initialise flag_check to false");
                    checkMap.put(key, false);
                }

                geoFire.getLocation(key, new LocationCallback() {
                    @Override
                    public void onLocationResult(String key, GeoLocation location) {
                        Log.d("flow","Inside onLocation Result");
                        LatLng cloudCoordinates = new LatLng(location.latitude, location.longitude);
                        boolean flag_value = checkMap.get(key);     // this retrieves value corresponding to given key.

                        if(!flag_value) {
                            Log.d("flow","Initial flag checking.");
                            checkMap.put(key,true);
                            // New User. Set initial parameters and Call the thread.
                            cloudCoordinatesMap.put(key,cloudCoordinates);
                           //TODO: prevCoordinatesMap.put(key,cloudCoordinates);
                            LatLng delete = new LatLng(7.596000 , 78.1231604);
                            prevCoordinatesMap.put(key,delete);
                            // For the first time, every new user will go through this check.
                            // so, here create a new marker, initialize it and save it to marker HashMap.
                            Log.d("flow","Adding Initial Marker");
                            myMarker = mMap.addMarker(new MarkerOptions()
                                    .position(cloudCoordinatesMap.get(key))
                                    .icon(BitmapDescriptorFactory.defaultMarker(114)));
                            //  .anchor((float)0.5,(float)0.5)
                            //  .rotation((float)90.0));

                            markerMap.put(key,myMarker);
                            Log.d("flow","String (Initial) marker value "+markerMap.get(key));

                            Log.d("flow","Calling animate marker");
                            // Calling animateMarker function. Infinite thread will be started in animate marker itself.
                            animateMarker(key);
                        }

                        else {
                            // Set only the final coordinates value.
                            Log.d("flow","changing value of final coordinate");
                            // TODO:
                            //cloudCoordinatesMap.put(key,cloudCoordinates);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
