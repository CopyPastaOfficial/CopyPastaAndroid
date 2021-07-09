package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;

import androidx.core.content.ContextCompat;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.models.Scan;

public class UrlScan extends Scan {

    private String url;

    public UrlScan(Context context, String url) {
        super(context);
        this.url = url;
        super.NAME = "url";
        add();
    }

    public UrlScan(Parcel in) {
        super();
        this.url = in.readString();
        super.NAME = "url";
    }

    @Override
    public String getRaw() {
        return this.url;
    }

    @Override
    public String getPlainText() {
        return this.url;
    }

    @Override
    public Drawable getComplementaryButtonDrawable() {
        return ContextCompat.getDrawable(context, R.drawable.ic_browser);
    }

    @Override
    public String getComplementaryButtonText() {
        return context.getString(R.string.select_mode_url_button);
    }

    @Override
    public void complementaryButtonAction() {
        String url = this.url;
        if (!url.startsWith("http://") && !url.startsWith("https://"))
        {
            url = "http://" + url;
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(browserIntent);
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
    }

    public static final Creator<UrlScan> CREATOR = new Creator<UrlScan>() {
        @Override
        public UrlScan createFromParcel(Parcel in) {
            return new UrlScan(in);
        }

        @Override
        public UrlScan[] newArray(int size) {
            return new UrlScan[size];
        }
    };
}
