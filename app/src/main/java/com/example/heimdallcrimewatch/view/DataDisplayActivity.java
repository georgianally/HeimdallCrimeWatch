package com.example.heimdallcrimewatch.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.heimdallcrimewatch.R;
import com.example.heimdallcrimewatch.model.Crime;
import com.example.heimdallcrimewatch.model.DBHelper;
import com.example.heimdallcrimewatch.model.Header;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DataDisplayActivity extends AppCompatActivity implements OnMapReadyCallback {

    final Context context = this;

    String lat;
    String lng;

    private GoogleMap mMap;
    boolean mapReady = false;

    Button filterButton;
    Button submitFilterButton;
    Spinner crimeSpinner;
    Spinner monthSpinner;
    Spinner yearSpinner;
    Button savedLocationsButton;
    CheckBox saveLocationCheckBox;
    Button showMapButton;
    Button showListButton;

    private DBHelper mydb;

    ProgressBar apiLoading;

    ListView crimeList;
    ArrayList<String> crimeData;
    ArrayList<Crime> crimeObjects;
    RequestQueue requestQueue;

    String baseURL = "https://data.police.uk/api/crimes-street/";
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        Header header = (Header) findViewById(R.id.headerlayout);
        header.initHeader();

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.INVISIBLE);

        mydb = new DBHelper(this);


        Bundle bundle = getIntent().getExtras();
        lat = bundle.getString("latitude");
        lng = bundle.getString("longitude");

        crimeList = (ListView) findViewById(R.id.crimeListView);
        apiLoading = (ProgressBar) findViewById(R.id.progressBar);
        filterButton = (Button) findViewById(R.id.filtersButton);
        showListButton = (Button) findViewById(R.id.showListButton);
        showMapButton = (Button) findViewById(R.id.showMapButton);
        submitFilterButton = (Button) findViewById(R.id.submitFilterButton);
        savedLocationsButton = (Button) findViewById(R.id.savedLocationsButton);
        crimeSpinner = (Spinner) findViewById(R.id.crimeSpinner);
        monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        saveLocationCheckBox = (CheckBox) findViewById(R.id.saveLocationCheckBox);


        final ArrayAdapter<String> crimeArray = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.crimesTypes));
        crimeSpinner.setAdapter(crimeArray);

        final ArrayAdapter<String> monthArray = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.months));
        monthSpinner.setAdapter(monthArray);

        final ArrayAdapter<String> yearArray = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.years));
        yearSpinner.setAdapter(yearArray);


        //Shows Filters
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleView(crimeSpinner);
                toggleView(monthSpinner);
                toggleView(yearSpinner);
                toggleView(submitFilterButton);
            }
        });

        //Submits filters and querys api
        submitFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String crime = crimeSpinner.getSelectedItem().toString();
                String year = yearSpinner.getSelectedItem().toString();
                String month = monthSpinner.getSelectedItem().toString();
                setURL(lat, lng, crime, year, month);
                apiRequest();
            }
        });

        //Click on list item
        crimeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                moreDetailsActivity(position, false);
            }
        });

        //Save current lat and lng location
        saveLocationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && !checkDatabaseLatLng() ){
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.prompts, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Save",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mydb.insertLocation(userInput.getText().toString(), lat, lng);
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                            saveLocationCheckBox.setChecked(false);
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                } else if(!isChecked && checkDatabaseLatLng()){
                    mydb.deleteLocation(lat, lng);
                    Toast toast = Toast.makeText(getApplicationContext(), "Unchecked", Toast.LENGTH_SHORT); toast.show();
                }
            }
        });

        //Show saved locations
        savedLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataDisplayActivity.this, SavedLocationsActivity.class);
                startActivity(intent);
            }
        });

        //Shows map
        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleView(apiLoading);
                toggleView(crimeList);
                mapFragment.getView().setVisibility(View.VISIBLE);
                toggleView(apiLoading);
            }
        });

        //Shows list
        showListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getView().setVisibility(View.INVISIBLE);
                if(crimeList.getVisibility() == View.GONE){
                toggleView(crimeList);}
            }
        });

        setURL(lat, lng);
        apiRequest();
    }

    private void setMapMarkers() {
        if(mapReady) {
            mMap.clear();
            for (int i = 0; i < crimeObjects.size(); i++) {
                LatLng location = new LatLng(Double.parseDouble(crimeObjects.get(i).getLatitude()), Double.parseDouble(crimeObjects.get(i).getLongitude()));
                Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(crimeObjects.get(i).getStreetName()).snippet(capitaliseFirstLetter(crimeObjects.get(i).getCategory()) + "\n" + crimeObjects.get(i).getMonth() + "\n" + crimeObjects.get(i).getOutcomeCategory()));
                marker.setTag(crimeObjects.get(i).getId());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleView(apiLoading);

        if(checkDatabaseLatLng()){
            saveLocationCheckBox.setChecked(true);
        }
        else{
            saveLocationCheckBox.setChecked(false);
        }
    }

    //Check if current lat and lng are stored in the database - if this location is already saved
    private boolean checkDatabaseLatLng() {
        if(mydb.getSavedLat().contains(lat) && mydb.getSavedLng().contains(lng)){
            return true;
        }
        else{
            return false;
        }
    }

    public void onClicked(View view){
        Intent intent = new Intent(DataDisplayActivity.this, MainActivity.class);
        this.finish();
        startActivity(intent);
    }

    //Takes to more details page
    public void moreDetailsActivity(int position, boolean isMap) {
        Crime crime = null;
        if(isMap){
            for (int i = 0; i < crimeObjects.size(); i++) {
                if(String.valueOf(position).equals(crimeObjects.get(i).getId())){
                    crime = crimeObjects.get(i);
                    break;
                }
            }
        } else { crime = crimeObjects.get(position); }

        Intent intent = new Intent(DataDisplayActivity.this, MoreDetailsActivity.class);
        intent.putExtra("crimeData", crime);
        startActivity(intent);
    }

    public void toggleView(View view){
        if(view.getVisibility()==View.GONE)
            view.setVisibility(View.VISIBLE);
        else if(view.getVisibility()==View.VISIBLE)
            view.setVisibility(View.GONE);
    }

    private void setURL(String lat, String lng){
        this.url = this.baseURL + "allcrime?lat=" + lat + "&lng=" + lng + "&date=2018-06";
    }

    private void setURL(String lat, String lng, String crime, String year, String month){
        this.url = this.baseURL + crime + "?lat=" + lat + "&lng=" + lng + "&date=" + year + "-" + month;
    }

    public void apiRequest () {
        toggleView(apiLoading);
        requestQueue = Volley.newRequestQueue(this);
        crimeData = new ArrayList<String>();
        crimeObjects = new ArrayList<Crime>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, crimeData);
        crimeList.setAdapter(adapter);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray array = new JSONArray(response);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.optJSONObject(i);

                                    String id = object.get("id").toString();
                                    String category = object.get("category").toString();
                                    String locationType = object.get("location_type").toString();
                                    String month = object.get("month").toString();
                                    String streetName = object.getJSONObject("location").getJSONObject("street").get("name").toString();
                                    String latitude = object.getJSONObject("location").get("latitude").toString();
                                    String longitude = object.getJSONObject("location").get("longitude").toString();
                                    String outcomeCategory;
                                    String outcomeDate;
                                    if(object.isNull("outcome_status")){
                                        outcomeCategory = "No Data";
                                        outcomeDate = "No Data";
                                    } else {
                                        outcomeCategory = object.getJSONObject("outcome_status").get("category").toString();
                                        outcomeDate = object.getJSONObject("outcome_status").get("date").toString();
                                    }

                                    Crime crime = new Crime(id, category, locationType, month, streetName, latitude, longitude, outcomeCategory, outcomeDate);
                                    updateList(crime);
                                }
                                toggleView(apiLoading);
                                adapter.notifyDataSetChanged();
                                setMapMarkers();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateList(Crime crime){
        crimeData.add(setImage(crime.getCategory()) + " Category: " + capitaliseFirstLetter(crime.getCategory()) + "\n Location: " + crime.getStreetName() +
                "\n Month: " + crime.getMonth() + "\n Outcome: " + crime.getOutcomeCategory());
        crimeObjects.add(crime);
    }

    private String setImage(String categoryText) {
        String icon = "❌";
        switch (categoryText){
            case "anti-social-behaviour":
                icon = "🗯️";
                break;
            case "bicycle-theft":
                icon = "🚲";
                break;
            case "burglary":
            case "other-theft":
            case "robbery":
            case "shoplifting":
            case "theft-from-the-person":
                icon = "💰";
                break;
            case "criminal-damage-arson":
                icon = "🔨";
                break;
            case "drugs":
                icon = "💊";
                break;
            case "possession-of-weapons":
                icon = "🗡";
                break;
            case "public-order":
                icon = "👎";
                break;
            case "vehicle-crime":
                icon = "🚗";
                break;
            case "violent-crime":
                icon = "🤜";
                break;
            case "other-crime":
                icon = "❌";
                break;
        }
        return icon;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        mMap = googleMap;
        LatLng startingLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

        float zoomLevel = (float) 15.0; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingLocation, zoomLevel));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String id = marker.getTag().toString();
                moreDetailsActivity(Integer.parseInt(id), true);
            }
        });
    }

    static String capitaliseFirstLetter(String name){
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}