package com.example.fieldnotes.activities;

/*
DEVELOPER NOTES:

This class is dedicated to getting the latitude and longitude of the current location.

If you're having issues with this functionality, here are a few suggestions:
-Make sure your location services are on on your Android device.
-Make sure you're in a location that can actually get GPS signal. I developed this
 functionality in Ben Franklin Hall and would have to go outside to test it, since the GPS
 signals didn't reach my phone inside that building.

There is a lot of code in this class, but worry not, a lot of it is boiler plate, so it won't
really be discussed in detail.

The entirety of the methods buildApiClient, onStart, onResume, onConnected, and checkPlayServices
are all setting up Google's API. The code isn't too hard to follow.

startLocationUpdates starts looking for information from the GPS services. It checks every
0.1 seconds for information from the API on locational information. When onChangedLocation
is called, it returns calls returnGeolocation which packs the latitude and longitude into
an intent to be returned to the Stop Activity.

If any error occurs during setting up the API or getting the location, exitActivity is called
and the appropriate error message is displayed to the user in a Toast.

If your phone is going to get your location, it's going to get it in a few seconds. If your phone
does not have the location services on, or you're in a building that can't get GPS signals,
it doesn't matter how long you stay on this Activity, you will not get the coordinates. It's for
this reason we start a Timer in the onCreate method, that exits this Activity after 10 seconds.

If this Activity is Stopped, it finishes, just so the location services don't continue in the
background (it's bad for battery.)

We didn't want to use external libraries for this app, but we found it appropriate to use Google's,
since it's very unlikely Google will stop supporting its libraries, especially one that's used
so universally.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.fieldnotes.R;
import com.example.fieldnotes.utilities.PermissionsUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A mostly blank <code>Activity</code> that simply invokes Google's API for
 * location services. This API is used through Google Play Services, which have
 * come installed on Android phones since around 2011. The second the coordinates are
 * available, this <code>Activity</code> returns the <code>longitude</code> and
 * <code>latitude</code> through an <code>Intent</code>. This <code>Activity</code> can
 * also end if the API fails at any point. The app will not crash, it will simply return
 * to the <code>StopActivity</code> and display a <code>Toast</code> summarizing to the
 * user why the coordinates couldn't be retrieved.
 * <p>
 * NOTE: A time-out feature is also implemented, where if the coordinates
 * could not be determined given 10 seconds to search, this <code>Activity</code> will
 * give up and return to the <code>StopActivity</code> screen.
 */
