package com.example.kunalraheja.yeah;

/**
 * Created by Kunal Raheja on 18-07-2016.
 */


        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.location.Address;
        import android.location.Geocoder;
        import android.location.Location;
        import android.location.LocationManager;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.AsyncTask;
        import android.os.Build;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.NotificationCompat;
        import android.support.v4.app.TaskStackBuilder;
        import android.util.Log;
        import android.widget.Toast;

        import com.firebase.geofire.GeoFire;
        import com.firebase.geofire.GeoLocation;
        import com.firebase.geofire.GeoQuery;
        import com.firebase.geofire.GeoQueryEventListener;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.gcm.GcmNetworkManager;
        import com.google.android.gms.gcm.GcmTaskService;
        import com.google.android.gms.gcm.TaskParams;
        import com.google.android.gms.location.LocationServices;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.io.IOException;
        import java.util.List;
        import java.util.Locale;


public class CoustomService extends GcmTaskService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LocationManager locationManager;
    Location mCurrentLocation;
    GoogleApiClient myclient;

    double RADIUS= 0.5;

    GeoFire geoFire;
    SharedPreferences sharedPreferences;
    boolean chcked=true;



    @Override
    public void onInitializeTasks() {
        // When your package is removed or updated, all of its network tasks are cleared by
        // the GcmNetworkManager. You can override this method to reschedule them in the case of
        // an updated package. This is not called when your application is first installed.

    }

    @Override
    public int onRunTask(TaskParams taskParams) {


        SharedPreferences preferences = getSharedPreferences("DATA",
                MODE_PRIVATE);

        chcked=preferences.getBoolean("CHECK",true);


        Log.e("gcm", "Task has started");
        getLastKnownLocation();



        return GcmNetworkManager.RESULT_SUCCESS;
    }




    public void getLastKnownLocation() {
        Location lastKnownGPSLocation;
        Location lastKnownNetworkLocation;
        String gpsLocationProvider = LocationManager.GPS_PROVIDER;
        String networkLocationProvider = LocationManager.NETWORK_PROVIDER;
       Location mylocation;

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            lastKnownNetworkLocation = locationManager.getLastKnownLocation(networkLocationProvider);
            lastKnownGPSLocation = locationManager.getLastKnownLocation(gpsLocationProvider);
            mylocation = LocationServices.FusedLocationApi.getLastLocation(myclient);

           if(mylocation != null){
               Log.e("gcm", "Google api is used.");
               mCurrentLocation = mylocation;
               if(isNetworkAvailable()) {
                   setLocationOnServer();
               }
           }

           else if (lastKnownGPSLocation != null) {
                Log.e("gcm", "lastKnownGPSLocation is used.");
                this.mCurrentLocation = lastKnownGPSLocation;
                if(isNetworkAvailable()) {
                    setLocationOnServer();
                }
            }
            else if (lastKnownNetworkLocation != null) {
                Log.e("gcm", "lastKnownNetworkLocation is used.");
                this.mCurrentLocation = lastKnownNetworkLocation;
                if(isNetworkAvailable()) {
                    setLocationOnServer();
                }
            }
            else
            {
                Log.e("gcm", "lastLocation is not known.");
                return;
            }


        } catch (SecurityException sex) {
            Log.e("gcm", "Location permission is not granted!");
        }

        return;
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

    private void setLocationOnServer()
    {
        Log.e("gcm","Trying to put location on server");

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference userReference;
        FirebaseUser currentuser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=currentuser.getUid();
        userReference=database.getReference().child("Users").child(uid).child("locations");

        userReference.setValue(mCurrentLocation);

        userReference.addValueEventListener(listener);

       /* DatabaseReference geoRef = FirebaseDatabase.getInstance().getReference().child("Geofire");
        geoRef.addValueEventListener(listener);*/

        setUpGeoFire();

    }


    ValueEventListener listener=new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            /*UserInformation userInformation=dataSnapshot.getValue(UserInformation.class);
            String bGroup=userInformation.bloodgroup;*/
            setUpGeoFire();

        }



        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    private void setUpGeoFire()
    {

        new MyTask().execute(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        DatabaseReference loc=FirebaseDatabase.getInstance().getReference("Geofire");
        geoFire=new GeoFire(loc);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if(error==null)
                {
                    Log.e("gcm","location Updated Successfully");
                }

            }
        });


        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()),RADIUS);
        geoQuery.addGeoQueryEventListener(geoLis1);


    }


    private void notification(String name){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("GeoFire Notification")
                        .setContentText(name+"is in you radius !!");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, NotificationTabs.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(NavigationDrawer.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        int mId=1;

        if(chcked)
        mNotificationManager.notify(mId, mBuilder.build());

    }

    GeoQueryEventListener geoLis1 = new GeoQueryEventListener() {
        @Override
        public void onKeyEntered(String key, GeoLocation location) {

            final DatabaseReference friend = FirebaseDatabase.getInstance().getReference().child("Users").child(key).child("uName");
            friend.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   String name = dataSnapshot.getValue(String.class);

                   notification(name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        @Override
        public void onKeyExited(String key) {

        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {

        }

        @Override
        public void onGeoQueryReady() {

        }

        @Override
        public void onGeoQueryError(DatabaseError error) {

        }
    };






    @Override
    public void onCreate() {
        super.onCreate();

        buildClient();

    }

    @Override
    public int onStartCommand(Intent intent, int i, int i1) {

        myclient.connect();
      //  SharedPreferences sp1 = getPreferences(MODE_PRIVATE);
        return super.onStartCommand(intent, i, i1);
    }

    public synchronized void buildClient() {

        myclient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.e("gcm","Google Connected");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /*       For Geocoder           */
    private class MyTask extends AsyncTask<Double , Void , String> {

        @Override
        protected String doInBackground(Double... params) {
            StringBuilder sb = new StringBuilder();

            Geocoder geocoder = new Geocoder(getApplicationContext(),
                    Locale.getDefault());

            try {

                /*   Address of type android.io    */
                List<Address> addresses = geocoder.getFromLocation(params[0] , params [1] , 1);


                Address ad1= addresses.get(0);

                for(int i = 0 ; i< ad1.getMaxAddressLineIndex();i++){
                    sb.append(ad1.getAddressLine(i) );
                    sb.append(",");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            DatabaseReference loc=FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("myLoc");

            loc.setValue(s);

        }
    }
}


