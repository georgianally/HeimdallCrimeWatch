package com.example.heimdallcrimewatch.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CrimeDB.db";
    public static final String LOCATIONS_TABLE_NAME = "savedLocations";
    public static final String LOCATIONS_COLUMN_ID = "id";
    public static final String LOCATIONS_COLUMN_NAME = "name";
    public static final String LOCATIONS_COLUMN_LAT = "lat";
    public static final String LOCATIONS_COLUMN_LNG = "lng";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table savedLocations " +
                "(id INTEGER PRIMARY KEY autoincrement, name text, lat text, lng text)"
        );
        //dummyData();
    }

    private void dummyData() {
        insertLocation("dummyData", "1", "2");
        insertLocation("London Bridge Station", "51.504674", "-0.086006");
        insertLocation("Blackpool Tower", "53.815949", "-3.054943");
        insertLocation("Port of Dover", "51.121014", "1.313163");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS savedLocations");
        onCreate(db);
    }

    public boolean insertLocation(String name, String lat, String lng){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("lat", lat);
        contentValues.put("lng", lng);
        db.insert("savedLocations", null, contentValues);
        return true;
    }
    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from savedLocations where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, LOCATIONS_TABLE_NAME);
        return numRows;
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ LOCATIONS_TABLE_NAME);
        db.execSQL("DELETE FROM sqlite_sequence WHERE name ='" + LOCATIONS_TABLE_NAME + "'");
        dummyData();
        db.close();
    }

    public boolean updateLocation(Integer id, String name, String lat, String lng){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("lat", lat);
        contentValues.put("lng", lng);
        db.update("savedLocations", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public void deleteWithName (String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        /*return db.delete("savedLocations",
                "id = ? ",
                new String[] { Integer.toString(id) });*/
        //Cursor res =  db.rawQuery( "delete from savedLocations where lat like "+lat+" and lng like "+lng+"", null );

        db.delete(LOCATIONS_TABLE_NAME, LOCATIONS_COLUMN_NAME + " = ?",new String[]{String.valueOf(name)});
        //db.close();
    }

    public void deleteLocation (String lat, String lng) {
        SQLiteDatabase db = this.getWritableDatabase();
        /*return db.delete("savedLocations",
                "id = ? ",
                new String[] { Integer.toString(id) });*/
        //Cursor res =  db.rawQuery( "delete from savedLocations where lat like "+lat+" and lng like "+lng+"", null );

        db.delete("savedLocations", LOCATIONS_COLUMN_LAT + " =? AND " + LOCATIONS_COLUMN_LNG + "=?",new String[]{lat, lng});
        //db.close();
    }

    public ArrayList<String> getSavedLocations() {
        return getStrings(LOCATIONS_COLUMN_NAME);
    }

    public ArrayList<String> getSavedLat() {
        return getStrings(LOCATIONS_COLUMN_LAT);
    }

    public ArrayList<String> getSavedLng() {
        return getStrings(LOCATIONS_COLUMN_LNG);
    }

    private ArrayList<String> getStrings(String columnName) {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from savedLocations", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(columnName)));
            res.moveToNext();
        }
        return array_list;
    }
}
