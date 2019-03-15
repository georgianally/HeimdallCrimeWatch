package com.example.heimdallcrimewatch.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Crime implements Parcelable {
    String id;
    String category;
    String locationType; //the type of the location. Either Force or BTP: Force indicates a normal police force location; BTP indicates a British Transport Police location. BTP locations fall within normal police force boundaries.
    String month;
    String streetName; //name of street
    String latitude;
    String longitude;
    String outcomeCategory;
    String outcomeDate;

    public Crime(String id, String category, String locationType, String month, String streetName, String latitude, String longitude, String outcomeCategory, String outcomeDate) {
        this.id = id;
        this.category = category;
        this.locationType = locationType;
        this.month = month;
        this.streetName = streetName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.outcomeCategory = outcomeCategory;
        this.outcomeDate = outcomeDate;
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

//parcel stuff

    @Override
    public int describeContents() {
        return 0;
    }

    protected Crime(Parcel in) {
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
