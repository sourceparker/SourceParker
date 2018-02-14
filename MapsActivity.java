package com.siat.diayan.sourceparker;

import android.graphics.Camera;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final GeoLocation INITIAL_CENTER = new GeoLocation(5.5968233, -0.2254799);
    private static final int INITIAL_ZOOM_LEVEL = 14;
    private static final String GEO_FIRE_DB = " https://proj-4c97c.firebaseio.com";
    private static final String GEO_FIRE_REF = GEO_FIRE_DB + "/Coordinates";

    private GoogleMap map;
    private Circle searchCircle;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private DatabaseReference mDatabase;

    private Map<String, Marker> markers;
    private static final String TAG = "MapsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



//        FirebaseOptions options = new FirebaseOptions.Builder().setApplicationId("proj-4c97c").setDatabaseUrl(GEO_FIRE_DB).build();
////        FirebaseApp app = FirebaseApp.initializeApp(this, options);

        // setup GeoFire
        mDatabase = FirebaseDatabase.getInstance().getReference("Coordinates");
        geoFire = new GeoFire(mDatabase);
        // radius in km
        geoQuery = geoFire.queryAtLocation(INITIAL_CENTER, 2000);
        // setup markers
        markers = new HashMap<>();


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        Log.e(TAG,"MAP READY");
        LatLng latLngCenter = new LatLng(INITIAL_CENTER.latitude, INITIAL_CENTER.longitude);
        searchCircle = this.map.addCircle(new CircleOptions().center(latLngCenter).radius(1000));
        searchCircle.setFillColor(Color.argb(66, 255, 0, 255));
        searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
        map.addMarker(new MarkerOptions().position(latLngCenter).title("my location"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCenter, INITIAL_ZOOM_LEVEL));

        //geoQuery = geoFire.queryAtLocation(new GeoLocation(latLngCenter.latitude, latLngCenter.longitude), 5000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                // Add a new marker to the map
                Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                markers.put(key, marker);
                map.getCameraPosition();
                Log.e(TAG, "onKeyEntered: cameraposition" + map.getCameraPosition());

                Log.e(TAG, "onKeyEntered: number of markers" + markers.size());
                Log.e(TAG, "onKeyEntered: keys" + key);            }

            @Override
            public void onKeyExited(String key) {
                Log.e(TAG,"IN");
                // Remove any old marker
                Marker marker = markers.get(key);
                if (marker != null) {
                    marker.remove();
                    markers.remove(key);
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                // Move the marker
                Log.e(TAG,"OUT");
                Marker marker = markers.get(key);
                if (marker != null) {
                    animateMarkerTo(marker, location.latitude, location.longitude);
                }
            }

            @Override
            public void onGeoQueryReady() {
                Log.e(TAG, "onGeoQueryReady: ");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Error")
                        .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
                        .setPositiveButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    // Animation handler for old APIs without animation support
    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed / DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));

                // if animation is not finished yet, repeat
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });


    }
}
