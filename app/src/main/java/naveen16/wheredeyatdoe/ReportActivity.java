package naveen16.wheredeyatdoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.v7.appcompat.R.styleable.View;

public class ReportActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private Map<String, String> buildingsMap;
    //private Map<String, List<Report>> reportMap;
    List<Report> reportList;
    private Button submit;
    private String selectedLvl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        buildingsMap=new HashMap<String, String>();
        //reportMap=new HashMap<String, List<Report>>();
        reportList=new ArrayList<Report>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        final String name = intent.getExtras().getString("NAME");
        TextView buildingName = (TextView) findViewById(R.id.buildingName);
        buildingName.setText(name);
        submit=(Button)findViewById(R.id.submitButton);
        final Spinner spinner= (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Crowded_Options, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner jjj
        spinner.setAdapter(adapter);
        spinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {
                        Log.d("SELECT","Item Selected");

                        selectedLvl=spinner.getSelectedItem().toString();





                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("GREENYELLOW","Reached Green yellow color method");
                                Log.d("DATACHILD",dataSnapshot.getChildrenCount()+"");

                                Log.d("DATAKEY",dataSnapshot.getKey());
                               // Log.d("DATAVALUE",dataSnapshot.getValue().toString());

                                //Log.d("REPORTCLASSOBJ",rep.toString());
                                Log.d("DATANAME",name);
                                // Get Post object and use the values to update the UI
                                //Post post = dataSnapshot.getValue(Post.class);
                                // ...

                                for( DataSnapshot child: dataSnapshot.getChildren()){
                                    if(!child.getKey().equals("total_value")) {
                                        Report rep = child.getValue(Report.class);
                                        reportList.add(rep);
                                    }
                                        //Report r = new Report(temp.get(0), Integer.parseInt(temp.get(1)));
                                        //buildingsMap.put(key,value);
                                        //Log.d("PREV KEY", "prev ke " + child.getKey()+" report Map keys"+reportMap.keySet().toString());
//                                        Report newR;
//                                        Log.d("REPORTKEYS",reportMap.keySet().toString());
//                                        if(rep != null){
//                                            Log.d("IN INNER", "In inner");
//                                            //Report prevR=reportMap.get((child.getKey()));
//                                            int numLvl=(getNumFromLvl(rep.getLevel())*rep.getNumEntries())+getNumFromLvl(selectedLvl);
//                                            int totAvg=numLvl/(rep.getNumEntries()+1);
//                                            Log.d("total number for:"+dataSnapshot.getKey(), ""+numLvl);
//                                            newR=new Report (getLvlFromNum(totAvg),rep.getNumEntries()+1);
//                                            Log.d("REPORT OBJECT",dataSnapshot.getKey()+newR.toString());
//                                            reportMap.put(dataSnapshot.getKey(),newR);
//                                        }
//                                        else {
//                                            Log.d("ENTER","E");
//                                            newR=new Report(selectedLvl,1);
//                                            reportMap.put(dataSnapshot.getKey(), newR);
//                                            Log.d("ELSEMAP",reportMap.toString());
//                                        }



                                }
                                int total=0;
                                for(int i=0;i<reportList.size();i++){
                                    Report R=reportList.get(i);
                                    total+=getNumFromLvl(R.getLevel());
                                }
                                int finalavg=(total+getNumFromLvl(selectedLvl))/(reportList.size()+1);
                                mDatabase.child(name).child("total_value").setValue(getLvlFromNum(finalavg));
                                //SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                //Date date=new Date();
                                Report newR=new Report(selectedLvl,new Date());
                                //Log.d("REPORT MAP", "report map: " + reportMap.toString());
                                mDatabase.child(name).push().setValue(newR);
                                Intent intent2=new Intent(ReportActivity.this,HomeScreenMapsActivity.class);
                                startActivity(intent2);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                Log.w("CANCELTAG", "loadPost:onCancelled", databaseError.toException());
                                // ...
                            }
                        });

//                        String level="";
//                        int numEntries=0;
//                        int lvl=0;
//                        int currlvl=0;
//                        Log.d("REPORTMAP",reportMap.toString());
//                        Report report = reportMap.get(name);
//                        String crowdedLvl = "";
//                        if(report != null) {
//                            Log.d("REPORT2","Inside IF");
//                            level = report.getLevel();
//                            numEntries = report.getNumEntries();
//                            if (level.equals("Not Crowded")) {
//                                lvl = 1;
//                            } else if (level.equals("Slightly Crowded")) {
//                                lvl = 2;
//                            } else if (level.equals("Crowded")) {
//                                lvl = 3;
//                            } else if (level.equals("Very Crowded")) {
//                                lvl = 4;
//                            } else {
//                                lvl = 5;
//                            }
//                            if (selectedLvl.equals("Not Crowded")) {
//                                currlvl = 1;
//                            } else if (selectedLvl.equals("Slightly Crowded")) {
//                                currlvl = 2;
//                            } else if (selectedLvl.equals("Crowded")) {
//                                currlvl = 3;
//                            } else if (selectedLvl.equals("Very Crowded")) {
//                                currlvl = 4;
//                            } else {
//                                currlvl = 5;
//                            }
//                            int total = lvl * numEntries;
//                            int newAvg = (total + currlvl) / (numEntries + 1);
//
//                            if (newAvg == 1) {
//                                crowdedLvl = "Not Crowded";
//                            } else if (newAvg == 2) {
//                                crowdedLvl = "Slightly Crowded";
//                            } else if (newAvg == 3) {
//                                crowdedLvl = "Crowded";
//                            } else if (newAvg == 4) {
//                                crowdedLvl = "Very Crowded";
//                            } else {
//                                crowdedLvl = "As Crowded as it Gets";
//                            }
//                        }
//                        if(crowdedLvl.equals("")){
//                            crowdedLvl=selectedLvl;
//                        }
//                        Report entry=new Report(crowdedLvl,numEntries+1);
//                        Log.d("RUNNING","writing to database");
//                        Log.d("REPORT OBJECT",entry.toString());
//                        mDatabase.child(name).setValue(entry);
//                        Intent intent2=new Intent(ReportActivity.this,HomeScreenMapsActivity.class);
//                        startActivity(intent2);
                        //Log.d("NAMEOFBUILDING",name+" "+reportMap.get(name)+" ah");


                    }
                });
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
}

