package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;

import fr.unrealsoftwares.copypasta.models.Scan;

public class PhoneScan extends Scan {

    public String getPhone() {
        return phone;
    }

    private final String phone;

    public PhoneScan(Context context, String phone) {
        super(context);
        super.NAME = "phone";
        this.phone = phone;
        add();
    }

    @Override
    public String get_raw() {
        return this.phone;
    }

    @Override
    public String getPlainText() {
        return this.phone;
    }
}
