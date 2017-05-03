package naveen16.wheredeyatdoe;

import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static naveen16.wheredeyatdoe.R.id.map;

public class HomeScreenMapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {


    private GoogleMap mMap;

    HashMap<Marker, String[]> info_set = new HashMap<>();


    private DatabaseReference mDatabase;

    private Map<String, String> buildingsMap;
    private Map<String, LatLng> buildingsLatLngs;
    private Map<String, String> buildingsHistoryMap;

    private Map<Marker, Event> eventMap;

    private List<String> requested;


    List<Report> reportList;
    List<Report> reportList2;
    List<Report> historyRList;

    TileProvider mProvider;
    TileOverlay mOverlay;

    String lastBuilding = "default";

    double[] loc = new double[2];

    protected ArrayList<Geofence> mGeofenceList;
    protected GoogleApiClient mGoogleApiClient;
    private Button mAddGeofencesButton;


    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_maps);

        loadMap();
        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();
    }

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.LANDMARKS.entrySet()) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
            Log.d("CONNECT", "IN IF FOR GOOGLE CLIENT CONNECT");
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
            Log.d("ONSTOP", "IN IF FOR GOOGLE CLIENT NOT CONNECT");
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("ONCONNECTED", "IN ON CONNECTED");
        addGeofencesHandler();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do something with result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    public void onResult(Status status) {
        Log.d("INONRESULT","IN ON RESULT");
        if (status.isSuccess()) {
            Log.d("INONRESULT","IN ON RESULT IF STATEMENT");
            Toast.makeText(
                    this,
                    "Geofences Added",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Log.d("INONRESULT","IN ON RESULT ELSE");
            // Get the status code for the error and log it using a user-friendly message.
            //String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    //status.getStatusCode());
        }
    }
    public void addGeofencesHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google API Client not connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Log.d("INADDGEOFENCE","IN ADD GEO FENCE HANDLER");
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER|GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        for(int i =0; i < mGeofenceList.size(); i++){
            Log.d("MGEOFENCELIST",mGeofenceList.get(i)+" latlng");
        }
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Log.d("INPENDINGINTENT","IN GET GEOFENCE PENDING INTENT");
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void loadMap() {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        buildingsMap = new HashMap<String, String>();
        buildingsLatLngs = new HashMap<String, LatLng>();
        reportList = new ArrayList<Report>();
        reportList2 = new ArrayList<Report>();
        historyRList = new ArrayList<Report>();
        buildingsHistoryMap = new HashMap<String, String>();
        eventMap = new HashMap<Marker, Event>();
        requested = new ArrayList<String>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        switch (item.getItemId()) {
            case R.id.instructions:
                Intent intent = new Intent(HomeScreenMapsActivity.this,
                        InstructionsActivity.class);
                startActivity(intent);
                return true;
            case R.id.refresh:
                loadMap();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("INSIDEONREQUEST", "INSIDE ON REQUEST");
                    //loc = getLocation();
                    loc[0] = 37.785281;
                    loc[1] = -122.4296384;
                    setUpUserMarker();
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    public void setUpUserMarker() {
        Log.d("USERSLOCATION", "" + loc[0] + " " + loc[1]);
        //adding a marker for users location
        LatLng user = new LatLng(loc[0], loc[1]);
        Marker userMarker = mMap.addMarker(new MarkerOptions().position(user).title("User Marker"));
        userMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        Log.d("USERMARKER", "USER MARKER TITLE: " + userMarker.getTitle());
    }


    public void addMarkers(List<String> requested){
        //adding a marker to cla
        LatLng cla = new LatLng(30.2849, -97.7355);
        buildingsLatLngs.put(getResources().getString(R.string.cla), cla);
        Marker claMarker = mMap.addMarker(new MarkerOptions().position(cla).title(getResources().getString(R.string.cla)));
        if(requested.contains(getResources().getString(R.string.cla)))
            claMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        info_set.put(claMarker, new String[]{getResources().getString(R.string.cla), getResources().getString(R.string.claHours)});

        //adding a marker to gregory gym
        LatLng gregoryGym = new LatLng(30.2842, -97.7365);
        buildingsLatLngs.put(getResources().getString(R.string.greg), gregoryGym);
        Marker gregoryMarker = mMap.addMarker(new MarkerOptions().position(gregoryGym).title(getResources().getString(R.string.cla)));
        if(requested.contains(getResources().getString(R.string.greg)))
            gregoryMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        info_set.put(gregoryMarker, new String[]{getResources().getString(R.string.greg), getResources().getString(R.string.gregHours)});

        //adding a marker to pcl library
        LatLng pcl = new LatLng(30.2827, -97.7381);
        buildingsLatLngs.put(getResources().getString(R.string.pcl), pcl);
        Marker pclMarker = mMap.addMarker(new MarkerOptions().position(pcl).title(getResources().getString(R.string.pcl)));
        if(requested.contains(getResources().getString(R.string.pcl)))
            pclMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        info_set.put(pclMarker, new String[]{getResources().getString(R.string.pcl), getResources().getString(R.string.pclHours)});

        //adding a marker to SAC
        LatLng sac = new LatLng(30.2849, -97.7360);
        buildingsLatLngs.put(getResources().getString(R.string.sac), sac);
        Marker sacMarker = mMap.addMarker(new MarkerOptions().position(sac).title(getResources().getString(R.string.sac)));
        if(requested.contains(getResources().getString(R.string.sac)))
            sacMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        info_set.put(sacMarker, new String[]{getResources().getString(R.string.sac), getResources().getString(R.string.sacHours)});

        //adding a marker to GDC
        LatLng gdc = new LatLng(30.28628, -97.73662);
        buildingsLatLngs.put(getResources().getString(R.string.gdc), gdc);
        Marker gdcMarker = mMap.addMarker(new MarkerOptions().position(gdc).title(getResources().getString(R.string.gdc)));
        if(requested.contains(getResources().getString(R.string.gdc)))
            gdcMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        info_set.put(gdcMarker, new String[]{getResources().getString(R.string.gdc), getResources().getString(R.string.gdcHours)});

        //adding a marker to UT Tower
        LatLng mai = new LatLng(30.286096, -97.73938);
        buildingsLatLngs.put(getResources().getString(R.string.mai), mai);
        Marker maiMarker = mMap.addMarker(new MarkerOptions().position(mai).title(getResources().getString(R.string.mai)));
        if(requested.contains(getResources().getString(R.string.mai)))
            maiMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        info_set.put(maiMarker, new String[]{getResources().getString(R.string.mai), getResources().getString(R.string.maiHours)});

        //adding a marker to Jackson Geological Sciences Building
        LatLng jgb = new LatLng(30.285821, -97.735745);
        buildingsLatLngs.put(getResources().getString(R.string.jgb), jgb);
        Marker jgbMarker = mMap.addMarker(new MarkerOptions().position(jgb).title(getResources().getString(R.string.jgb)));
        if(requested.contains(getResources().getString(R.string.jgb)))
            jgbMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        info_set.put(jgbMarker, new String[]{getResources().getString(R.string.jgb), getResources().getString(R.string.jgbHours)});

        //adding a marker to Robert A. Welch Hall
        LatLng wel = new LatLng(30.286696, -97.737692);
        buildingsLatLngs.put(getResources().getString(R.string.wel), wel);
        Marker welMarker = mMap.addMarker(new MarkerOptions().position(wel).title(getResources().getString(R.string.wel)));
        if(requested.contains(getResources().getString(R.string.wel)))
            welMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(wel));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wel, 17));
        info_set.put(welMarker, new String[]{getResources().getString(R.string.wel), getResources().getString(R.string.welHours)});

        //adding a marker to Flawn Academic Center
        LatLng fac = new LatLng(30.286281, -97.740313);
        buildingsLatLngs.put(getResources().getString(R.string.fac), fac);
        Marker facMarker = mMap.addMarker(new MarkerOptions().position(fac).title(getResources().getString(R.string.fac)));
        if(requested.contains(getResources().getString(R.string.fac)))
            facMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        info_set.put(facMarker, new String[]{getResources().getString(R.string.fac), getResources().getString(R.string.facHours)});

        //adding a marker to Jack S. Blanton Museum of Art
        LatLng bma = new LatLng(30.281014, -97.737473);
        buildingsLatLngs.put(getResources().getString(R.string.bma), bma);
        Marker bmaMarker = mMap.addMarker(new MarkerOptions().position(bma).title(getResources().getString(R.string.bma)));
        info_set.put(bmaMarker, new String[]{getResources().getString(R.string.bma), getResources().getString(R.string.bmaHours)});

        //adding a marker to Harry Ransom Center
        LatLng hrc = new LatLng(30.281014, -97.737473);
        buildingsLatLngs.put(getResources().getString(R.string.hrc), hrc);
        Marker hrcMarker = mMap.addMarker(new MarkerOptions().position(hrc).title(getResources().getString(R.string.hrc)));
        if(requested.contains(getResources().getString(R.string.hrc)))
            hrcMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        info_set.put(hrcMarker, new String[]{getResources().getString(R.string.hrc), getResources().getString(R.string.hrcHours)});

        //adding a marker to Jester City Limits
        LatLng jcl = new LatLng(30.282806, -97.736771);
        buildingsLatLngs.put(getResources().getString(R.string.jcl), jcl);
        Marker jclMarker = mMap.addMarker(new MarkerOptions().position(jcl).title(getResources().getString(R.string.jcl)));
        if(requested.contains(getResources().getString(R.string.jcl)))
            jclMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        info_set.put(jclMarker, new String[]{getResources().getString(R.string.jcl), getResources().getString(R.string.jclHours)});

        //adding a marker to South Mall
        LatLng sou = new LatLng(30.284373, -97.739572);
        buildingsLatLngs.put(getResources().getString(R.string.sou), sou);
        Marker souMarker = mMap.addMarker(new MarkerOptions().position(sou).title(getResources().getString(R.string.sou)));
        if(requested.contains(getResources().getString(R.string.sou)))
            souMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        info_set.put(souMarker, new String[]{getResources().getString(R.string.sou), getResources().getString(R.string.souHours)});

        //adding a marker to Waggener Hall
        LatLng wag = new LatLng(30.284995, -97.737630);
        buildingsLatLngs.put(getResources().getString(R.string.wag), wag);
        Marker wagMarker = mMap.addMarker(new MarkerOptions().position(wag).title(getResources().getString(R.string.wag)));
        if(requested.contains(getResources().getString(R.string.wag)))
            wagMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        info_set.put(wagMarker, new String[]{getResources().getString(R.string.wag), getResources().getString(R.string.wagHours)});

        if (!lastBuilding.equals("default")) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(buildingsLatLngs.get(lastBuilding)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(buildingsLatLngs.get(lastBuilding), 17));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(wel));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wel, 17));
        }
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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        } else {
            //loc = getLocation();
            loc[0] = 37.785281;
            loc[1] = -122.4296384;
            Log.d("INELSEMARKER", "IN ELSE OF MARKER");
            setUpUserMarker();
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                Marker eventMarker = mMap.addMarker(new MarkerOptions().position(point).title("Custom location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                Intent intent = new Intent(HomeScreenMapsActivity.this, AddEventActivity.class);
                Log.d("LAT",point.latitude+"");
                intent.putExtra("Latitude",point.latitude);
                intent.putExtra("Longitude",point.longitude);
                startActivity(intent);
                //event_info_set.put(eventMarker,);
            }
        });

        Log.d("REQUESTEDABOVE",requested.toString());
        mDatabase.child("request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d("REQ",child.getValue().toString());
                    requested.add(child.getValue().toString());
                }
                Log.d("REQUESTED",requested.toString());
                addMarkers(requested);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

        if(requested.size() == 0)
            addMarkers(requested);


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("GREENYELLOW", "Reached Green yellow color method");
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    reportList = new ArrayList<Report>();
                    historyRList = new ArrayList<Report>();
                    Log.d("OUTERLOOPKEY", child.getKey());
                    Log.d("OUTERLOOPVAL", child.getValue().toString());

                    if (!child.getKey().equals("history") && !child.getKey().equals("events") && !child.getKey().equals("request")) {
                        String level = "";
                        for (DataSnapshot child2 : child.getChildren()) {
                            Log.d("INNERLOOPKEY", child2.getKey());
                            Log.d("INNERLOOPVAL", child2.getValue().toString());

                            if (!child2.getKey().equals("total_value")) {
                                Report rep = child2.getValue(Report.class);
                                Date currDate = new Date();
                                Date repDate = rep.getTimeOfEntry();
                                if (currDate.getHours() - repDate.getHours() > 1 || currDate.getDay() != repDate.getDay()) {
                                    Log.d("IFHOURS","IN IF HOURS DELETE");
                                    child2.getRef().removeValue();
                                } else
                                    reportList.add(rep);

                            }

                        }
                        int total = 0;
                        for (int i = 0; i < reportList.size(); i++) {
                            Report R = reportList.get(i);
                            total += getNumFromLvl(R.getLevel());
                        }
                        Log.d("RLIST", "name:" + child.getKey() + reportList.toString());
                        if (reportList.size() == 0) {
                            Log.d("REPORTSIZE","IN reportlist size zero DELETE");
                            child.getRef().removeValue();
                        } else {
                            int finalavg = (total) / (reportList.size());
                            mDatabase.child(child.getKey()).child("total_value").setValue(getLvlFromNum(finalavg));
                            buildingsMap.put(child.getKey(), getLvlFromNum(finalavg));
                        }
                    }
                    else if(child.getKey().equals("events")){
                        //process
                        for (DataSnapshot child2 : child.getChildren()) {
                            Event event = child2.getValue(Event.class);
                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            String now = dateFormat.format(new Date());
                            Date eDate = null;
                            try {
                                eDate = dateFormat.parse(event.getDate());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(eDate.compareTo(dateFormat.parse(now)) < 0){
                                    Log.d("DATECOMPARE",eDate.compareTo(new Date())+"'");
                                    Log.d("DATECOMPARE1",eDate+"");
                                    Log.d("DATECOMPARE2",new Date()+"");
                                    child2.getRef().removeValue();
                                }
                                else if(eDate.compareTo(dateFormat.parse(now)) == 0){
                                    LatLng point = new LatLng(event.getLatitude(), event.getLongitude());
                                    Marker eventMarker = mMap.addMarker(new MarkerOptions().position(point).title("Event location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                    eventMap.put(eventMarker, event);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //read history
                    else if(child.getKey().equals("history")){
                        Log.d("CKEY", child.getKey());
                        for (DataSnapshot child2 : child.getChildren()) {
                            Log.d("OUTERLOOPKEYHISTORY", child2.getKey());
                            Log.d("OUTERLOOPVALHISTORY", child2.getValue().toString());
                            for (DataSnapshot child3 : child2.getChildren()) {
                                Log.d("INNERLOOPKEYHISTORY", child3.getKey());
                                Log.d("INNERLOOPVALHISTORY", child3.getValue().toString());
                                if (!child3.getKey().equals("total_value")) {
                                    Report rep = child3.getValue(Report.class);
                                    Log.d("REPHISTORY", rep.toString());
                                    Date currDate = new Date();
                                    Date repDate = rep.getTimeOfEntry();
                                    if (currDate.getHours() - repDate.getHours() <= 1 && currDate.getDay() == repDate.getDay() && currDate.getDate() != repDate.getDate()) {
                                        historyRList.add(rep);
                                    }
                                }
                            }
                            int total = 0;
                            for (int i = 0; i < historyRList.size(); i++) {
                                Report R = historyRList.get(i);
                                total += getNumFromLvl(R.getLevel());
                            }
                            int finalavg = 0;
                            if (historyRList.size() != 0)
                                finalavg = (total) / (historyRList.size());
                            buildingsHistoryMap.put(child2.getKey(), getLvlFromNum(finalavg));
                        }
                    }

                }
                addHeatMap();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });


    }

    public int getNumFromLvl(String selectedLvl) {
        if (selectedLvl.equals("No prior data")) {
            return 0;
        } else if (selectedLvl.equals("Not Crowded")) {
            return 1;
        } else if (selectedLvl.equals("Slightly Crowded")) {
            return 2;
        } else if (selectedLvl.equals("Crowded")) {
            return 3;
        } else if (selectedLvl.equals("Very Crowded")) {
            return 4;
        } else {
            return 5;
        }
    }

    public String getLvlFromNum(int newAvg) {
        if (newAvg == 0) {
            return "No prior data";
        } else if (newAvg == 1) {
            return "Not Crowded";
        } else if (newAvg == 2) {
            return "Slightly Crowded";
        } else if (newAvg == 3) {
            return "Crowded";
        } else if (newAvg == 4) {
            return "Very Crowded";
        } else {
            return "As Crowded as it Gets";
        }

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        final String day = getDayoFWeek();

        if (info_set.containsKey(marker)) { //This might be an unnecessary check, as we can assume existing markers ar ours
            //we want to say something akin to info = get_info(), info[0] = name, info[1] = hours
            String[] info = info_set.get(marker);
            final String name = info[0];
            final String hours = parseHours(info[1]);

            String[] options = {"View Details", "Report", "Request Report", "Cancel"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setTitle("Select an option")
            builder.setTitle(name)
                    .setItems(options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Intent intent = new Intent(HomeScreenMapsActivity.this, BuildingDetailsActivity.class);
                                intent.putExtra("NAME", name);
                                intent.putExtra("HOURS", hours);
                                if (buildingsMap.get(name) == null)
                                    intent.putExtra("POPULARITY", "No Current Data");
                                else
                                    intent.putExtra("POPULARITY", buildingsMap.get(name));
                                if (buildingsHistoryMap.get(name) == null)
                                    intent.putExtra("HISTORY", "No Prior Data");
                                else
                                    intent.putExtra("HISTORY", buildingsHistoryMap.get(name));
                                intent.putExtra("IMAGE", R.drawable.cla);
                                startActivity(intent);
                            }
                            if (which == 1) {
//                                Intent intent = new Intent(HomeScreenMapsActivity.this,ReportActivity.class);
//                                intent.putExtra("NAME",name);
//                                startActivity(intent);
                                String[] options = {"1", "2", "3", "4", "5"};
                                final AlertDialog.Builder builder2 = new AlertDialog.Builder(builder.getContext());
                                builder.setTitle("Crowd level on a scale of 1-5")
                                        .setItems(options, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (which == 0) {
                                                    final String selectedLvl = getLvlFromNum(which + 1);
                                                    mDatabase.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Log.d("GREENYELLOW", "Reached Green yellow color method");
                                                            Log.d("DATACHILD", dataSnapshot.getChildrenCount() + "");

                                                            Log.d("DATAKEY", dataSnapshot.getKey());

                                                            Log.d("DATANAME", name);

                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                if (!child.getKey().equals("total_value")) {
                                                                    Report rep = child.getValue(Report.class);
                                                                    reportList2.add(rep);
                                                                }


                                                            }
                                                            int total = 0;
                                                            for (int i = 0; i < reportList2.size(); i++) {
                                                                Report R = reportList2.get(i);
                                                                total += getNumFromLvl(R.getLevel());
                                                            }
                                                            Log.d("REPORTLIST", reportList2.toString());
                                                            Report newR = new Report(selectedLvl, new Date());
                                                            mDatabase.child(name).push().setValue(newR);
                                                            mDatabase.child("history").child(name).push().setValue(newR);
//                                                            Intent intent2=new Intent(ReportActivity.this,HomeScreenMapsActivity.class);
//                                                            intent2.putExtra("ReportBuilding",name);
//                                                            startActivity(intent2);
//                                                            finish();
                                                            lastBuilding = name;
//                                                            mDatabase.child("request").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                @Override
//                                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
//                                                                       if(child.getValue().equals(name))
//                                                                           child.getRef().removeValue();
//                                                                    }
//                                                                }
//                                                                @Override
//                                                                public void onCancelled(DatabaseError databaseError) {
//                                                                    // Getting Post failed, log a message
//                                                                    Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
//                                                                    // ...
//                                                                }
//                                                            });
                                                            loadMap();


                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Getting Post failed, log a message
                                                            Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
                                                            // ...
                                                        }
                                                    });
                                                    mDatabase.child("request").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                if(child.getValue().equals(name))
                                                                    child.getRef().removeValue();
                                                            }
                                                            loadMap();
                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Getting Post failed, log a message
                                                            Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
                                                            // ...
                                                        }
                                                    });

                                                }
                                                if (which == 1) {
                                                    final String selectedLvl = getLvlFromNum(which + 1);
                                                    mDatabase.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Log.d("GREENYELLOW", "Reached Green yellow color method");
                                                            Log.d("DATACHILD", dataSnapshot.getChildrenCount() + "");

                                                            Log.d("DATAKEY", dataSnapshot.getKey());

                                                            Log.d("DATANAME", name);

                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                if (!child.getKey().equals("total_value")) {
                                                                    Report rep = child.getValue(Report.class);
                                                                    reportList2.add(rep);
                                                                }


                                                            }
                                                            int total = 0;
                                                            for (int i = 0; i < reportList2.size(); i++) {
                                                                Report R = reportList2.get(i);
                                                                total += getNumFromLvl(R.getLevel());
                                                            }
                                                            Log.d("REPORTLIST", reportList2.toString());
                                                            Report newR = new Report(selectedLvl, new Date());
                                                            mDatabase.child(name).push().setValue(newR);
                                                            mDatabase.child("history").child(name).push().setValue(newR);