public class GeolocationActivity extends AppCompatActivity implements
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GEOLOCATION";

    //Error codes if coordinates can't be obtained
    private static final int PERMISSIONS_NOT_GRANTED = 1;
    private static final int API_ERROR = 2;
    private static final int NEED_GOOGLE_PLAY_SERVICES = 3;
    private static final int PROCESS_INTERRUPTED = 4;
    private static final int TIMEOUT = 5;
    private static final int MILLISECONDS_TO_WAIT = 10000;
    Timer timer;
    private GoogleApiClient googleApiClient;
    private Location location;
    private LocationRequest locationRequest;
    private double longitude;
    private double latitude;

    /**
     * Beyond calling the parent method, this method checks for location permissions, then
     * calls <code>buildApiClient</code> to initialize the API. Also calls a method to
     * initialize a timer that checks if this activity is taking too long to retrieve
     * the coordinates.
     *
     * @param savedInstanceState The data stored by the app when it is minimized, brought out
     *                           of focus, etc. so the app can restore its data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocation);

        if (!PermissionsUtility.getPermissions(this, PermissionsUtility.LOCATION))
            exitActivity(PERMISSIONS_NOT_GRANTED);

        buildApiClient();
        startTimer();
    }

    /**
     * Initializes the API client, but does not yet connect to the API.
     */
    private void buildApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
    }

    /**
     * Starts a <code>Timer</code> to check if the geolocation is taking too long to work (if it
     * is working, it should take less than a second.) Reasons it may not work is if the user
     * has a poor GPS signal or location services are turned off. Returns the user to the
     * <code>StopActivity</code> page, with a <code>Toast</code> telling them the GPS timed out.
     */
    private void startTimer() {
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        exitActivity(TIMEOUT);
                    }
                });
            }
        };
        timer.schedule(task, MILLISECONDS_TO_WAIT);
    }

    /**
     * Activity needs to be created before connecting to the API. Exits this activity if it can't
     * connect to the API.
     */
    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        } else {
            exitActivity(API_ERROR);
        }
    }

    /**
     * Called after <code>onStart</code>, and ensures Play Services are available by calling
     * <code>checkPlayServices</code>.
     */
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * When this <code>Activity</code> is stopped (brought out of focus) it stops the whole
     * process of receiving the location. Ends the thread that's checking for a timeout.
     */
    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
        timer.purge();
        exitActivity(PROCESS_INTERRUPTED);
    }

    /**
     * Gets the last location from the Google API, but doesn't use it. This is because
     * the location needs to be initialized to start listening for the new locations.
     * It also calls <code>startLocationUpdates</code> to begin listening for updates in
     * the location.
     * <p>
     * The <code>Missing Permission</code> warning is suppressed, since there is no way to reach
     * this method without granting location permissions.
     *
     * @param bundle Contains information from calling <code>Activity</code>. Not used.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    /**
     * When the first new location is received, this method gets the <code>longitude</code>
     * and <code>latitude</code>, and passes them to <code>returnGeolocation</code> to
     * be returned to the <code>Stop Activity</code>.
     *
     * @param location The new <code>Location</code> passed by the listener.
     */
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            returnGeolocation();
        }
    }

    /**
     * Sets a few properties of the location listener, like the accuracy of the location and
     * how often to check for location updates, and then begins the listener.
     * <p>
     * The <code>Missing Permission</code> warning is suppressed, since there is no way to reach this
     * method without granting location permissions.
     */
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(100); //checks for changes in location every 0.1 seconds
        locationRequest.setFastestInterval(100);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    /**
     * Checks to see if Google Play Services are available. All Androids have had them for years,
     * but it doesn't hurt to check, since this functionality is dependent on them.
     * Closes this <code>Activity</code> if they are not found, with different error codes
     * depending on if the API is missing/corrupted or if the user had not enabled them.
     */
    private void checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                exitActivity(NEED_GOOGLE_PLAY_SERVICES);
            } else {
                exitActivity(API_ERROR);
            }
        }
    }

    /**
     * If this <code>Activity</code> successfully gets the coordinates, this method is called
     * to put them in an <code>Intent</code>, and end the <code>Activity</code>, returning
     * the the <code>Stops</code> screen.
     */
    private void returnGeolocation() {
        Intent intent = new Intent();
        intent.putExtra("LONGITUDE", longitude);
        intent.putExtra("LATITUDE", latitude);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * If this <code>Activity</code> fails to get the coordinates, this method returns the user
     * to the <code>Stop</code> screen with a <code>resultCode</code> of
     * <code>RESULT_CANCELED</code>. It also displays a <code>Toast</code> to the user
     * giving the information on what went wrong.
     *
     * @param errorCode The constant corresponding to the error.
     */
    private void exitActivity(int errorCode) {
        switch (errorCode) {
            case PERMISSIONS_NOT_GRANTED:
                Toast.makeText(this, "You need to grant this app location permissions to use the geolocation features.", Toast.LENGTH_LONG).show();
                break;
            case API_ERROR:
                Toast.makeText(this, "There was an error with the GPS services.", Toast.LENGTH_SHORT).show();
                break;
            case NEED_GOOGLE_PLAY_SERVICES:
                Toast.makeText(this, "Google Play Services not enabled.", Toast.LENGTH_SHORT).show();
                break;
            case PROCESS_INTERRUPTED:
                //No error message
                //App was minimized or closed
                break;
            case TIMEOUT:
                Toast.makeText(this, "The GPS took too long to find your coordinates", Toast.LENGTH_SHORT).show();
                break;
        }
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
