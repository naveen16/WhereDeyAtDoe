package naveen16.wheredeyatdoe;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeScreenMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    private Marker claMarker;
    private Marker gregoryMarker;
    private Marker pclMarker;
    private Marker sacMarker;

    TileProvider mProvider;
    TileOverlay mOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMarkerClickListener(this);
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng cla = new LatLng(30.2849,-97.7355);
        claMarker = mMap.addMarker(new MarkerOptions().position(cla).title("College of Liberal Arts"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cla));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cla, 17));

        //adding a marker to gregory gym
        LatLng gregoryGym = new LatLng(30.2842,-97.7365);
        gregoryMarker = mMap.addMarker(new MarkerOptions().position(gregoryGym).title("Gregory Gym"));

        //adding a marker to pcl library
        LatLng pcl = new LatLng(30.2827, -97.7381);
        pclMarker = mMap.addMarker(new MarkerOptions().position(pcl).title("PCL"));

        //adding a marker to SAC
        LatLng sac = new LatLng(30.2849, -97.7360);
        sacMarker = mMap.addMarker(new MarkerOptions().position(sac).title("SAC"));

        addHeatMap();

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        String day = getDayoFWeek();

        Geocoder gc = new Geocoder(this);
        String name = "";
        try {
            List<Address> addresses = gc.getFromLocation(claMarker.getPosition().latitude, claMarker.getPosition().longitude, 1);
            for(int i = 0; i < addresses.size(); i++){
                Log.d("ADDRESS",addresses.get(i).getFeatureName());
            }
            Address building = addresses.get(0);
            name = building.getFeatureName();
            Log.d("NAME",name+" test");
            //Log.d("EXTRAS",building.getExtras().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (marker.equals(claMarker))
        {
            String hours = "";
            if(day.equals("Monday") || day.equals("Tuesday") || day.equals("Wednesday") || day.equals("Thursday") || day.equals("Friday"))
                hours = "6:00AM - 11:00PM";
            else
                hours = "8:00AM - 10:00PM";
            //handle click here
            Intent intent = new Intent(HomeScreenMapsActivity.this,BuildingDetailsActivity.class);
            intent.putExtra("NAME","College of Liberal Arts (CLA)");
            intent.putExtra("HOURS",hours);
            startActivity(intent);
        }
        else if(marker.equals(gregoryMarker)){
            String hours = "";
            if(day.equals("Monday") || day.equals("Tuesday") || day.equals("Wednesday") || day.equals("Thursday"))
                hours = "6:00AM - 1:00AM";
            else if(day.equals("Friday"))
                hours = "6:00AM - 10:00PM";
            else if(day.equals("Saturday"))
                hours = "8:00AM - 10:00PM";
            else
                hours = "10:00AM - 1:00AM";
            Intent intent = new Intent(HomeScreenMapsActivity.this,BuildingDetailsActivity.class);
            intent.putExtra("NAME","Gregory Gymnasium");
            intent.putExtra("HOURS",hours);
            startActivity(intent);
        }
        else if(marker.equals(pclMarker)){
            String hours = "";
            if(day.equals("Monday") || day.equals("Tuesday") || day.equals("Wednesday") || day.equals("Thursday"))
                hours = "Open 24 hours";
            else if(day.equals("Friday"))
                hours = "12:00AM - 11:00PM";
            else if(day.equals("Saturday"))
                hours = "10:00AM - 11:00PM";
            else
                hours = "11:00AM - 12:00AM";
            Intent intent = new Intent(HomeScreenMapsActivity.this,BuildingDetailsActivity.class);
            intent.putExtra("NAME","Perry Castaneda Library (PCL)");
            intent.putExtra("HOURS",hours);
            startActivity(intent);
        }
        else if(marker.equals(sacMarker)){
            String hours = "";
            if(day.equals("Tuesday") || day.equals("Wednesday") || day.equals("Thursday") || day.equals("Friday"))
                hours = "7:00AM - 5:00PM";
            else if(day.equals("Monday"))
                hours = "7:00AM - 12:00AM";
            else if(day.equals("Saturday"))
                hours = "10:00AM - 3:00AM";
            else
                hours = "12:00PM - 3:00AM";
            Intent intent = new Intent(HomeScreenMapsActivity.this,BuildingDetailsActivity.class);
            intent.putExtra("NAME","Student Activity Center (SAC)");
            intent.putExtra("HOURS",hours);
            startActivity(intent);
        }
        else{

        }
        return true;
    }

    private void addHeatMap() {
        List<LatLng> list = new ArrayList<LatLng>();
        list.add(new LatLng(30.2849,-97.7355)); //cla
        list.add(new LatLng(30.2842,-97.7365)); //greg
        list.add(new LatLng(30.2827, -97.7381)); //pcl
        list.add(new LatLng(30.2849, -97.7360)); //sac


        // Create a heat map tile provider, passing it the latlngs of the police stations.
        mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }


    public String getDayoFWeek(){
        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week abbreviated
        return simpleDateformat.format(now);
    }
}
