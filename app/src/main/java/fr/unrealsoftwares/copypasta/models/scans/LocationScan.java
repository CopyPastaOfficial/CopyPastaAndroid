package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;

import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.models.Scan;

public class LocationScan extends Scan {

    private double latitude;
    private double longitude;

    public LocationScan(Context context, double latitude, double longitude) {
        super(context);
        this.latitude = latitude;
        this.longitude = longitude;
        super.NAME = "location";
        add();
    }

    public LocationScan(Parcel in)
    {
        super();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        super.NAME = "location";
    }

    @Override
    public String getRaw() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude", this.latitude);
            jsonObject.put("longitude", this.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    @Override
    public String getPlainText() {
        return "<b>" + context.getString(R.string.scan_qr_code_location_lat) + "</b><br/>" + this.latitude + "<br/><b>" + context.getString(R.string.scan_qr_code_location_long) + "</b><br/>" + longitude;
    }

    @Override
    public Drawable getComplementaryButtonDrawable() {
        return ContextCompat.getDrawable(context, R.drawable.ic_baseline_map_24);
    }

    @Override
    public String getComplementaryButtonText() {
        return context.getString(R.string.select_mode_map_button);
    }

    @Override
    public void complementaryButtonAction() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("geo:" + String.valueOf(latitude) + "," + String.valueOf(longitude)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
    }

    public static final Creator<LocationScan> CREATOR = new Creator<LocationScan>() {
        @Override
        public LocationScan createFromParcel(Parcel in) {
            return new LocationScan(in);
        }

        @Override
        public LocationScan[] newArray(int size) {
            return new LocationScan[size];
        }
    };
}
