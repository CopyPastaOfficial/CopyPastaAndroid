package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;

import fr.unrealsoftwares.copypasta.models.Scan;

public class UrlScan extends Scan {

    private final String url;

    public UrlScan(Context context, String url) {
        super(context);
        this.url = url;
        super.NAME = "url";
        add();
    }

    @Override
    public String get_raw() {
        return this.url;
    }

    public String getUrl() {
        return url;
    }
}
