package com.uclan.mstocklmayr.map;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uclan.mstocklmayr.R;
import com.uclan.mstocklmayr.gallery.GalleryActivity;

import java.util.List;

/**
 * Created by mike on 12/6/14.
 */
public class MapActivity extends FragmentActivity {

    GoogleMap googleMap;
    List<MapLocation> mapLocations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        this.mapLocations = GalleryActivity.mapLocations;

        //to have a full screen with an action bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status!= ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else { // Google Play Services are available

            // Getting reference to the SupportMapFragment of activity_main.xml
            MapFragment fm = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();

            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);


            // Iterating through all the locations stored
            String lat = "";
            String lng = "";

            for(int i=0;i<this.mapLocations.size();i++){
                MapLocation mapLocation = this.mapLocations.get(i);

                // Drawing marker on the map
                drawMarker(new LatLng(mapLocation.getLatitude(), mapLocation.getLongitude()), mapLocation.getName());
            }

            // Moving CameraPosition to last clicked position
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(this.mapLocations.get(0).getLatitude(), this.mapLocations.get(0).getLongitude())));

            String zoom = "8";

            // Setting the zoom level in the map on last position  is clicked
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));

        }
    }

    private void drawMarker(LatLng point, String title){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);
        markerOptions.title(title);

        // Adding marker on the Google Map
        googleMap.addMarker(markerOptions);
    }
}
