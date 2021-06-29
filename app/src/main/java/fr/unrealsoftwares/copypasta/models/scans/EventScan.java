package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;

import com.google.mlkit.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.unrealsoftwares.copypasta.models.Scan;

public class EventScan extends Scan {

    private final String summary;
    private final String description;
    private final String organizer;
    private final String location;
    private Date startAt;
    private Date endAt;

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

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

    @Override
    public String get_raw() {
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
}
