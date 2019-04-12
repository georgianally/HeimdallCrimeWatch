package com.example.heimdallcrimewatch.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
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
import com.example.heimdallcrimewatch.model.MapItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

import es.dmoral.toasty.Toasty;


public class DataDisplayActivity extends AppCompatActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterItemInfoWindowClickListener<MapItem> {

    private final Context context = this;

    private String lat;
    private String lng;

    private GoogleMap mMap;
    private boolean mapReady = false;
    private MapItem clickedClusterItem;

    private Button filterButton;
    private Button refreshButton;
    private Button submitFilterButton;
    private Spinner crimeSpinner;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private Button savedLocationsButton;
    private CheckBox saveLocationCheckBox;
    private Button showMapButton;
    private Button showListButton;
    private TextView crimeAmountTextView;

    private DBHelper mydb;

    private ProgressBar apiLoading;

    private ListView crimeList;
    private ArrayList<String> crimeData;
    private ArrayList<Crime> crimeObjects;
    private RequestQueue requestQueue;

    private final String baseURL = "https://data.police.uk/api/crimes-street/";
    private String url;

     ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        Header header = findViewById(R.id.header_layout);
        header.initHeader();

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.INVISIBLE);

        mydb = new DBHelper(this);


        Bundle bundle = getIntent().getExtras();
        lat = bundle.getString("latitude");
        lng = bundle.getString("longitude");

        crimeList = findViewById(R.id.crimeListView);
        apiLoading = findViewById(R.id.progressBar);
        filterButton = findViewById(R.id.filtersButton);
        showListButton = findViewById(R.id.showListButton);
        showMapButton = findViewById(R.id.showMapButton);
        submitFilterButton = findViewById(R.id.submitFilterButton);
        savedLocationsButton = findViewById(R.id.savedLocationsButton);
        crimeSpinner = findViewById(R.id.crimeSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        saveLocationCheckBox = findViewById(R.id.saveLocationCheckBox);
        crimeAmountTextView = findViewById(R.id.crimeAmountTextView);
        refreshButton = findViewById(R.id.refreshButton);


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

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if (isChecked && !mydb.checkDatabaseLatLng(lat, lng)) {
                    saveLocation();
                } else if (!isChecked && mydb.checkDatabaseLatLng(lat, lng)) {
                    mydb.deleteLocation(lat, lng);
                    Toasty.error(DataDisplayActivity.this, "Location Deleted", Toast.LENGTH_SHORT, true).show();
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
                toggleView(crimeList);
                setMapMarkers();
                mapFragment.getView().setVisibility(View.VISIBLE);
            }
        });

        //Shows list
        showListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getView().setVisibility(View.INVISIBLE);
                if (crimeList.getVisibility() == View.GONE) {
                    toggleView(crimeList);
                }
            }
        });

        setURL(lat, lng);
        apiRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mydb.checkDatabaseLatLng(lat, lng)) {
            saveLocationCheckBox.setChecked(true);
        } else {
            saveLocationCheckBox.setChecked(false);
        }
    }

    //*******************************API Request Methods
    private void apiRequest() {
        toggleView(apiLoading);
        requestQueue = Volley.newRequestQueue(this);
        crimeData = new ArrayList<>();
        crimeObjects = new ArrayList<>();

         adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, crimeData);
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
                                    Crime crime = new Crime(object);
                                    updateList(crime);
                                }
                                toggleView(apiLoading);
                                crimeAmountTextView.setText("Crime in this area: " + crimeData.size());
                                adapter.notifyDataSetChanged();
                                setMapMarkers();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    if(networkResponse != null) {
                        switch (networkResponse.statusCode) {
                            case 404:
                                Toasty.error(DataDisplayActivity.this, "Location Not Found", Toast.LENGTH_LONG, true).show();
                                break;
                            case 500:
                                Toasty.error(DataDisplayActivity.this, "Internal Server Error", Toast.LENGTH_LONG, true).show();
                                break;
                            default:
                                Toasty.error(DataDisplayActivity.this, "Error", Toast.LENGTH_LONG, true).show();
                                break;
                        }
                    }
                    toggleView(apiLoading);
                }
            });
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateList(Crime crime) {
        crimeData.add(crime.getImage() + " Category: " + capitaliseFirstLetter(crime.getCategory()) + "\n Location: " + crime.getStreetName() +
                "\n Month: " + crime.getMonth() + "\n Outcome: " + crime.getOutcomeCategory() + "\n");
        crimeObjects.add(crime);
    }

    private void setURL(String lat, String lng) {
        this.url = this.baseURL + "allcrime?lat=" + lat + "&lng=" + lng + "&date=2018-06";
    }

    private void setURL(String lat, String lng, String crime, String year, String month) {
        this.url = this.baseURL + crime + "?lat=" + lat + "&lng=" + lng + "&date=" + year + "-" + month;
    }

    //*******************************Saving Locations and Database
    private void saveLocation() {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompts, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton((Html.fromHtml("<font color='#333333'>Save</font>")),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(TextUtils.isEmpty(userInput.getText().toString().trim())) {
                                    Toasty.warning(DataDisplayActivity.this, "Enter Name for Location \n Location not Saved!", Toast.LENGTH_LONG, true).show();
                                    saveLocationCheckBox.setChecked(false);
                                    saveLocation();
                                }
                                else{
                                    mydb.insertLocation(userInput.getText().toString(), lat, lng);
                                    saveLocationCheckBox.setChecked(true);
                                    Toasty.success(DataDisplayActivity.this, "Location Saved", Toast.LENGTH_SHORT, true).show();
                                }
                            }
                        })
                .setNegativeButton(Html.fromHtml("<font color='#333333'>Cancel</font>"),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                saveLocationCheckBox.setChecked(false);
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    //Takes to more details page
    private void moreDetailsActivity(int position, boolean isMap) {
        Crime crime = null;
        if (isMap) {
            for (int i = 0; i < crimeObjects.size(); i++) {
                if (String.valueOf(position).equals(crimeObjects.get(i).getId())) {
                    crime = crimeObjects.get(i);
                    break;
                }
            }
        } else {
            crime = crimeObjects.get(position);
        }

        Intent intent = new Intent(DataDisplayActivity.this, MoreDetailsActivity.class);
        intent.putExtra("crimeData", crime);
        startActivity(intent);
    }

    //******************************* ETC Methods
    private void toggleView(View view) {
        if (view.getVisibility() == View.GONE)
            view.setVisibility(View.VISIBLE);
        else if (view.getVisibility() == View.VISIBLE)
            view.setVisibility(View.GONE);
    }

    private static String capitaliseFirstLetter(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public void onClicked(View view) {
        Intent intent = new Intent(DataDisplayActivity.this, MainActivity.class);
        this.finish();
        startActivity(intent);
    }

    //*******************************Map Methods
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        mMap = googleMap;
        LatLng startingLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingLocation, 14));
    }

    private void setMapMarkers() {
        if (mapReady) {
            mMap.clear();
            ClusterManager<MapItem> clusterManager = new ClusterManager<>(this, mMap);

            mMap.setOnCameraIdleListener(clusterManager);
            mMap.setOnMarkerClickListener(clusterManager);
            mMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
            mMap.setOnInfoWindowClickListener(clusterManager);
            clusterManager.setOnClusterItemInfoWindowClickListener(this);
            clusterManager.setRenderer(new CustomRenderer<>(this, mMap, clusterManager));

            clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MapItem>() {
                public boolean onClusterItemClick(MapItem item) {
                    clickedClusterItem = item;
                    return false;
                }
            });


        for (int i = 0; i < crimeObjects.size(); i++) {
                Collection<Marker> markers = clusterManager.getClusterMarkerCollection().getMarkers();
                ArrayList<Marker> markerList = new ArrayList<>(markers);
                double finalLat = Double.parseDouble(crimeObjects.get(i).getLatitude());
                double finalLng = Double.parseDouble(crimeObjects.get(i).getLongitude());
                LatLng testLatLng = new LatLng(finalLat, finalLng);

                //If current marker is in same position as an exisiting marker, randomise location so they don't stack
                //Offset marker to try and fix clustering stack not breaking - overlapping markers
                if(markerList.size() != 0){
                    for(i=0; i < markerList.size(); i++){
                        Marker exisitingMarker = markerList.get(i);
                        LatLng position = exisitingMarker.getPosition();

                        if(testLatLng.equals(position)){
                            finalLat = testLatLng.latitude + (Math.random() * 1.000010) + .999999;
                             finalLng = testLatLng.longitude + (Math.random() * 1.010010) + .999999;
                        }
                    }
                }

                MapItem marker = new MapItem(finalLat, finalLng, capitaliseFirstLetter(crimeObjects.get(i).getCategory()),
                        capitaliseFirstLetter(crimeObjects.get(i).getOutcomeCategory()) + "\n" + crimeObjects.get(i).getMonth() + "\n" + crimeObjects.get(i).getStreetName(), crimeObjects.get(i).getId());
                clusterManager.addItem(marker);
            }
            clusterManager.cluster();
            clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomAdapterForItems());
        }
    }

    public void onClusterItemInfoWindowClick(MapItem marker) {
        String id = marker.getTag();
        moreDetailsActivity(Integer.parseInt(id), true);
    }

    class CustomAdapterForItems implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        CustomAdapterForItems() {
            myContentsView = getLayoutInflater().inflate(
                    R.layout.info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tvTitle = myContentsView
                    .findViewById(R.id.txtTitle);
            TextView tvSnippet = myContentsView
                    .findViewById(R.id.txtSnippet);

            tvTitle.setText(clickedClusterItem.getTitle());
            tvSnippet.setText(clickedClusterItem.getSnippet());
            return myContentsView;
        }
    }
    class CustomRenderer<T extends MapItem> extends DefaultClusterRenderer<T> {
        CustomRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
            super(context, map, clusterManager);
            setMinClusterSize(30);
        }
    }
}