//                                                            Intent intent2=new Intent(ReportActivity.this,HomeScreenMapsActivity.class);
//                                                            intent2.putExtra("ReportBuilding",name);
//                                                            startActivity(intent2);
//                                                            finish();
                                                            lastBuilding = name;
                                                            loadMap();


                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Getting Post failed, log a message
                                                            Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
                                                            // ...
                                                        }
                                                    });
                                                    mDatabase.child("request").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                if(child.getValue().equals(name))
                                                                    child.getRef().removeValue();
                                                            }
                                                            loadMap();
                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Getting Post failed, log a message
                                                            Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
                                                            // ...
                                                        }
                                                    });
                                                }
                                                if (which == 2) {
                                                    final String selectedLvl = getLvlFromNum(which + 1);
                                                    mDatabase.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Log.d("GREENYELLOW", "Reached Green yellow color method");
                                                            Log.d("DATACHILD", dataSnapshot.getChildrenCount() + "");

                                                            Log.d("DATAKEY", dataSnapshot.getKey());

                                                            Log.d("DATANAME", name);

                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                if (!child.getKey().equals("total_value")) {
                                                                    Report rep = child.getValue(Report.class);
                                                                    reportList2.add(rep);
                                                                }


                                                            }
                                                            int total = 0;
                                                            for (int i = 0; i < reportList2.size(); i++) {
                                                                Report R = reportList2.get(i);
                                                                total += getNumFromLvl(R.getLevel());
                                                            }
                                                            Log.d("REPORTLIST", reportList2.toString());
                                                            Report newR = new Report(selectedLvl, new Date());
                                                            mDatabase.child(name).push().setValue(newR);
                                                            mDatabase.child("history").child(name).push().setValue(newR);
