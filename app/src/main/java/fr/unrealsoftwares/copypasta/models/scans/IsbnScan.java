package fr.unrealsoftwares.copypasta.models.scans;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;

import androidx.core.content.ContextCompat;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.models.Scan;

public class IsbnScan extends Scan {

    private String isbn;

    public IsbnScan(Context context, String isbn) {
        super(context);
        this.isbn = isbn;
        super.NAME = "isbn";
        add();
    }

    public IsbnScan(Parcel in) {
        super();
        this.isbn = in.readString();
        super.NAME = "isbn";
    }

    @Override
    public String getRaw() {
        return this.isbn;
    }

    @Override
    public String getPlainText() {
        return this.isbn;
    }

    @Override
    public Drawable getComplementaryButtonDrawable() {
        return ContextCompat.getDrawable(context, R.drawable.ic_baseline_search_24);
    }

    @Override
    public String getComplementaryButtonText() {
        return context.getString(R.string.select_mode_search_button);
    }

    @Override
    public void complementaryButtonAction() {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(SearchManager.QUERY, this.isbn);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }

    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.isbn);
    }

    public static final Creator<IsbnScan> CREATOR = new Creator<IsbnScan>() {
        @Override
        public IsbnScan createFromParcel(Parcel in) {
            return new IsbnScan(in);
        }

        @Override
        public IsbnScan[] newArray(int size) {
            return new IsbnScan[size];
        }
    };
}
