package naveen16.wheredeyatdoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.support.v7.appcompat.R.styleable.View;

public class ReportActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        final String name = intent.getExtras().getString("NAME");
        TextView buildingName = (TextView) findViewById(R.id.buildingName);
        buildingName.setText(name);
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
                        String value=spinner.getSelectedItem().toString();
                        mDatabase.child(name).setValue(value);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });
    }
}
