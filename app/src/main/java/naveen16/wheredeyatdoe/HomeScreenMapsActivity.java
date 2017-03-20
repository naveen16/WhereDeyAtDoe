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

import java.io.IOException;
import java.util.List;

public class HomeScreenMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    private Marker myMarker;

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
        LatLng austin = new LatLng(30.2672,-97.7431);
        myMarker = mMap.addMarker(new MarkerOptions().position(austin).title("The UT Tower"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(austin));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(austin, 17));

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {


        Geocoder gc = new Geocoder(this);
        String name = "";
        try {
            List<Address> addresses = gc.getFromLocation(myMarker.getPosition().latitude, myMarker.getPosition().longitude, 1);
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

        if (marker.equals(myMarker))
        {
            //handle click here
            Intent intent = new Intent(HomeScreenMapsActivity.this,BuildingDetailsActivity.class);
            intent.putExtra("NAME",name);
            startActivity(intent);
        }
        return true;
    }
}