//                                                            Intent intent2=new Intent(ReportActivity.this,HomeScreenMapsActivity.class);
//                                                            intent2.putExtra("ReportBuilding",name);
//                                                            startActivity(intent2);
//                                                            finish();
                                                            lastBuilding = name;
                                                            loadMap();


                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Getting Post failed, log a message
                                                            Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
                                                            // ...
                                                        }
                                                    });
                                                    mDatabase.child("request").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                if(child.getValue().equals(name))
                                                                    child.getRef().removeValue();
                                                            }
                                                            loadMap();
                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Getting Post failed, log a message
                                                            Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
                                                            // ...
                                                        }
                                                    });
                                                }
                                                if (which == 3) {
                                                    final String selectedLvl = getLvlFromNum(which + 1);
                                                    mDatabase.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Log.d("GREENYELLOW", "Reached Green yellow color method");
                                                            Log.d("DATACHILD", dataSnapshot.getChildrenCount() + "");

                                                            Log.d("DATAKEY", dataSnapshot.getKey());

                                                            Log.d("DATANAME", name);

                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                if (!child.getKey().equals("total_value")) {
                                                                    Report rep = child.getValue(Report.class);
                                                                    reportList2.add(rep);
                                                                }


                                                            }
                                                            int total = 0;
                                                            for (int i = 0; i < reportList2.size(); i++) {
                                                                Report R = reportList2.get(i);
                                                                total += getNumFromLvl(R.getLevel());
                                                            }
                                                            Log.d("REPORTLIST", reportList2.toString());
                                                            Report newR = new Report(selectedLvl, new Date());
                                                            mDatabase.child(name).push().setValue(newR);
                                                            mDatabase.child("history").child(name).push().setValue(newR);
