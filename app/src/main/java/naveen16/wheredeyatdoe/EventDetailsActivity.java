package naveen16.wheredeyatdoe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class EventDetailsActivity extends AppCompatActivity {

    //UI components
    protected TextView nameText;
    protected TextView startTimeText;
    protected TextView endTimeText;
    protected TextView addressText;
    protected TextView dateText;
    protected TextView descriptionText;
    protected TextView priceText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        //get all the text view objects
        nameText = (TextView) findViewById(R.id.eventNameText);
        startTimeText = (TextView) findViewById(R.id.startTimeText);
        endTimeText = (TextView) findViewById(R.id.endTimeText);
        addressText = (TextView) findViewById(R.id.addressText);
        dateText = (TextView) findViewById(R.id.dateText);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
        priceText = (TextView) findViewById(R.id.priceText);

        //populate the ui components
        Intent intent = getIntent();
        nameText.setText(intent.getExtras().get("NAME").toString());
        startTimeText.setText(intent.getExtras().get("START_TIME").toString());
        endTimeText.setText(intent.getExtras().get("END_TIME").toString());
        addressText.setText(intent.getExtras().get("LOCATION").toString());
        dateText.setText(intent.getExtras().get("DATE").toString());
        descriptionText.setText(intent.getExtras().get("DESCRIPTION").toString());
        priceText.setText(intent.getExtras().get("PRICE").toString());



    }
}

