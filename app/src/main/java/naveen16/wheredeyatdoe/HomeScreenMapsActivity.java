package naveen16.wheredeyatdoe;

import android.app.FragmentManager;
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
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeScreenMapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    HashMap<Marker, String[]> info_set = new HashMap<>();



    private DatabaseReference mDatabase;

    private Map<String,String> buildingsMap;
    private Map<String,LatLng> buildingsLatLngs;
    private Map<String,String> buildingsHistoryMap;


    List<Report> reportList;
    List<Report> reportList2;
    List<Report> historyRList;

    TileProvider mProvider;
    TileOverlay mOverlay;

    String lastBuilding="default";

    double[] loc = new double[2];

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_maps);
        loadMap();
//        setContentView(R.layout.activity_home_screen_maps);
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        buildingsMap=new HashMap<String, String>();
//        buildingsLatLngs=new HashMap<String, LatLng>();
//        reportList=new ArrayList<Report>();
//        reportList2=new ArrayList<Report>();
//        historyRList=new ArrayList<Report>();
//        buildingsHistoryMap=new HashMap<String, String>();
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);



    }

    private void loadMap(){

        mDatabase = FirebaseDatabase.getInstance().getReference();
        buildingsMap=new HashMap<String, String>();
        buildingsLatLngs=new HashMap<String, LatLng>();
        reportList=new ArrayList<Report>();
        reportList2=new ArrayList<Report>();
        historyRList=new ArrayList<Report>();
        buildingsHistoryMap=new HashMap<String, String>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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
                Intent intent=new Intent(HomeScreenMapsActivity.this,
                        InstructionsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("INSIDEONREQUEST","INSIDE ON REQUEST");
                    loc = getLocation();
                    setUpUserMarker();
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    public void setUpUserMarker(){
        Log.d("USERSLOCATION",""+loc[0]+" "+loc[1]);
        //adding a marker for users location
        LatLng user = new LatLng(loc[0],loc[1]);
        Marker userMarker = mMap.addMarker(new MarkerOptions().position(user).title("User Marker"));
        Log.d("USERMARKER","USER MARKER TITLE: "+userMarker.getTitle());
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
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
        else{
            loc = getLocation();
            Log.d("INELSEMARKER","IN ELSE OF MARKER");
            setUpUserMarker();
        }



        // Add a marker in Sydney and move the camera
        LatLng cla = new LatLng(30.2849,-97.7355);
        buildingsLatLngs.put("College of Liberal Arts (CLA)",cla);
        Marker claMarker = mMap.addMarker(new MarkerOptions().position(cla).title("College of Liberal Arts"));
        info_set.put(claMarker, new String[]{"College of Liberal Arts (CLA)", "0623062306230623062308220822"});
        // HEY. To see what the hour format means, go to parseHourse method. (Monday FIRST!)


        //adding a marker to gregory gym
        LatLng gregoryGym = new LatLng(30.2842,-97.7365);
        buildingsLatLngs.put("Gregory Gymnasium",gregoryGym);
        Marker gregoryMarker = mMap.addMarker(new MarkerOptions().position(gregoryGym).title("Gregory Gym"));
        info_set.put(gregoryMarker, new String[]{"Gregory Gymnasium", "0601060106010601062208221001"});

        //adding a marker to pcl library
        LatLng pcl = new LatLng(30.2827, -97.7381);
        buildingsLatLngs.put("Perry Castaneda Library (PCL)",pcl);
        Marker pclMarker = mMap.addMarker(new MarkerOptions().position(pcl).title("PCL"));
        info_set.put(pclMarker, new String[]{"Perry Castaneda Library (PCL)", "2424242424242424002310231124"});

        //adding a marker to SAC
        LatLng sac = new LatLng(30.2849, -97.7360);
        buildingsLatLngs.put("Student Activity Center (SAC)",sac);
        Marker sacMarker = mMap.addMarker(new MarkerOptions().position(sac).title("SAC"));
        info_set.put(sacMarker, new String[]{"Student Activity Center (SAC)", "07170717071707170717cccccccc"});

        //adding a marker to GDC
        LatLng gdc = new LatLng(30.28628, -97.73662);
        buildingsLatLngs.put("Gates Dell Complex (GDC)",gdc);
        Marker gdcMarker = mMap.addMarker(new MarkerOptions().position(gdc).title("GDC"));
        info_set.put(gdcMarker, new String[]{"Gates Dell Complex (GDC)", "2424242424242424242424242424"});

        //adding a marker to UT Tower
        LatLng mai = new LatLng(30.286096, -97.73938);
        buildingsLatLngs.put("Main Building (MAI)",mai);
        Marker maiMarker = mMap.addMarker(new MarkerOptions().position(mai).title("0722072207220722072207220722"));
        info_set.put(maiMarker, new String[]{"Main Building (MAI)", "2424242424242424242424242424"});

        //adding a marker to Jackson Geological Sciences Building
        LatLng jgb = new LatLng(30.285821, -97.735745);
        buildingsLatLngs.put("Jackson Geological Sciences Building (JGB)",jgb);
        Marker jgbMarker = mMap.addMarker(new MarkerOptions().position(jgb).title("JGB"));
        info_set.put(jgbMarker, new String[]{"Jackson Geological Sciences Building (JGB)", "08220822082208220818cccc1422"});

        //adding a marker to Robert A. Welch Hall
        LatLng wel = new LatLng(30.286696, -97.737692);
        buildingsLatLngs.put("Robert A Welch Hall (WEL)",wel);
        Marker welMarker = mMap.addMarker(new MarkerOptions().position(wel).title("WEL"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(wel));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wel, 17));
        info_set.put(welMarker, new String[]{"Robert A Welch Hall (WEL)", "08220822082208220818cccc1422"});

        //adding a marker to Flawn Academic Center
        LatLng fac = new LatLng(30.286281, -97.740313);
        buildingsLatLngs.put("Flawn Academic Hall (FAC)",fac);
        Marker facMarker = mMap.addMarker(new MarkerOptions().position(fac).title("FAC"));
        info_set.put(facMarker, new String[]{"Flawn Academic Hall (FAC)", "2424242424242424002210220022"});

        //adding a marker to Jack S. Blanton Museum of Art
        LatLng bma = new LatLng(30.281014, -97.737473);
        buildingsLatLngs.put("Jack S Blanton Museum of Art (BMA)",bma);
        Marker bmaMarker = mMap.addMarker(new MarkerOptions().position(bma).title("BMA"));
        info_set.put(bmaMarker, new String[]{"Jack S Blanton Museum of Art (BMA)", "cccc101710171017101711171317"});

        //adding a marker to Harry Ransom Center
        LatLng hrc = new LatLng(30.281014, -97.737473);
        buildingsLatLngs.put("Harry Ransom Center (HRC)",hrc);
        Marker hrcMarker = mMap.addMarker(new MarkerOptions().position(hrc).title("HRC"));
        info_set.put(hrcMarker, new String[]{"Harry Ransom Center (HRC)", "1017101710171019101712171217"});

        //adding a marker to Jester City Limits
        LatLng jcl = new LatLng(30.282806, -97.736771);
        buildingsLatLngs.put("Jester City Limits (JCL)",jcl);
        Marker jclMarker = mMap.addMarker(new MarkerOptions().position(jcl).title("JCL"));
        info_set.put(jclMarker, new String[]{"Jester City Limits (JCL)", "0723072307230723072109200923"});

        //adding a marker to South Mall
        LatLng sou = new LatLng(30.284373, -97.739572);
        buildingsLatLngs.put("South Mall (SOU)",sou);
        Marker souMarker = mMap.addMarker(new MarkerOptions().position(sou).title("SOU"));
        info_set.put(souMarker, new String[]{"South Mall (SOU)", "2424242424242424242424242424"});

        //adding a marker to Waggener Hall
        LatLng wag = new LatLng(30.284995, -97.737630);
        buildingsLatLngs.put("Waggener Hall (WAG)",wag);
        Marker wagMarker = mMap.addMarker(new MarkerOptions().position(wag).title("WAG"));
        info_set.put(wagMarker, new String[]{"Waggener Hall (WAG)", "08170817081708170817cccccccc"});

        if(!lastBuilding.equals("default")){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(buildingsLatLngs.get(lastBuilding)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(buildingsLatLngs.get(lastBuilding), 17));
        }
        else{
            mMap.moveCamera(CameraUpdateFactory.newLatLng(wel));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wel, 17));
        }


       mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("GREENYELLOW","Reached Green yellow color method");
                for( DataSnapshot child: dataSnapshot.getChildren()) {
                    reportList = new ArrayList<Report>();
                    historyRList=new ArrayList<Report>();
                    Log.d("OUTERLOOPKEY", child.getKey());
                    Log.d("OUTERLOOPVAL", child.getValue().toString());

                    if (!child.getKey().equals("history")){
                        String level = "";
                        for (DataSnapshot child2 : child.getChildren()) {
                            Log.d("INNERLOOPKEY", child2.getKey());
                            Log.d("INNERLOOPVAL", child2.getValue().toString());

                            if (!child2.getKey().equals("total_value")) {
                                Report rep = child2.getValue(Report.class);
                                Date currDate = new Date();
                                Date repDate = rep.getTimeOfEntry();
                                if (currDate.getHours() - repDate.getHours() > 1 || currDate.getDay() != repDate.getDay()) {
                                    child2.getRef().removeValue();
                            }   else
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
                            child.getRef().removeValue();
                        } else {
                            int finalavg = (total) / (reportList.size());
                            mDatabase.child(child.getKey()).child("total_value").setValue(getLvlFromNum(finalavg));
                            buildingsMap.put(child.getKey(), getLvlFromNum(finalavg));
                        }
                    }
                    //read history
                    else {
                        Log.d("CKEY",child.getKey());
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
                            int finalavg=0;
                            if(historyRList.size() !=0)
                                finalavg = (total) / (historyRList.size());
                            buildingsHistoryMap.put(child2.getKey(),getLvlFromNum(finalavg));
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
    public int getNumFromLvl(String selectedLvl){
        if (selectedLvl.equals("No prior data")){
            return 0;
        }
        else if (selectedLvl.equals("Not Crowded")) {
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
    public String getLvlFromNum(int newAvg){
        if(newAvg==0){
            return "No prior data";
        }
        else if (newAvg == 1) {
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

        if(info_set.containsKey(marker)){ //This might be an unnecessary check, as we can assume existing markers ar ours
            //we want to say something akin to info = get_info(), info[0] = name, info[1] = hours
            String[] info = info_set.get(marker);
            final String name = info[0];
            final String hours = parseHours(info[1]);

            String [] options={"View Details","Report","Cancel"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setTitle("Select an option")
            builder.setTitle(name)
                    .setItems(options,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which){
                            if(which==0){
                                Intent intent = new Intent(HomeScreenMapsActivity.this,BuildingDetailsActivity.class);
                                intent.putExtra("NAME",name);
                                intent.putExtra("HOURS",hours);
                                if(buildingsMap.get(name) == null)
                                    intent.putExtra("POPULARITY","No Current Data");
                                else
                                    intent.putExtra("POPULARITY",buildingsMap.get(name));
                                if(buildingsHistoryMap.get(name) == null)
                                    intent.putExtra("HISTORY","No Prior Data");
                                else
                                    intent.putExtra("HISTORY",buildingsHistoryMap.get(name));
                                startActivity(intent);
                            }
                            if(which==1){
//                                Intent intent = new Intent(HomeScreenMapsActivity.this,ReportActivity.class);
//                                intent.putExtra("NAME",name);
//                                startActivity(intent);
                                String [] options={"1","2","3","4","5"};
                                final AlertDialog.Builder builder2 = new AlertDialog.Builder(builder.getContext());
                                builder.setTitle("Select an option")
                                        .setItems(options,new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which){
                                                if(which==0){
                                                    final String selectedLvl=getLvlFromNum(which+1);
                                                    mDatabase.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Log.d("GREENYELLOW","Reached Green yellow color method");
                                                            Log.d("DATACHILD",dataSnapshot.getChildrenCount()+"");

                                                            Log.d("DATAKEY",dataSnapshot.getKey());

                                                            Log.d("DATANAME",name);

                                                            for( DataSnapshot child: dataSnapshot.getChildren()){
                                                                if(!child.getKey().equals("total_value")) {
                                                                    Report rep = child.getValue(Report.class);
                                                                    reportList2.add(rep);
                                                                }


                                                            }
                                                            int total=0;
                                                            for(int i=0;i<reportList2.size();i++){
                                                                Report R=reportList2.get(i);
                                                                total+=getNumFromLvl(R.getLevel());
                                                            }
                                                            Log.d("REPORTLIST",reportList2.toString());
                                                            Report newR=new Report(selectedLvl,new Date());
                                                            mDatabase.child(name).push().setValue(newR);
                                                            mDatabase.child("history").child(name).push().setValue(newR);
//                                                            Intent intent2=new Intent(ReportActivity.this,HomeScreenMapsActivity.class);
//                                                            intent2.putExtra("ReportBuilding",name);
//                                                            startActivity(intent2);
//                                                            finish();
                                                            lastBuilding=name;
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
                                                if(which==1){
                                                    final String selectedLvl=getLvlFromNum(which+1);
                                                    mDatabase.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Log.d("GREENYELLOW","Reached Green yellow color method");
                                                            Log.d("DATACHILD",dataSnapshot.getChildrenCount()+"");

                                                            Log.d("DATAKEY",dataSnapshot.getKey());

                                                            Log.d("DATANAME",name);

                                                            for( DataSnapshot child: dataSnapshot.getChildren()){
                                                                if(!child.getKey().equals("total_value")) {
                                                                    Report rep = child.getValue(Report.class);
                                                                    reportList2.add(rep);
                                                                }


                                                            }
                                                            int total=0;
                                                            for(int i=0;i<reportList2.size();i++){
                                                                Report R=reportList2.get(i);
                                                                total+=getNumFromLvl(R.getLevel());
                                                            }
                                                            Log.d("REPORTLIST",reportList2.toString());
                                                            Report newR=new Report(selectedLvl,new Date());
                                                            mDatabase.child(name).push().setValue(newR);
                                                            mDatabase.child("history").child(name).push().setValue(newR);
//                                                            Intent intent2=new Intent(ReportActivity.this,HomeScreenMapsActivity.class);
//                                                            intent2.putExtra("ReportBuilding",name);
//                                                            startActivity(intent2);
//                                                            finish();
                                                            lastBuilding=name;
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
                                                if(which==2){
                                                    final String selectedLvl=getLvlFromNum(which+1);
                                                    mDatabase.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Log.d("GREENYELLOW","Reached Green yellow color method");
                                                            Log.d("DATACHILD",dataSnapshot.getChildrenCount()+"");

                                                            Log.d("DATAKEY",dataSnapshot.getKey());

                                                            Log.d("DATANAME",name);

                                                            for( DataSnapshot child: dataSnapshot.getChildren()){
                                                                if(!child.getKey().equals("total_value")) {
                                                                    Report rep = child.getValue(Report.class);
                                                                    reportList2.add(rep);
                                                                }


                                                            }
                                                            int total=0;
                                                            for(int i=0;i<reportList2.size();i++){
                                                                Report R=reportList2.get(i);
                                                                total+=getNumFromLvl(R.getLevel());
                                                            }
                                                            Log.d("REPORTLIST",reportList2.toString());
                                                            Report newR=new Report(selectedLvl,new Date());
                                                            mDatabase.child(name).push().setValue(newR);
                                                            mDatabase.child("history").child(name).push().setValue(newR);
//                                                            Intent intent2=new Intent(ReportActivity.this,HomeScreenMapsActivity.class);
//                                                            intent2.putExtra("ReportBuilding",name);
//                                                            startActivity(intent2);
//                                                            finish();
                                                            lastBuilding=name;
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
                                                if(which==3){
                                                    final String selectedLvl=getLvlFromNum(which+1);
                                                    mDatabase.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Log.d("GREENYELLOW","Reached Green yellow color method");
                                                            Log.d("DATACHILD",dataSnapshot.getChildrenCount()+"");

                                                            Log.d("DATAKEY",dataSnapshot.getKey());

                                                            Log.d("DATANAME",name);

                                                            for( DataSnapshot child: dataSnapshot.getChildren()){
                                                                if(!child.getKey().equals("total_value")) {
                                                                    Report rep = child.getValue(Report.class);
                                                                    reportList2.add(rep);
                                                                }


                                                            }
                                                            int total=0;
                                                            for(int i=0;i<reportList2.size();i++){
                                                                Report R=reportList2.get(i);
                                                                total+=getNumFromLvl(R.getLevel());
                                                            }
                                                            Log.d("REPORTLIST",reportList2.toString());
                                                            Report newR=new Report(selectedLvl,new Date());
                                                            mDatabase.child(name).push().setValue(newR);
                                                            mDatabase.child("history").child(name).push().setValue(newR);
//                                                            Intent intent2=new Intent(ReportActivity.this,HomeScreenMapsActivity.class);
//                                                            intent2.putExtra("ReportBuilding",name);
//                                                            startActivity(intent2);
//                                                            finish();
                                                            lastBuilding=name;
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
                                                if(which==4){
                                                    final String selectedLvl=getLvlFromNum(which+1);
                                                    mDatabase.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Log.d("GREENYELLOW","Reached Green yellow color method");
                                                            Log.d("DATACHILD",dataSnapshot.getChildrenCount()+"");

                                                            Log.d("DATAKEY",dataSnapshot.getKey());

                                                            Log.d("DATANAME",name);

                                                            for( DataSnapshot child: dataSnapshot.getChildren()){
                                                                if(!child.getKey().equals("total_value")) {
                                                                    Report rep = child.getValue(Report.class);
                                                                    reportList2.add(rep);
                                                                }


                                                            }
                                                            int total=0;
                                                            for(int i=0;i<reportList2.size();i++){
                                                                Report R=reportList2.get(i);
                                                                total+=getNumFromLvl(R.getLevel());
                                                            }
                                                            Log.d("REPORTLIST",reportList2.toString());
                                                            Report newR=new Report(selectedLvl,new Date());
                                                            mDatabase.child(name).push().setValue(newR);
                                                            mDatabase.child("history").child(name).push().setValue(newR);
//                                                            Intent intent2=new Intent(ReportActivity.this,HomeScreenMapsActivity.class);
//                                                            intent2.putExtra("ReportBuilding",name);
//                                                            startActivity(intent2);
//                                                            finish();
                                                            lastBuilding=name;
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

                        }
                    });
            builder.create().show();
            //handle click here
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
            String value=buildingsMap.get(s);

            float l1 = .1f;
            float l2 = .2f;
            float l3 = .3f;
            if(value.equals("Not Crowded")){
                l1=.1f;
                l2=2f;
                l3=3f;
            }
            else if(value.equals("Slightly Crowded")){
                l1=.1f;
                l2=.5f;
                l3=3f;
            }
            else if(value.equals("Crowded")){
                l1=.1f;
                l2=.2f;
                l3=3f;
            }
            else if(value.equals("Very Crowded")){
                l1=.1f;
                l2=.2f;
                l3=1f;
            }
            else {
                l1=.1f;
                l2=.2f;
                l3=.3f;
            }

            float[] startPoints = {
                l1, l2, l3
            };

            List<LatLng> list = new ArrayList<LatLng>();
            list.add(buildingsLatLngs.get(s)); //cla
            Log.d("SVALUE",s);
            Log.d("BUILDING","message: "+buildingsLatLngs.toString());
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


    public String getDayoFWeek(){
        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week abbreviated
        return simpleDateformat.format(now);
    }

    public String parseHours(String format){
        /*
            expects format: 28 int characters, 4 per day, 2 per time_start/time_close
         */
        char[] data = format.toCharArray();
        String day = getDayoFWeek();
        int d;
        switch (day){
            case "Monday" : d = 0;
                break;
            case "Tuesday" : d = 1;
                break;
            case "Wednesday" : d = 2;
                break;
            case "Thursday" : d = 3;
                break;
            case "Friday" : d = 4;
                break;
            case "Saturday" : d = 5;
                break;
            case "Sunday" : d = 6;
                break;
            default: d = -1;
                break;
        }
        if (data[d*4] == 'c')
            return "Closed";
        int b = Integer.parseInt("" + data[d*4] + data[d*4+1]);
        if(b == 24)
            return "24 Hours";
        int e = Integer.parseInt("" + data[d*4+2] + data[d*4+3]);

        String TS1 = ((b / 12) == 1) ? "PM" : "AM";
        String TS2 = ((e / 12) == 1) ? "PM" : "AM";

        if(e == 24)
            return b%12 + ":00" + TS1 + " to midnight";
        if(b == 00)
            return "open until " + e%12 + ":00" + TS2;

        return b%12 + ":00" + TS1 + " - " + e%12 + ":00" + TS2;
    }

    public double[] getLocation(){
        GPSTracker gps = new GPSTracker(this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        Log.d("PhoneLocation",""+latitude+" "+longitude);
        return new double[]{latitude,longitude};
    }


}
