package com.example.heimdallcrimewatch.view;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.heimdallcrimewatch.R;
import com.example.heimdallcrimewatch.model.DBHelper;
import com.example.heimdallcrimewatch.model.Header;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class SavedLocationsActivity extends AppCompatActivity {

    private ListView locationList;
    private Button deleteAllButton;
    private ArrayList<String> locationData;

    private DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations);

        Header header = findViewById(R.id.header_layout);
        header.initHeader();

        mydb = new DBHelper(this);

        locationList = findViewById(R.id.locationsListView);
        deleteAllButton = findViewById(R.id.deleteAllButton);
        locationData = new ArrayList<>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationData);
        locationList.setAdapter(adapter);

        initList(adapter);

        locationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedLocation = locationData.get(position);

                for (int i = 0; i < mydb.numberOfRows(); i++){
                    Cursor rs = mydb.getData(i);
                    if(rs.moveToFirst()) {
                        String dbName = mydb.getSavedLocations().get(i);

                        if(clickedLocation.equals(dbName)){
                            Intent intent = new Intent(SavedLocationsActivity.this, DataDisplayActivity.class);
                            intent.putExtra("latitude", mydb.getSavedLat().get(i));
                            intent.putExtra("longitude", mydb.getSavedLng().get(i));
                            rs.close();
                            mydb.close();
                            startActivity(intent);
                            break;
                        }
                    }
                }
            }
        });

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb.deleteAll();
                locationData.clear();
                adapter.notifyDataSetChanged();
                initList(adapter);
                Toasty.error(SavedLocationsActivity.this, "Location(s) Deleted", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public void onClicked(View view){
        Intent intent = new Intent(SavedLocationsActivity.this, MainActivity.class);
        this.finish();
        startActivity(intent);
    }

    private void initList(ArrayAdapter adapter) {
        for (int i = 0; i < mydb.numberOfRows(); i++){
            Cursor rs = mydb.getData(i);
            if(rs.moveToFirst()) {
                locationData.add(mydb.getSavedLocations().get(i));
            }
            rs.close();
        }
        adapter.notifyDataSetChanged();
    }
}
