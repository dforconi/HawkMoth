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
    //public final static String EXTRA_MESSAGE = "com.teamVictory.map.MESSAGE";
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public Location mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mPreviousLocation;
    private Marker mMarker;
    private Marker startMarker;
    private Marker endMarker;
    Button button1;
    Button button2;
    double totalDistance = 0;
    long startTime;
    long startingTime;
    long secs, mins, hrs;
    private String hours, minutes, seconds;
    String timeString;
    MarkerOptions startMarkerOptions = new MarkerOptions().position(getCurrentLatLng(mCurrentLocation))
            .title("start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
    MarkerOptions endMarkerOptions = new MarkerOptions().position(getCurrentLatLng(mCurrentLocation))
            .title("end").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

    public long getTimeInSeconds() {
        return System.currentTimeMillis() / 1000;
    }
    public void placeStartingMarker(){

    }
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
                .setInterval(2 * 1000)        // 5 seconds, in milliseconds
                .setFastestInterval(5 * 100); // .5 second, in milliseconds
    }

    private void addListenerOnButton() {
        button1 = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);

        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = System.currentTimeMillis();
                if (startMarker == null && endMarker == null) {
                    totalDistance = 0;
                    startMarker = mMap.addMarker(startMarkerOptions);
                    startingTime=getTimeInSeconds();
                }
                else if (startMarker != null && endMarker == null) {
                    endMarker = mMap.addMarker(endMarkerOptions);
                }
                else {
                    endMarker.remove();
                    startMarker.remove();
                    startMarker = mMap.addMarker(startMarkerOptions);
                }
            }

        });

        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                long elapsedTime = System.currentTimeMillis() - startTime;
                   /* convert milliseconds to hours, minutes and seconds */
                secs = elapsedTime / 1000;
                mins = (elapsedTime / 1000) / 60;
                hrs = ((elapsedTime / 1000) / 60) / 60;

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
                timeString = "Time: " + hours + ":" + minutes + ":" + seconds;
                openSettings(v);
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("This is the test marker"));
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        if (mMarker != null) {
            mMarker.setPosition(latLng);
        } else {
            mMarker = mMap.addMarker(options);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //mCurrentLocation = location;
        updateMap(location);
    }

    public LatLng getCurrentLatLng(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        return new LatLng(lat, lon);
    }

    private void updateMap(Location location) {
        mMarker.setPosition(getCurrentLatLng(mCurrentLocation));
        double mCurrentLat = mCurrentLocation.getLatitude();
        double mCurrentLng = mCurrentLocation.getLongitude();
        float[] answer = new float[1];
        if (startMarker != null && endMarker == null) {
            PolylineOptions lineOptions = new PolylineOptions()
                    .add(getCurrentLatLng(mCurrentLocation), getCurrentLatLng(mPreviousLocation))
                    .width(15).color(Color.CYAN);
            mMap.addPolyline(lineOptions);
        }
        mPreviousLocation = location;
        double mPrevLat = mPreviousLocation.getLatitude();
        double mPrevLng = mPreviousLocation.getLongitude();
        Location.distanceBetween(mPrevLat, mCurrentLat, mPrevLng, mCurrentLng, answer);
        if (startMarker != null) {
            totalDistance = totalDistance + answer[0];
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        Bundle extras = new Bundle();
        if (startMarker != null) {
            intent.putExtra("startPosition", startMarker.getPosition().toString());
        }
        if (endMarker != null) {
            intent.putExtra("endPosition", endMarker.getPosition().toString());
        }
        intent.putExtra("currentPosition", getCurrentLatLng(mCurrentLocation).toString());
        String totalDistanceString = String.valueOf(totalDistance);
        intent.putExtra("totalDistance", totalDistanceString);
        intent.putExtra("stringTime", timeString);
        MapsActivity.this.startActivity(intent);


    }


}
