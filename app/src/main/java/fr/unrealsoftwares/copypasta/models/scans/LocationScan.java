package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import fr.unrealsoftwares.copypasta.models.Scan;

public class LocationScan extends Scan {

    private final double latitude;

    private final double longitude;

    public LocationScan(Context context, double latitude, double longitude) {
        super(context);
        this.latitude = latitude;
        this.longitude = longitude;
        super.NAME = "location";
        add();
    }

    @Override
    public String get_raw() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude", this.latitude);
            jsonObject.put("longitude", this.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