//                                                            Intent intent2=new Intent(ReportActivity.this,HomeScreenMapsActivity.class);
//                                                            intent2.putExtra("ReportBuilding",name);
//                                                            startActivity(intent2);
//                                                            finish();
                                                            lastBuilding = name;
                                                            loadMap();


                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Getting Post failed, log a message
                                                            Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
                                                            // ...
                                                        }
                                                    });
                                                    mDatabase.child("request").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                if(child.getValue().equals(name))
                                                                    child.getRef().removeValue();
                                                            }
                                                            loadMap();
                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Getting Post failed, log a message
                                                            Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
                                                            // ...
                                                        }
                                                    });
                                                }
                                                if (which == 4) {
                                                    final String selectedLvl = getLvlFromNum(which + 1);
                                                    mDatabase.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Log.d("GREENYELLOW", "Reached Green yellow color method");
                                                            Log.d("DATACHILD", dataSnapshot.getChildrenCount() + "");

                                                            Log.d("DATAKEY", dataSnapshot.getKey());

                                                            Log.d("DATANAME", name);

                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                if (!child.getKey().equals("total_value")) {
                                                                    Report rep = child.getValue(Report.class);
                                                                    reportList2.add(rep);
                                                                }


                                                            }
                                                            int total = 0;
                                                            for (int i = 0; i < reportList2.size(); i++) {
                                                                Report R = reportList2.get(i);
                                                                total += getNumFromLvl(R.getLevel());
                                                            }
                                                            Log.d("REPORTLIST", reportList2.toString());
                                                            Report newR = new Report(selectedLvl, new Date());
                                                            mDatabase.child(name).push().setValue(newR);
                                                            mDatabase.child("history").child(name).push().setValue(newR);
