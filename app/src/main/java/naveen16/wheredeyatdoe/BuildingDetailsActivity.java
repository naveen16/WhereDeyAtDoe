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
        TextView buildingName = (TextView) findViewById(R.id.textView);
        buildingName.setText(name);
    }
}
