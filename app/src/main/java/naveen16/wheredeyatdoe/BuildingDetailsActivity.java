package naveen16.wheredeyatdoe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class BuildingDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_details);
        Intent intent = getIntent();
        String name = intent.getExtras().getString("NAME");
        TextView buildingName = (TextView) findViewById(R.id.buildingName);
        buildingName.setText(name);


        String hours = "Today's hours:\n" + intent.getExtras().getString("HOURS");
        TextView hoursText = (TextView) findViewById(R.id.hoursValue);
        hoursText.setText(hours);
        TextView popularityValue = (TextView) findViewById(R.id.popularityValue);
        popularityValue.setText(intent.getExtras().getString("POPULARITY"));
        TextView popularityHistoryValue = (TextView) findViewById(R.id.popularityHistoryValue);
        popularityHistoryValue.setText(intent.getExtras().getString("HISTORY"));
    }
}
