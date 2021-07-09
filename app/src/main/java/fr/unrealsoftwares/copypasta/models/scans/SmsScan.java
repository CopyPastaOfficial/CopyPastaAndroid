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

public class SmsScan extends Scan {

    private String number;
    private String content;

    public SmsScan(Context context, String number, String content) {
        super(context);
        this.number = number;
        this.content = content;
        super.NAME = "sms";
        add();
    }

    public SmsScan(Parcel in) {
        super();
        this.number = in.readString();
        this.content = in.readString();
        super.NAME = "sms";
    }

    @Override
    public String getRaw() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("number", this.number);
            jsonObject.put("content", this.content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    @Override
    public String getPlainText() {
        return "<b>" + context.getString(R.string.scan_qr_code_sms_number)+ "</b><br/>" + this.number + "<br/><b>" + context.getString(R.string.scan_qr_code_sms_content) + "</b><br/>" + this.content;
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
    public void complementaryButtonAction() {}

    public String getNumber() {
        return number;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.number);
        dest.writeString(this.content);
    }

    public static final Creator<SmsScan> CREATOR = new Creator<SmsScan>() {
        @Override
        public SmsScan createFromParcel(Parcel in) {
            return new SmsScan(in);
        }

        @Override
        public SmsScan[] newArray(int size) {
            return new SmsScan[size];
        }
    };
}
