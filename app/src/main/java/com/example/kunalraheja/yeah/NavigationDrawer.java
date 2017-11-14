package com.example.kunalraheja.yeah;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {


    private GoogleMap mMap;
    private  boolean hybrid=false;
    ProgressDialog pd;
    GcmNetworkManager gcmNetworkManager;
   ImageView nav_header;
    FirebaseAuth firebaseAuth;
    String myUid=FirebaseAuth.getInstance().getCurrentUser().getUid();


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RequireItems.locationRequest(this);
        setContentView(R.layout.activity_navigation_drawer);

        pd=new ProgressDialog(this);
        pd.setMessage("Make sure you are connected to internet");
        pd.setTitle("Logging in");
        pd.show();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(!hybrid){
                    fab.setImageResource(R.drawable.ic_visibility_black_24dp);
                   mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                   hybrid=true;
               }else{
                   fab.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                   mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    hybrid=false;
               }

            }
        });

        final FloatingActionButton friends = (FloatingActionButton) findViewById(R.id.floating_friends);
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in1 = new Intent(NavigationDrawer.this, OnlyFriends.class);
                startActivity(in1);


            }
        });
        friends.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Intent in1 = new Intent(NavigationDrawer.this, AllUsers.class);
                startActivity(in1);
                return false;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        init_navigationHeader(navigationView);

        // setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setUpMapIfNeeded();







        firebaseAuth = FirebaseAuth.getInstance();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        // Check if we were successful in obtaining the map.
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void init_navigationHeader(final NavigationView navigationView){

        if(navigationView==null)return;
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //setting up header
                View header = navigationView.getHeaderView(0);

                UserDetail u =  dataSnapshot.getValue(UserDetail.class);
                pd.dismiss();

                TextView name = (TextView) header.findViewById(R.id.nav_head_name);
                name.setText(u.getuName());
                TextView email = (TextView) header.findViewById(R.id.nav_head_email);
                email.setText(u.getEmail());
                ImageView pro_photo=(ImageView) header.findViewById(R.id.nav_head_addphoto);
                pro_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in1 = new Intent(NavigationDrawer.this, UpdateDetail.class);
                        startActivity(in1);
                    }
                });
                if(!u.getPhoto_url().isEmpty()) {
                    byte[] imageAsBytes = Base64.decode(u.getPhoto_url().getBytes(), Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    pro_photo.setImageBitmap(image);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.id_setting) {
            Intent in1 = new Intent(NavigationDrawer.this, GeofireActvity.class);
            startActivity(in1);
            return true;
        }

       else if (id == R.id.id_logout) {
            firebaseAuth.signOut();
            Intent in = new Intent(this, ExistingUser.class);
            startActivity(in);
            finish();
            return true;
        }

        else if(id== R.id.badge){
            Intent in = new Intent(this, NotificationTabs.class);
            startActivity(in);
            return true;
        }
        else if(id== R.id.id_edit_profile){
            Intent in = new Intent(this, UpdateDetail.class);
            startActivity(in);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_users) {
            Intent in1 = new Intent(NavigationDrawer.this, AllUsers.class);
            startActivity(in1);

        } else if (id == R.id.nav_friends) {
            Intent in1 = new Intent(NavigationDrawer.this, OnlyFriends.class);
            startActivity(in1);

        }else if(id == R.id.nav_setup_geofire){
            Intent in1 = new Intent(NavigationDrawer.this, GeofireActvity.class);
            startActivity(in1);

        }else if(id == R.id.nav_contact_us){
            Intent in1 = new Intent(NavigationDrawer.this, ContactUs.class);
            startActivity(in1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camer

        DatabaseReference tofriends=FirebaseDatabase.getInstance().getReference().child("Users").child(myUid).child("friends");
        tofriends.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final String name=dataSnapshot.getValue(String.class);
                DatabaseReference tofriendlocation=FirebaseDatabase.getInstance().getReference().child("Users").child(dataSnapshot.getKey()).child("locations");
                tofriendlocation.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       //Location location=dataSnapshot.getValue(Location.class);
                       // mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude()))).setTitle(name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng));//.icon(BitmapDescriptor ));;
            }
        });

//        Drawable circle = getResources().getDrawable(R.drawable.circle_shape);
//        Canvas canvas = new Canvas();
//        Bitmap bitmap = Bitmap.createBitmap(circle.getIntrinsicWidth(), circle.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        canvas.setBitmap(bitmap);
//        circle.setBounds(0, 0, circle.getIntrinsicWidth(), circle.getIntrinsicHeight());
//        circle.draw(canvas);
//        BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(bitmap);
//
//        mMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(41.906991, 12.453360))
//                        .title("My Marker")
//                        .icon(bd));


      //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION};

            ActivityCompat.requestPermissions(this, permissions, 1);

        }
        mMap.setMyLocationEnabled(true);

        startTask();
    }

    public void startTask() {
        GcmNetworkManager gcmNetworkManager = GcmNetworkManager.getInstance(this);

        Task task1 = new PeriodicTask.Builder()
                .setService(CoustomService.class)
                .setPeriod(30)
                .setFlex(10)                                //flexibility time // the task run within every 10 - 30 sec..
                .setTag("Task2")
                .setPersisted(true)       // for service start ia boot
                .build();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        gcmNetworkManager.schedule(task1);


    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NavigationDrawer Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.kunalraheja.yeah/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NavigationDrawer Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.kunalraheja.yeah/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


}
