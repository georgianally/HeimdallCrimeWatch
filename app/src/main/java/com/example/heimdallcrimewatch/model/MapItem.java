package com.example.heimdallcrimewatch.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MapItem implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private final String tag;

    public MapItem(double lat, double lng, String title, String snippet, String tag) {
        this.position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
        this.tag = tag;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getTag(){
        return tag;
    }
}
