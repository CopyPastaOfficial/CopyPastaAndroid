package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;

import fr.unrealsoftwares.copypasta.models.Scan;

public class PhoneScan extends Scan {

    private String phone;

    public PhoneScan(Context context, String phone) {
        super(context);
        super.NAME = "phone";
        this.phone = phone;
        add();
    }

    public PhoneScan(Parcel in) {
        super();
        this.phone = in.readString();
        super.NAME = "phone";
    }

    @Override
    public String getRaw() {
        return this.phone;
    }

    @Override
    public String getPlainText() {
        return this.phone;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phone);
    }

    public static final Creator<PhoneScan> CREATOR = new Creator<PhoneScan>() {
        @Override
        public PhoneScan createFromParcel(Parcel in) {
            return new PhoneScan(in);
        }

        @Override
        public PhoneScan[] newArray(int size) {
            return new PhoneScan[size];
        }
    };
}
