package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;

import com.google.mlkit.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.models.Scan;

public class EventScan extends Scan {

    private String summary;
    private String description;
    private String organizer;
    private String location;
    private Date startAt;
    private Date endAt;

    private SimpleDateFormat simpleDateFormat;

    /**
     * @param context Context of activity or directly the activity
     */
    public EventScan(Context context, String summary, String description, String organizer, String location, String startAt, String endAt) {
        super(context);
        super.NAME = "event";
        this.summary = summary;
        this.description = description;
        this.location = location;
        this.organizer = organizer;

        simpleDateFormat = new SimpleDateFormat("yyyy-MMMM-dd hh:mm");

        try {
            this.startAt = this.simpleDateFormat.parse(startAt);
            this.endAt = this.simpleDateFormat.parse(endAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public EventScan(Parcel in) {
        super();
        super.NAME = "event";
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    @Override
    public String getRaw() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("summary", this.summary);
            jsonObject.put("description", this.description);
            jsonObject.put("location", this.location);
            jsonObject.put("startAt", this.simpleDateFormat.format(this.startAt));
            jsonObject.put("endAt", this.simpleDateFormat.format(this.endAt));
            jsonObject.put("organizer", this.organizer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    @Override
    public String getPlainText() {
        String date = this.simpleDateFormat.format(this.startAt);
        String result = "";

        result += "\n" + context.getString(R.string.scan_qr_code_event_summary) + " " + this.summary;
        result += "\n" + context.getString(R.string.scan_qr_code_event_description) + " " + this.description;
        result += "\n" + context.getString(R.string.scan_qr_code_event_location) + " " + this.location;
        result += "\n" + context.getString(R.string.scan_qr_code_event_start) + " " + date.substring(0, 4) + "-" + date.substring(5, 7) + "-" + date.substring(8, 10) + " to " + date.substring(11, 16) ;
        date = this.simpleDateFormat.format(this.endAt);
        result += "\n" + context.getString(R.string.scan_qr_code_event_end) + " " + date.substring(0, 4) + "-" + date.substring(5, 7) + "-" + date.substring(8, 10) + " to " + date.substring(11, 16) ;
        return result;
    }

    @Override
    public Drawable getComplementaryButtonDrawable() {
        return null;
    }

    @Override
    public String getComplementaryButtonText() {
        return null;
    }

    @Override
    public void complementaryButtonAction() {

    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Date getStartAt() {
        return startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public static String formatDate(Barcode.CalendarDateTime dateTime)
    {
        return dateTime.getYear() + "-" + dateTime.getMonth() + "-" + dateTime.getDay() + " " + dateTime.getHours() + ":" + dateTime.getMinutes();
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }

    public void setSimpleDateFormat(SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Creator<EventScan> CREATOR = new Creator<EventScan>() {
        @Override
        public EventScan createFromParcel(Parcel in) {
            return new EventScan(in);
        }

        @Override
        public EventScan[] newArray(int size) {
            return new EventScan[size];
        }
    };
}
