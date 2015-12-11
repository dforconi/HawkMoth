package com.teamvictory.map;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = MapsActivity.class.getSimpleName();
    public final static String EXTRA_MESSAGE = "com.teamvictory.map.MESSAGE";
    public boolean start_stop =true;
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mPreviousLocation;
    private Marker mMarker;
    private Marker startMarker;
    private Marker endMarker;
    private boolean mRequestingLocation;
    Button button1;
    Button button2;
    public boolean startState =true;
    public double dist;   // will depend on units dist is measured in (meters, miles, feet, etc.)
    private long startTime;  // will depend on how time is measured (sec, mins, etc.)
    private long elapsedTime;
    private boolean running;  // used for timer
    private long secs,mins,hrs;
    private String hours,minutes,seconds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        addListenerOnButton();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5 * 1000)        // 5 seconds, in milliseconds
                .setFastestInterval(5 * 100); // .5 second, in milliseconds
    }


    /* Don't need updateTimer */
    private void updateTimer (float time) {
        secs = (long) (time / 1000);
        mins = (long) ((time / 1000) / 60);
        hrs = (long) (((time / 1000) / 60) / 60);

        /* Convert the seconds to String * and format to ensure it has * a leading zero when required */
        secs = secs % 60;
        seconds = String.valueOf(secs);
            if (secs == 0) {
                seconds = "00";
            }
            if (secs < 10 && secs > 0) {
                seconds = "0" + seconds;
            }

        /* Convert the minutes to String and format the String */
        mins = mins % 60;
        minutes = String.valueOf(mins);
            if (mins == 0) {
                minutes = "00";
            }
            if (mins < 10 && mins > 0) {
                minutes = "0" + minutes;
            }

        /* Convert the hours to String and format the String */
        hours = String.valueOf(hrs);
            if (hrs == 0) {
                hours = "00";
            }
            if (hrs < 10 && hrs > 0) {
                hours = "0" + hours;
            }

    }


    private void addListenerOnButton() {
        button1 = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);

        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // keep track of time when start is pressed
                startTime = System.currentTimeMillis();


                MarkerOptions startOptions = new MarkerOptions().position(getCurrentLatLng(mCurrentLocation))
                        .title("start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                MarkerOptions endOptions = new MarkerOptions().position(getCurrentLatLng(mCurrentLocation))
                        .title("end").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                if(startMarker==null && endMarker==null) {
                        startMarker = mMap.addMarker(startOptions);
                    }
                else if (startMarker!=null && endMarker==null){
                    endMarker =mMap.addMarker(endOptions);
                }
                else if (startMarker !=null && endMarker!=null){
                    endMarker.remove();
                    startMarker.remove();
                    startMarker=mMap.addMarker(startOptions);
                }
                else if( startMarker==null && endMarker!=null){
                    endMarker.remove();
                    startMarker=mMap.addMarker(startOptions);
                }
            }

        });

        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast popup to display stats. Should show elapsed time (System.currentTimeMillis()-startTime)
                double dist = 15.0; // dummy value for display purposes

        /* gives elapsed time since start was pressed in milliseconds*/
                long elapsedTime = System.currentTimeMillis()-startTime;

        /* convert milliseconds to hours, minutes and seconds */
                secs = (long) (elapsedTime / 1000);
                mins = (long) ((elapsedTime / 1000) / 60);
                hrs = (long) (((elapsedTime / 1000) / 60) / 60);

        /* Convert the seconds to String * and format to ensure it has * a leading zero when required */
                secs = secs % 60;
                seconds = String.valueOf(secs);
                if (secs == 0) {
                    seconds = "00";
                }
                if (secs < 10 && secs > 0) {
                    seconds = "0" + seconds;
                }

        /* Convert the minutes to String and format the String */
                mins = mins % 60;
                minutes = String.valueOf(mins);
                if (mins == 0) {
                    minutes = "00";
                }
                if (mins < 10 && mins > 0) {
                    minutes = "0" + minutes;
                }

        /* Convert the hours to String and format the String */
                hrs = hrs % 60;
                hours = String.valueOf(hrs);
                if (hrs == 0) {
                    hours = "00";
                }
                if (hrs < 10 && hrs > 0) {
                    hours = "0" + hours;
                }

         /* Toast popup that displays time and distance*/
                Toast.makeText(getApplicationContext(), "Distance (mi.): " +dist +"\nTime: " +hours + ":" + minutes + ":" + seconds, Toast.LENGTH_LONG).show();
                // do something when Stats clicked



            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.clear();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("This is the test marker"));
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,19));
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updatemap(mCurrentLocation);

        //String mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }
    private LatLng getCurrentLatLng(Location location){
        double lat = location.getLatitude();

        double lon = location.getLongitude();

        return new LatLng(lat,lon);
    }
    private void updatemap(Location location) {
        double currentLat = location.getLatitude();
        double currentLong = location.getLongitude();

        LatLng currLatLng= new LatLng(currentLat,currentLong);
       // MarkerOptions currOptions = new MarkerOptions()
         //       .position(currLatLng)
           //     .title("this is the Location Now!");
             //   mMap.addMarker(currOptions);
        mMarker.setPosition(getCurrentLatLng(mCurrentLocation));
        if(startMarker!=null && endMarker == null){
            PolylineOptions lineOptions = new PolylineOptions()
                    .add(getCurrentLatLng(mCurrentLocation), getCurrentLatLng(mPreviousLocation))
                    .width(20).color(Color.CYAN);
            //make a polyline from mpreviouslocation
            //to mcurrentlocation
            mMap.addPolyline(lineOptions);
        }
        mPreviousLocation=location;//test of getCurrentLatLng
        //this method works but gives issue for buttonclick, since marker isnt down yet
    }

    protected void startLocationUpdates(){
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void openSettings(View view) {
       Intent intent = new Intent(this,SettingsActivity.class);
        MapsActivity.this.startActivity(intent);
    }

}