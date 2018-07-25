package de.tubs.ibr.dtn.chat;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Cloud_ServiceCloudData extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private FusedLocationProviderClient mFusedLocationClient;     // Location client object
    private PendingIntent pendingIntent;
    private FirebaseUser firebaseUser;                     // Object to get unique user id
    DataSnapshot dataSnapshot;
    Location mLastlocation;



    private static final String TAG = "Cloud_ServiceCloudData";


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("universal", "ServiceCloudData: Inside onDestroy");
        Log.d("universal", "ServiceCloudData: Removing Location Updates");
        //Stop receiving location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);

        // onDestroy() method is called when we stop the service using stopService() method from some activity.
        // Here delete the entry from database.
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Vehicle_Info");
        GeoFire geoFire = new GeoFire(databaseReference);
        geoFire.removeLocation(id);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("serviceCheck", "onBind called");
        return null;
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d("universal", "ServiceCloudData: calling onLocationChanged ");
            onLocationChanged(locationResult.getLastLocation());
        }

        ;
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("cloudLifeCycle", "CloudServiceData called");
        Log.d("universal", "ServiceCloudData: Inside onCreate");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("universal", "ServiceCloudData: Google APIClient connected. Calling requestLocationUpdates");
        //Request location updates.
        function_LocationUpdates();
    }

    private void function_LocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(100);       // get updates in 0.1 seconds. However it drains more battery.
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);      // get location with high accuracy.
        Log.d("universal", "ServiceCloudData: Inside requestLocationUpdates");

        // Permission Already granted in Main Activity.
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("universal","ServiceCloudData: Inside onStartCommand. Connecting googleAPIClient");

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect(); // This will call onConnected method first.

        // start_sticky will force OS not to kill our app in case of resource crunch.
        return START_STICKY;
    }





    @Override
    public void onLocationChanged(Location location) {
        Log.d("universal","ServiceCloudData: Inside onLocationChanged");
        Log.d("universal","Received Location from Location callback"+location);
        Log.d("flow","Received location in location object" +location);

        //TODO: Here we are not receiving correct values. Please check the accuracy.

        Log.d("universal", "Latitude = " + location.getLatitude());
        Log.d("universal", "Longitude = " + location.getLongitude());

        // Make firebase unique id as Primary key of our database. This will uniquely identify our user.
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Vehicle_Info");
        GeoFire geoFire = new GeoFire(databaseReference);
        geoFire.setLocation(id, new GeoLocation(location.getLatitude(),location.getLongitude()));
        Log.d("flow","pushing Location to cloud...Pushed Location is: "+location.getLatitude() +" , "+ location.getLongitude());
        databaseReference.child(id).child("Speed").setValue(location.getSpeed());
    }
}
