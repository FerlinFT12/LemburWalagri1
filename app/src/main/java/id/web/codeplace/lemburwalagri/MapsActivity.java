package id.web.codeplace.lemburwalagri;
/**
 * update : Making a new MapsActivity class to implement google Maps API
 * implement : to showing google maps api
 * Update date : 30 agust 2018
 * name : yosua silitonga
 */


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;

// classes needed to initialize map

import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;

// classes needed to add location layer
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;




public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback ,LocationEngineListener, PermissionsListener{

     /*
       < --------------------------- All var global  ------------------->
     */
     private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;
    private ImageView mGps;
    private static final String TAG = "MapActivity";
    private FeatureCollection featureCollection;
    private RecyclerView locationsRecyclerView;
    private ArrayList<IndividualLocation> listOfIndividualLocations;
    private DirectionsRoute currentRoute;
    private BottomNavigationView Bnvg;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,getString(R.string.acces_token));
        setContentView(R.layout.activity_hospital_search);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        try {
            getFeatureCollectionFromJson();
        } catch (Exception exception) {
            Log.e("MapActivity", "onCreate: " + exception);
            Toast.makeText(this, "Failure to retrieve the navigation route!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onMapReady(final MapboxMap mapboxMap) {
        map = mapboxMap;
        enableLocationPlugin();

        listOfIndividualLocations = new ArrayList<>();
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeLocationEngine();
            }
        });
        Bnvg = (BottomNavigationView)findViewById(R.id.button_navigation);
        Bnvg.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem Item) {
                switch (Item.getItemId()) {
                    case R.id.action_hospital:
                        List<Feature> featureList = featureCollection.features();
                        for (int x = 0; x < featureList.size(); x++) {

                            Feature singleLocation = featureList.get(x);

                            // Get the single location's String properties to place in its map marker
                            String singleLocationName = singleLocation.getStringProperty("name");
                            String singleLocationDescription = singleLocation.getStringProperty("alamat");


                            // Get the single location's LatLng coordinates
                            Double stringLong = ((Point) singleLocation.geometry()).coordinates().get(0);
                            Double stringLat = ((Point) singleLocation.geometry()).coordinates().get(1);

                            // Create a new LatLng object with the Position object created above
                            LatLng singleLocationLatLng = new LatLng(stringLat, stringLong);

                            // Add the location to the Arraylist of locations for later use in the recyclerview
                            listOfIndividualLocations.add(new IndividualLocation(
                                    singleLocationName,
                                    singleLocationDescription,
                                    singleLocationLatLng
                            ));

                            // Add the location's marker to the map
                            mapboxMap.addMarker(new MarkerOptions()
                                    .position(singleLocationLatLng)
                                    .title(singleLocationName));


                        }
                        break;
                    case R.id.action_CallAmbulance:
                        Log.d(TAG, "onClick: This Buttun Is not ready yet");
                }
                return;
            }
        });


    }

    private void getFeatureCollectionFromJson() throws  IOException{
        try
        {
            //Use formJson() method to convert the geoJson file into a usable FeatureCollection object
            featureCollection = FeatureCollection.fromJson(loadGeoJsonFromAsset("list_of_locations.geojson"));
        }catch (Exception exception){
            Log.e("MapsActivity","getFeatureCollectionFromJson: " + exception);
        }

    }
    private String loadGeoJsonFromAsset(String filename) {
        try {
            // Load the GeoJSON file from the local asset folder
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (Exception exception) {
            Log.e("MapActivity", "Exception Loading GeoJSON: " + exception.toString());
            exception.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of LOST location engine
            initializeLocationEngine();
            initializeLocationLayer();

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);

        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initializeLocationLayer(){
        locationPlugin = new LocationLayerPlugin(mapView,map,locationEngine);
        locationPlugin.setLocationLayerEnabled(true);
        locationPlugin.setCameraMode(CameraMode.TRACKING);
        locationPlugin.setRenderMode(RenderMode.NORMAL);
    }

    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onConnected() {

            locationEngine.requestLocationUpdates();
        }


    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            originLocation = location;
            setCameraPosition(location);
            locationEngine.removeLocationEngineListener(this);
        }

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

        if (granted) {
            enableLocationPlugin();
        } else {
            finish();
        }

    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


}
