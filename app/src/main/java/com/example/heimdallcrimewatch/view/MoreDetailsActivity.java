package com.example.heimdallcrimewatch.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.heimdallcrimewatch.R;
import com.example.heimdallcrimewatch.model.Crime;
import com.example.heimdallcrimewatch.model.Header;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MoreDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ImageView categoryImageView;

    private TextView crimeData;
    private TextView outcomeData;
    private TextView locationData;
    private Button backButton;
    private String categoryText;
    private Crime crime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details);

        Header header = findViewById(R.id.header_layout);
        header.initHeader();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        categoryImageView = findViewById(R.id.categoryImageView);
        crimeData = findViewById(R.id.crimeDataTextview);
        outcomeData = findViewById(R.id.outcomeDataTextView);
        locationData = findViewById(R.id.locationDataTextView);
        backButton = findViewById(R.id.backButton);

        Bundle bundle = getIntent().getExtras();
        crime = bundle.getParcelable("crimeData");
        categoryText = crime.getCategory();

        crimeData.setText("ID: " + crime.getId() + "\n" +
                "Category: " + categoryText + "\n" +
                "Police Type: " + crime.getLocationType() + "\n" +
                "Date: " + crime.getMonth());

        outcomeData.setText("Outcome: " + crime.getOutcomeCategory() + "\n" +
                "Outcome Date: " + crime.getOutcomeDate());

        locationData.setText("Street Name: " + crime.getStreetName() + "\n" +
                "Latitude: " + crime.getLatitude() + "\n" +
                "Longitude: " + crime.getLongitude());

        setImage(categoryText);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;
        LatLng location = new LatLng(Double.parseDouble(crime.getLatitude()), Double.parseDouble(crime.getLongitude()));
        mMap.addMarker(new MarkerOptions().position(location).title(categoryText));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
    }

    public void onClicked(View view){
        Intent intent = new Intent(MoreDetailsActivity.this, MainActivity.class);
        this.finish();
        startActivity(intent);
    }

    private void setImage(String categoryText) {
        switch (categoryText){
            case "anti-social-behaviour":
                categoryImageView.setImageDrawable(getResources().getDrawable(R.drawable.antisocial));
                break;
            case "bicycle-theft":
                categoryImageView.setImageDrawable(getResources().getDrawable(R.drawable.bicycle));
                break;
                case "burglary":
            case "other-theft":
            case "robbery":
            case "shoplifting":
            case "theft-from-the-person":
                categoryImageView.setImageDrawable(getResources().getDrawable(R.drawable.thief));
                break;
                case "criminal-damage-arson":
                categoryImageView.setImageDrawable(getResources().getDrawable(R.drawable.crack));
                break;
                case "drugs":
                categoryImageView.setImageDrawable(getResources().getDrawable(R.drawable.drugs));
                break;
                case "possession-of-weapons":
                categoryImageView.setImageDrawable(getResources().getDrawable(R.drawable.weapon));
                break;
                case "public-order":
                categoryImageView.setImageDrawable(getResources().getDrawable(R.drawable.publicorder));
                break;
                case "vehicle-crime":
                categoryImageView.setImageDrawable(getResources().getDrawable(R.drawable.car));
                break;
                case "violent-crime":
                categoryImageView.setImageDrawable(getResources().getDrawable(R.drawable.violence));
                break;
                case "other-crime":
                categoryImageView.setImageDrawable(getResources().getDrawable(R.drawable.other));
                break;
        }
    }
}
