package de.tubs.ibr.dtn.chat;


        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.SystemClock;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.support.annotation.Nullable;
        import android.widget.Toast;

        import java.net.InetAddress;
        import java.util.Observable;
        import java.util.Observer;
        import java.util.Stack;

        import static android.content.ContentValues.TAG;


class ObservedObject extends Observable {
    private boolean watchedValue;

    public ObservedObject(boolean value) {
        watchedValue = value;
    }

    public void setValue(boolean value) {

        // if value has changed notify observers
        if(!watchedValue==value) {
            watchedValue = value;

            // mark as value changed
            setChanged();
        }
    }
}
public class MergeActivity extends Activity {

    boolean isConnected;
    public boolean name;
    Button cloudButton;
    Button peerButton;
     int i=0;
     int x;
    int count=0;

    int y;
     boolean[] flag = new  boolean[1000];
 /*   public MergeActivity(boolean name) {
        this.name = name;
    }
    public MergeActivity()
    {
        System.out.println("in merge constructor"+i);
        // create watched and watcher objects
if(i>=1) {
    System.out.println("flag "+i+" "+this.flag[i]);
    ObservedObject watched = new ObservedObject(false);

    // watcher object listens to object change
    MergeActivity watcher = new MergeActivity(flag[i-1]);

    // add observer to the watched object
    watched.addObserver(watcher);

    // trigger value change
    System.out.println("setValue method called...");
    watched.setValue(flag[i]);

    // check if value has changed
    if (watched.hasChanged())
        System.out.println("Value changed");
    else
        System.out.println("Value not changed");
}
    }


    public void update(Observable obj, Object arg) {
        System.out.println("Update called");
    }

*/





    public void givePopup(final int y,int count){
        int z;
        final AlertDialog.Builder builder2=new AlertDialog.Builder(MergeActivity.this);
        if(y==0) {
            builder2.setMessage("Internet connectivity present . Press OK to use cloud app");
        }
        else if(y==1)
        {
            builder2.setMessage("No Internet connection . Press OK to use p2p app");
        }
    builder2.setPositiveButton("OK",new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {

// TODO Auto-generated method stub

//            Toast.makeText(getApplicationContext(), "U Clicked OK", Toast.LENGTH_LONG).show();
            if(y==1){
            Intent peerIntent = new Intent(MergeActivity.this,PeerActivity.class);
            startActivity(peerIntent);
            }
            else if(y==0) {
                Intent ServiceIntent = new Intent(MergeActivity.this, Cloud_ServiceCloudData.class);
                startService(ServiceIntent);
                Intent MapIntent = new Intent(MergeActivity.this, Cloud_MapActivity.class);
                startActivity(MapIntent);

            }
        }

    });

    builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

        @Override

        public void onClick(DialogInterface dialog, int which) {

// TODO Auto-generated method stub

            Toast.makeText(getApplicationContext(), "U Clicked Cancel ", Toast.LENGTH_LONG).show();

        }

    });
    //builder2.show();

        AlertDialog alert = builder2.create();
        Stack<AlertDialog> alert_stack = new Stack<AlertDialog>();
        alert_stack.push(alert);
       // if(alert_stack.pop()!=null)

                alert_stack.pop().show();

      //if(count>=3)
        //  alert.dismiss();
}


public void checkInternet(){
    Log.d("check","in check internet");

    AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
            Log.d("Check", "Thread to check internet connectivity started in background..");
       //     while (true) {
                //  Log.d("Check", "internet connection present..");
                final ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                assert cm != null;
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                 isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                //isConnected;

            if (!isConnected) {
                    //8/ Toast.makeText(MergeActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                    flag[i]=false;
                    //Log.d("Check", "No Internet Connection");
//                    showDialog(MergeActivity.this,"Hi","Hello");
                //    Intent peerIntent = new Intent(MergeActivity.this,MapsActivity.class);
                  //  startActivity(peerIntent);
                }
                else{
                    //Log.d("Check", "Internet Connection present");
                    flag[i]=true;
                }
          //  System.out.println("flag "+i+" "+flag[i]);

            i++;
            if(i>=2) {
                if (flag[i - 1] != flag[i - 2]) {
                    if(flag[i-1]==true){
                        //no internet connection
                     y=0;
                    }
                    else if(flag[i-1]==false){
                        y=1;
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //Toast.makeText(getBaseContext(),"Iam inside thread",Toast.LENGTH_SHORT).show();
                            count++;
                            SystemClock.sleep(7000);
                            givePopup(y,count);


                        }
                    });

                     System.out.println("State changed in run" + flag[i - 1] + flag[i]);

                } else {
                    System.out.println("State not changed in run" + i);

                }
            }
            x=0;
            if(i>800){
                    i=0;
                }

                }

       // }
    });



}
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merge);
/*        checkInternet=isInternetAvailable();
        Log.d("Check","flag="+checkInternet);
        if(checkInternet==true){
            Log.d("Check", "Internet Connection");
        }
        else {
            Log.d("Check", "No Internet Connection");
        }

       */
        final Handler handler = new Handler();
        final int delay = 3000  ; //milliseconds
        Log.d(TAG, "Before Runnable");
    //    final MeFragment mf=new MeFragment();
        Log.d(TAG, "Before Runnable problem");
        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                Log.d(TAG, "Before onMapready");
                // display(googleMap);
                checkInternet();
                //new MergeActivity();

                Log.d(TAG, "Before CreateView");
               // mf.createView();

                handler.postDelayed(this, delay);
            }
        }, delay);
//givePopup();

        cloudButton = (Button) findViewById(R.id.buttonCloud);
        peerButton = (Button) findViewById(R.id.buttonPeer);
        checkInternet();
       cloudButton.setOnClickListener(myhandler1);
        peerButton.setOnClickListener(myhandler2);
    }

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            // Here start Cloud App
            Intent ServiceIntent = new Intent(MergeActivity.this, Cloud_ServiceCloudData.class);
            startService(ServiceIntent);


            // Run async task in background to check for internet connectivity. We cant run it as an Intent service as
            // because only one thread can run parallel on intent service.

            // Here we can parallely start activity to display maps because we are using
            // Intent service which runs on background. Background service will push data to cloud and
            // foreground service will simultaneously display on maps.
            // Log.d(TAG, "Starting Cloud_MapActivity");

            Intent MapIntent = new Intent(MergeActivity.this, Cloud_MapActivity.class);
            startActivity(MapIntent);
        }
    };

    View.OnClickListener myhandler2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent peerIntent = new Intent(MergeActivity.this,PeerActivity.class);
            startActivity(peerIntent);
        }
    };


}