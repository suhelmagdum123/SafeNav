package de.tubs.ibr.dtn.chat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";   // used for logging
    private FirebaseUser user;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    boolean hasLoggedIn;





    public void locationRelatedTask() {

        Intent login = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(login);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("universal","MainActivity: Inside onStart");
        // Todo: Add Logout button.
        // if user is already signed in, no need to go to login activity. Directly jump to map activity.
        // This can be achieved by saving the state of user.
        // check here the saved state and in the else part call sign in or sign up activity.


        // Request for location permission.
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("delete", "No permission inside MainActivity. Requesting permission");
            // Permission not granted. Request for permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }

        else {
            locationRelatedTask();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("delete","Inside onActivityResult of MainActivity");
        Log.d("permission","request code MapActivity"+requestCode);
        Log.d("permission","result code MapActivity"+requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted. Now do location related tasks.
                    Log.d("delete","calling locationRelatedTask method");
                    locationRelatedTask();
                }

                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