//                                                            Intent intent2=new Intent(ReportActivity.this,HomeScreenMapsActivity.class);
//                                                            intent2.putExtra("ReportBuilding",name);
//                                                            startActivity(intent2);
//                                                            finish();
                                                            lastBuilding = name;
                                                            loadMap();


                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Getting Post failed, log a message
                                                            Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
                                                            // ...
                                                        }
                                                    });
                                                    mDatabase.child("request").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                if(child.getValue().equals(name))
                                                                    child.getRef().removeValue();
                                                            }
                                                            loadMap();
                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Getting Post failed, log a message
                                                            Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
                                                            // ...
                                                        }
                                                    });
                                                }

                                            }
                                        });
                                builder.create().show();
                            }
                            if(which == 2){
                                //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                mDatabase.child("request").push().setValue(name);
                                loadMap();
                            }

                        }
                    });
            builder.create().show();
            //handle click here
        }
        else{
            Event event = eventMap.get(marker);
            Intent intent = new Intent(HomeScreenMapsActivity.this, EventDetailsActivity.class);
            intent.putExtra("DATE", event.date);
            intent.putExtra("DESCRIPTION", event.description);
            intent.putExtra("LOCATION", event.location);
            intent.putExtra("NAME", event.name);
            intent.putExtra("START_TIME", event.startTime);
            intent.putExtra("END_TIME", event.endTime);
            intent.putExtra("PRICE", event.entryFee);
            startActivity(intent);
        }
        return true;

    }


    private void addHeatMap() {
        int[] colors = {
                Color.rgb(0, 255, 0), // green
                Color.rgb(255, 255, 0),   // yellow
                Color.rgb(255, 0, 0)  //red
        };
        for (String s : buildingsMap.keySet()) {
            String value = buildingsMap.get(s);

            float l1 = .1f;
            float l2 = .2f;
            float l3 = .3f;
            if (value.equals("Not Crowded")) {
                l1 = .1f;
                l2 = 2f;
                l3 = 3f;
            } else if (value.equals("Slightly Crowded")) {
                l1 = .1f;
                l2 = .5f;
                l3 = 3f;
            } else if (value.equals("Crowded")) {
                l1 = .1f;
                l2 = .2f;
                l3 = 3f;
            } else if (value.equals("Very Crowded")) {
                l1 = .1f;
                l2 = .2f;
                l3 = 1f;
            } else {
                l1 = .1f;
                l2 = .2f;
                l3 = .3f;
            }

            float[] startPoints = {
                    l1, l2, l3
            };

            List<LatLng> list = new ArrayList<LatLng>();
            list.add(buildingsLatLngs.get(s)); //cla
            Log.d("SVALUE", s);
            Log.d("BUILDING", "message: " + buildingsLatLngs.toString());
            Log.d("LATLONG", list.toString());
            // Create a heat map tile provider, passing it the latlngs of the police stations.

            mProvider = new HeatmapTileProvider.Builder()
                    .data(list)
                    .radius(50)
                    .gradient(new Gradient(colors, startPoints))
                    .build();
            // Add a tile overlay to the map, using the heat map tile provider.
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }
    }


    public String getDayoFWeek() {
        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week abbreviated
        return simpleDateformat.format(now);
    }

    public String parseHours(String format) {
        /*
            expects format: 28 int characters, 4 per day, 2 per time_start/time_close
         */
        char[] data = format.toCharArray();
        String day = getDayoFWeek();
        int d;
        switch (day) {
            case "Monday":
                d = 0;
                break;
            case "Tuesday":
                d = 1;
                break;
            case "Wednesday":
                d = 2;
                break;
            case "Thursday":
                d = 3;
                break;
            case "Friday":
                d = 4;
                break;
            case "Saturday":
                d = 5;
                break;
            case "Sunday":
                d = 6;
                break;
            default:
                d = -1;
                break;
        }
        if (data[d * 4] == 'c')
            return "Closed";
        int b = Integer.parseInt("" + data[d * 4] + data[d * 4 + 1]);
        if (b == 24)
            return "24 Hours";
        int e = Integer.parseInt("" + data[d * 4 + 2] + data[d * 4 + 3]);

        String TS1 = ((b / 12) == 1) ? "PM" : "AM";
        String TS2 = ((e / 12) == 1) ? "PM" : "AM";

        if (e == 24)
            return b % 12 + ":00" + TS1 + " to midnight";
        if (b == 00)
            return "open until " + e % 12 + ":00" + TS2;

        return b % 12 + ":00" + TS1 + " - " + e % 12 + ":00" + TS2;
    }

    public double[] getLocation() {
        GPSTracker gps = new GPSTracker(this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        Log.d("PhoneLocation", "" + latitude + " " + longitude);
        return new double[]{latitude, longitude};
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }


}
