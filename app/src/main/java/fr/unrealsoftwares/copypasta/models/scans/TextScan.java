package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;

import fr.unrealsoftwares.copypasta.models.Scan;

/**
 * Represent a text scan object of recognition, clipboard, QR Code contains text, ...
 */
public class TextScan extends Scan {

    /**
     * Text content
     */
    private String content;

    public TextScan(Context context, String content) {
        super(context);
        this.content = content;
        super.NAME = "text";
        add();
    }

    public TextScan(Parcel in)
    {
        super();
        this.content = in.readString();
        super.NAME = "text";
    }

    @Override
    public String getRaw() {
        return this.content;
    }

    @Override
    public String getPlainText() {
        return this.content;
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

    public String getContent()
    {
        return this.content;
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
        dest.writeString(this.content);
    }

    public static final Creator<TextScan> CREATOR = new Creator<TextScan>() {
        @Override
        public TextScan createFromParcel(Parcel in) {
            return new TextScan(in);
        }

        @Override
        public TextScan[] newArray(int size) {
            return new TextScan[size];
        }
    };
}
