package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import fr.unrealsoftwares.copypasta.models.Scan;

public class SmsScan extends Scan {

    private final String number;
    private final String content;

    public SmsScan(Context context, String number, String content) {
        super(context);
        this.number = number;
        this.content = content;
        super.NAME = "sms";
        add();
    }

    @Override
    public String get_raw() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("number", this.number);
            jsonObject.put("content", this.content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String getNumber() {
        return number;
    }

    public String getContent() {
        return content;
    }
}
