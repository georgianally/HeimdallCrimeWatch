package com.example.heimdallcrimewatch.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.heimdallcrimewatch.R;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    Button currentLocationButton;
    Button enterPostcodeButton;
    Button submitPostcodeButton;
    EditText postcodePlainText;
    Button savedLocationsButton;

    static String lat;
    static String lng;

    RequestQueue requestQueue;
    String baseURL = "https://api.postcodes.io/postcodes/";
    String url;

    LocationManager locationManager;
    //LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentLocationButton = (Button) findViewById(R.id.currentLocationButton);
        enterPostcodeButton = (Button) findViewById(R.id.enterPostcodeButton);
        submitPostcodeButton = (Button) findViewById(R.id.submitPostcodeButton);
        savedLocationsButton = (Button) findViewById(R.id.savedLocationsButton);
        postcodePlainText = (EditText) findViewById(R.id.postcodePlainText);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                getLocation();
            }
        });



        submitPostcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertPostcode();
            }
        });

        enterPostcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleView(postcodePlainText);
                toggleView(submitPostcodeButton);
            }
        });

        savedLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SavedLocationsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }

    public void toggleView(View view){
        if(view.getVisibility()==View.GONE)
            view.setVisibility(View.VISIBLE);
        else if(view.getVisibility()==View.VISIBLE)
            view.setVisibility(View.GONE);
    }

    void getLocation() {
        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lat = String.valueOf(location.getLatitude());
            lng = String.valueOf(location.getLongitude());
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
        displayDataPage(lat, lng);
    }


    public void convertPostcode(){
        requestQueue = Volley.newRequestQueue(this);

        this.url = this.baseURL + postcodePlainText.getText().toString();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String)null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if(response.isNull("result")){
                            lat = "No Data";
                            lng = "No Data";
                        } else {
                            try {
                                lat = response.getJSONObject("result").get("latitude").toString();
                                lng = response.getJSONObject("result").get("longitude").toString();

                                displayDataPage(lat, lng);
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void displayDataPage(String lat, String lng){
        Intent intent = new Intent(this, DataDisplayActivity.class);
        intent.putExtra("latitude", lat);
        intent.putExtra("longitude", lng);
        Log.d("Lat", lat + " " + lng);
        startActivity(intent);
    }
}
