package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;

import fr.unrealsoftwares.copypasta.models.Scan;

/**
 * Represent a text scan object of recognition, clipboard, QR Code contains text, ...
 */
public class TextScan extends Scan {

    /**
     * Text content
     */
    private final String content;

    public TextScan(Context context, String content) {
        super(context);
        this.content = content;
        super.NAME = "text";
        add();
    }

    @Override
    public String get_raw() {
        return this.content;
    }

    @Override
    public String getPlainText() {
        return this.content;
    }

    public String getContent()
    {
        return this.content;
    }
}
