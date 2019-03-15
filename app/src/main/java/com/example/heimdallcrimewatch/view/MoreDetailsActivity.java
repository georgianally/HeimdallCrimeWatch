package com.example.heimdallcrimewatch.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heimdallcrimewatch.R;
import com.example.heimdallcrimewatch.model.Crime;
import com.example.heimdallcrimewatch.model.Header;

public class MoreDetailsActivity extends AppCompatActivity {

    /*
    TextView id;
    TextView category;
    TextView locationType;
    TextView month;
    TextView streetName;
    TextView latitude;
    TextView longitude;
    TextView outcomeCategory;
    TextView outcomeDate;
    */

    ImageView categoryImageView;

    TextView crimeData;
    TextView outcomeData;
    TextView locationData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details);

        /*final int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(MoreDetailsActivity.this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });
        */

        Header header = (Header) findViewById(R.id.headerlayout);
        header.initHeader();
/*
        id = (TextView) findViewById(R.id.idTextView);
        category = (TextView) findViewById(R.id.categoryTextView);
        locationType = (TextView) findViewById(R.id.locationTypeTextView);
        month = (TextView) findViewById(R.id.monthTextView);
        streetName = (TextView) findViewById(R.id.streetNameTextView);
        latitude = (TextView) findViewById(R.id.latitudeTextView);
        longitude = (TextView) findViewById(R.id.longitudeTextView);
        outcomeCategory = (TextView) findViewById(R.id.outcomeCategoryTextView);
        outcomeDate = (TextView) findViewById(R.id.outcomeDateTextView);*/

        categoryImageView = (ImageView) findViewById(R.id.categoryImageView);

        crimeData = (TextView) findViewById(R.id.crimeDataTextview);
        outcomeData = (TextView) findViewById(R.id.outcomeDataTextView);
        locationData = (TextView) findViewById(R.id.locationDataTextView);

        Bundle bundle = getIntent().getExtras();
        Crime crime = (Crime) bundle.getParcelable("crimeData");
        String categoryText = crime.getCategory();

        crimeData.setText("ID: " + crime.getId() + "\n" +
                "Category: " + categoryText + "\n" +
                "Police Type: " + crime.getLocationType() + "\n" +
                "Date: " + crime.getMonth());

        outcomeData.setText("Outcome: " + crime.getOutcomeCategory() + "\n" +
                "Outcome Date: " + crime.getOutcomeDate());

        locationData.setText("Street Name: " + crime.getStreetName() + "\n" +
                "Latitude: " + crime.getLatitude() + "\n" +
                "Longitude: " + crime.getLongitude());

        /*
        id.setText(id.getText() + crime.getId());
        category.setText(category.getText() + categoryText);
        locationType.setText(locationType.getText() + crime.getLocationType());
        month.setText(month.getText() + crime.getMonth());
        streetName.setText(streetName.getText() + crime.getStreetName());
        latitude.setText(latitude.getText() + crime.getLatitude());
        longitude.setText(longitude.getText() + crime.getLongitude());
        outcomeCategory.setText(outcomeCategory.getText() + crime.getOutcomeCategory());
        outcomeDate.setText(outcomeDate.getText() + crime.getOutcomeDate());*/

        setImage(categoryText);
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
