package naveen16.wheredeyatdoe;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
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

public class HomeScreenMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    HashMap<Marker, String[]> info_set = new HashMap<>();

    /*private Marker claMarker;
    private Marker gregoryMarker;
    private Marker pclMarker;
    private Marker sacMarker;*/

    private DatabaseReference mDatabase;

    private Map<String,String> buildingsMap;
    private Map<String,LatLng> buildingsLatLngs;


    List<Report> reportList;

    TileProvider mProvider;
    TileOverlay mOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_maps);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        buildingsMap=new HashMap<String, String>();
        buildingsLatLngs=new HashMap<String, LatLng>();
        reportList=new ArrayList<Report>();
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
        buildingsLatLngs.put("College of Liberal Arts (CLA)",cla);
        Marker claMarker = mMap.addMarker(new MarkerOptions().position(cla).title("College of Liberal Arts"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cla));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cla, 17));
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
        info_set.put(sacMarker, new String[]{"Student Activity Center (SAC)", "hours3"});

        //adding a marker to GDC
        LatLng gdc = new LatLng(30.28628, -97.73662);
        buildingsLatLngs.put("Gates Dell Complex (GDC)",gdc);
        Marker gdcMarker = mMap.addMarker(new MarkerOptions().position(gdc).title("GDC"));
        info_set.put(gdcMarker, new String[]{"Gates Dell Complex (GDC)", "hours4"});

        //adding a marker to UT Tower
        LatLng mai = new LatLng(30.286096, -97.73938);
        buildingsLatLngs.put("Main Building (MAI)",mai);
        Marker maiMarker = mMap.addMarker(new MarkerOptions().position(mai).title("MAI"));
        info_set.put(maiMarker, new String[]{"Main Building (MAI)", "hours5"});

        //adding a marker to Jackson Geological Sciences Building
        LatLng jgb = new LatLng(30.285821, -97.735745);
        buildingsLatLngs.put("Jackson Geological Sciences Building (JGB)",jgb);
        Marker jgbMarker = mMap.addMarker(new MarkerOptions().position(jgb).title("JGB"));
        info_set.put(jgbMarker, new String[]{"Jackson Geological Sciences Building (JGB)", "hours6"});

        //adding a marker to Robert A. Welch Hall
        LatLng wel = new LatLng(30.286696, -97.737692);
        buildingsLatLngs.put("Robert A. Welch Hall (WEL)",wel);
        Marker welMarker = mMap.addMarker(new MarkerOptions().position(wel).title("WEL"));
        info_set.put(welMarker, new String[]{"Robert A. Welch Hall (WEL)", "hours7"});

        //adding a marker to Flawn Academic Center
        LatLng fac = new LatLng(30.286281, -97.740313);
        buildingsLatLngs.put("Flawn Academic Hall (FAC)",fac);
        Marker facMarker = mMap.addMarker(new MarkerOptions().position(fac).title("FAC"));
        info_set.put(facMarker, new String[]{"Flawn Academic Hall (FAC)", "hours8"});

        //adding a marker to Jack S. Blanton Museum of Art
        LatLng bma = new LatLng(30.281014, -97.737473);
        buildingsLatLngs.put("Jack S. Blanton Museum of Art (BMA)",bma);
        Marker bmaMarker = mMap.addMarker(new MarkerOptions().position(bma).title("BMA"));
        info_set.put(bmaMarker, new String[]{"Jack S. Blanton Museum of Art (BMA)", "hours9"});

        //adding a marker to Harry Ransom Center
        LatLng hrc = new LatLng(30.281014, -97.737473);
        buildingsLatLngs.put("Harry Ransom Center (HRC)",hrc);
        Marker hrcMarker = mMap.addMarker(new MarkerOptions().position(hrc).title("HRC"));
        info_set.put(hrcMarker, new String[]{"Harry Ransom Center (HRC)", "hours10"});

        //adding a marker to Jester City Limits
        LatLng jcl = new LatLng(30.282806, -97.736771);
        buildingsLatLngs.put("Jester City Limits (JCL)",jcl);
        Marker jclMarker = mMap.addMarker(new MarkerOptions().position(jcl).title("JCL"));
        info_set.put(jclMarker, new String[]{"Jester City Limits (JCL)", "hours11"});

        //adding a marker to South Mall
        LatLng sou = new LatLng(30.284373, -97.739572);
        buildingsLatLngs.put("South Mall (SOU)",sou);
        Marker souMarker = mMap.addMarker(new MarkerOptions().position(sou).title("SOU"));
        info_set.put(souMarker, new String[]{"South Mall (SOU)", "hours12"});

        //adding a marker to Waggener Hall
        LatLng wag = new LatLng(30.284995, -97.737630);
        buildingsLatLngs.put("Waggener Hall (WAG)",wag);
        Marker wagMarker = mMap.addMarker(new MarkerOptions().position(wag).title("WAG"));
        info_set.put(wagMarker, new String[]{"Waggener Hall (WAG)", "hours13"});

       mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("GREENYELLOW","Reached Green yellow color method");
                // Get Post object and use the values to update the UI
                //Post post = dataSnapshot.getValue(Post.class);
                // ...
                for( DataSnapshot child: dataSnapshot.getChildren()){
                    Log.d("OUTERLOOPKEY",child.getKey());
                    Log.d("OUTERLOOPVAL",child.getValue().toString());

                        //List<String> temp = new ArrayList<String>();
                        String level="";
                        for (DataSnapshot child2 : child.getChildren()) {
                            Log.d("INNERLOOPKEY",child2.getKey());
                            Log.d("INNERLOOPVAL",child2.getValue().toString());

                            if(!child2.getKey().equals("total_value")){
                                Report rep = child2.getValue(Report.class);
                                Date currDate= new Date();
                                Date repDate=rep.getTimeOfEntry();
                                if(currDate.getHours()-repDate.getHours()>1 || currDate.getDay() != repDate.getDay()){
                                    child2.getRef().removeValue();
                                }
                                else
                                    reportList.add(rep);

                            }
//                            String key = child2.getKey();
//                            String value = child2.getValue().toString();
//                            temp.add(value);
                        }
                        int total=0;
                        for(int i=0;i<reportList.size();i++){
                            Report R=reportList.get(i);
                            total+=getNumFromLvl(R.getLevel());
                        }
                        if(reportList.size()==0){
                            child.getRef().removeValue();
                        }
                        else {
                            int finalavg = (total) / (reportList.size());
                            mDatabase.child(child.getKey()).child("total_value").setValue(getLvlFromNum(finalavg));
                            //Report r= new Report(temp.get(0),Integer.parseInt(temp.get(1)));

                            buildingsMap.put(child.getKey(), getLvlFromNum(finalavg));
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
        //mPostReference.addValueEventListener(postListener);

        //addHeatMap();

    }
    public int getNumFromLvl(String selectedLvl){
        if (selectedLvl.equals("Not Crowded")) {
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
        if (newAvg == 1) {
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
        /*
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
        }*/

        if(info_set.containsKey(marker)){ //This might be an unnecessary check, as we can assume existing markers ar ours
            //we want to say something akin to info = get_info(), info[0] = name, info[1] = hours
            String[] info = info_set.get(marker);
            final String name = info[0];
            final String hours = parseHours(info[1]);

            //DialogFragment df = new ReportDialogFragment();
            //df.show(getSupportFragmentManager(),"Option");
            String [] options={"View Details","Report","Cancel"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select an option")
                    .setItems(options,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which){
                            if(which==0){
                                Intent intent = new Intent(HomeScreenMapsActivity.this,BuildingDetailsActivity.class);
                                intent.putExtra("NAME",name);
                                intent.putExtra("HOURS",hours);
                                startActivity(intent);
                            }
                            if(which==1){
                                Intent intent = new Intent(HomeScreenMapsActivity.this,ReportActivity.class);
                                intent.putExtra("NAME",name);
                                startActivity(intent);
                            }

                        }
                    });
            builder.create().show();
            //handle click here
        }

        /*if (marker.equals(claMarker))
        {
            //DialogFragment df = new ReportDialogFragment();
            //df.show(getSupportFragmentManager(),"Option");
            String hours = "";
            if(day.equals("Monday") || day.equals("Tuesday") || day.equals("Wednesday") || day.equals("Thursday") || day.equals("Friday"))
                hours = "6:00AM - 11:00PM";
            else
                hours = "8:00AM - 10:00PM";
            String [] options={"View Details","Report","Cancel"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select an option")
                    .setItems(options,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which){
                            String hours = "";
                            if(day.equals("Monday") || day.equals("Tuesday") || day.equals("Wednesday") || day.equals("Thursday") || day.equals("Friday"))
                                hours = "6:00AM - 11:00PM";
                            else
                                hours = "8:00AM - 10:00PM";
                            if(which==0){
                                Intent intent = new Intent(HomeScreenMapsActivity.this,BuildingDetailsActivity.class);
                                intent.putExtra("NAME","College of Liberal Arts (CLA)");
                                intent.putExtra("HOURS",hours);
                                startActivity(intent);
                            }
                            if(which==1){
                                Intent intent = new Intent(HomeScreenMapsActivity.this,ReportActivity.class);
                                intent.putExtra("NAME","College of Liberal Arts (CLA)");
                                startActivity(intent);
                            }

                        }
                    });
            builder.create().show();
            //handle click here

        }
        else if(marker.equals(gregoryMarker)){
            //DialogFragment df = new ReportDialogFragment();
            //df.show(getSupportFragmentManager(),"Option");

            String [] options={"View Details","Report","Cancel"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select an option")
                    .setItems(options,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which){
                            String hours = "";
                            if(day.equals("Monday") || day.equals("Tuesday") || day.equals("Wednesday") || day.equals("Thursday"))
                                hours = "6:00AM - 1:00AM";
                            else if(day.equals("Friday"))
                                hours = "6:00AM - 10:00PM";
                            else if(day.equals("Saturday"))
                                hours = "8:00AM - 10:00PM";
                            else
                                hours = "10:00AM - 1:00AM";
                            if(which==0){
                                Intent intent = new Intent(HomeScreenMapsActivity.this,BuildingDetailsActivity.class);
                                intent.putExtra("NAME","Gregory Gymnasium");
                                intent.putExtra("HOURS",hours);
                                startActivity(intent);
                            }
                            if(which==1){
                                Intent intent = new Intent(HomeScreenMapsActivity.this,ReportActivity.class);
                                intent.putExtra("NAME","Gregory Gymnasium");
                                startActivity(intent);
                            }

                        }
                    });
            builder.create().show();

        }
        else if(marker.equals(pclMarker)){
            //DialogFragment df = new ReportDialogFragment();
            //df.show(getSupportFragmentManager(),"Option");

            String [] options={"View Details","Report","Cancel"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select an option")
                    .setItems(options,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String hours = "";
                            if (day.equals("Monday") || day.equals("Tuesday") || day.equals("Wednesday") || day.equals("Thursday"))
                                hours = "Open 24 hours";
                            else if (day.equals("Friday"))
                                hours = "12:00AM - 11:00PM";
                            else if (day.equals("Saturday"))
                                hours = "10:00AM - 11:00PM";
                            else
                                hours = "11:00AM - 12:00AM";
                            if (which == 0) {
                                Intent intent = new Intent(HomeScreenMapsActivity.this, BuildingDetailsActivity.class);
                                intent.putExtra("NAME", "Perry Castaneda Library (PCL)");
                                intent.putExtra("HOURS", hours);
                                startActivity(intent);
                            }
                            if (which == 1) {
                                Intent intent = new Intent(HomeScreenMapsActivity.this,ReportActivity.class);
                                intent.putExtra("NAME","Perry Castaneda Library (PCL)");
                                startActivity(intent);
                            }
                        }
                    });
            builder.create().show();

        }

        else if(marker.equals(sacMarker)){
            //DialogFragment df = new ReportDialogFragment();
            //df.show(getSupportFragmentManager(),"Option");

            String [] options={"View Details","Report","Cancel"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select an option")
                    .setItems(options,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which){
                            String hours = "";
                            if(day.equals("Tuesday") || day.equals("Wednesday") || day.equals("Thursday") || day.equals("Friday"))
                                hours = "7:00AM - 5:00PM";
                            else if(day.equals("Monday"))
                                hours = "7:00AM - 12:00AM";
                            else if(day.equals("Saturday"))
                                hours = "10:00AM - 3:00AM";
                            else
                                hours = "12:00PM - 3:00AM";
                            if(which==0){
                                Intent intent = new Intent(HomeScreenMapsActivity.this,BuildingDetailsActivity.class);
                                intent.putExtra("NAME","Student Activity Center (SAC)");
                                intent.putExtra("HOURS",hours);
                                startActivity(intent);
                            }
                            if(which==1){
                                Intent intent = new Intent(HomeScreenMapsActivity.this,ReportActivity.class);
                                intent.putExtra("NAME","Student Activity Center (SAC)");
                                startActivity(intent);
                            }

                        }
                    });
            builder.create().show();

        }
        else{

        }*/
        return true;

    }


    private void addHeatMap() {
       // List<LatLng> list = new ArrayList<LatLng>();
//        list.add(new LatLng(30.2849, -97.7355)); //cla
//        list.add(new LatLng(30.2842, -97.7365)); //greg
//        list.add(new LatLng(30.2827, -97.7381)); //pcl
//        list.add(new LatLng(30.2849, -97.7360)); //sac

        //String crowdedLvl = buildingsMap.get("Student Activity Center (SAC)");
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
}
