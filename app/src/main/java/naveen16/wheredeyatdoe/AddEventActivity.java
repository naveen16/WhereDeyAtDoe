package naveen16.wheredeyatdoe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEventActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    //UI components
    protected EditText nameEditText;
    protected EditText startTimeEditText;
    protected EditText endTimeEditText;
    protected EditText addressEditText;
    protected EditText dateEditText;
    protected EditText descriptionEditText;
    protected EditText priceEditText;
    protected Button submitButton;

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Intent intent = getIntent();
        final double latitude = intent.getExtras().getDouble("Latitude");
        final double longitude = intent.getExtras().getDouble("Longitude");
        Log.d("ADDEVENTLAT",""+latitude);

        // Initialize Firebase Auth and Database Reference
        //mFirebaseAuth = FirebaseAuth.getInstance();
        //mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //the user that is logged in
        //mUserId = mFirebaseUser.getUid();

        nameEditText = (EditText) findViewById(R.id.eventNameText);
        startTimeEditText = (EditText) findViewById(R.id.startTimeText);
        endTimeEditText = (EditText) findViewById(R.id.endTimeText);
        addressEditText = (EditText) findViewById(R.id.addressText);
        dateEditText = (EditText) findViewById(R.id.dateText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionText);
        priceEditText = (EditText) findViewById(R.id.priceText);

        submitButton = (Button) findViewById(R.id.submitButton);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = nameEditText.getText().toString();
                String startTime = startTimeEditText.getText().toString();
                String endTime = endTimeEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String date = dateEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String price = priceEditText.getText().toString();
                Event event = new Event(date, description, address, eventName, startTime, endTime, price,latitude,longitude);
                mDatabase.child("events").push().setValue(event);
                Intent intent = new Intent(AddEventActivity.this, HomeScreenMapsActivity.class);
                startActivity(intent);
            }
        });
    }
}