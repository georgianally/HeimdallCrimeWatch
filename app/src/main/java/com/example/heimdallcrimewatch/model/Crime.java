package com.example.heimdallcrimewatch.model;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

public class Crime implements Parcelable {
    private String id;
    private String category;
    private String locationType; //the type of the location. Either Force or BTP: Force indicates a normal police force location; BTP indicates a British Transport Police location. BTP locations fall within normal police force boundaries.
    private String month;
    private String streetName; //name of street
    private String latitude;
    private String longitude;
    private String outcomeCategory;
    private String outcomeDate;

    public Crime(JSONObject object){
        try {
            id = object.get("id").toString();
            category = object.get("category").toString();
            locationType = object.get("location_type").toString();
            month = object.get("month").toString();
            streetName = object.getJSONObject("location").getJSONObject("street").get("name").toString();
            latitude = object.getJSONObject("location").get("latitude").toString();
            longitude = object.getJSONObject("location").get("longitude").toString();
        if(object.isNull("outcome_status")){
            outcomeCategory = "No Data";
            outcomeDate = "No Data";
        } else {
            outcomeCategory = object.getJSONObject("outcome_status").get("category").toString();
            outcomeDate = object.getJSONObject("outcome_status").get("date").toString();
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCategory() {
        return category;
    }

    public String getLocationType() {
        return locationType;
    }

    public String getMonth() {
        return month;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getOutcomeCategory() {
        return outcomeCategory;
    }

    public String getOutcomeDate() {
        return outcomeDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getImage(){
        String icon = "❌";
        switch (category){
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
                break;
        }
        return icon;
    }

//parcel stuff

    @Override
    public int describeContents() {
        return 0;
    }

    private Crime(Parcel in) {
        id = in.readString();
        category = in.readString();
        locationType = in.readString();
        month = in.readString();
        streetName = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        outcomeCategory = in.readString();
        outcomeDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(category);
        dest.writeString(locationType);
        dest.writeString(month);
        dest.writeString(streetName);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(outcomeCategory);
        dest.writeString(outcomeDate);
    }

    public static final Creator<Crime> CREATOR = new Creator<Crime>() {
        @Override
        public Crime createFromParcel(Parcel in) {
            return new Crime(in);
        }

        @Override
        public Crime[] newArray(int size) {
            return new Crime[size];
        }
    };
}
