package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;

import fr.unrealsoftwares.copypasta.models.Scan;

public class IsbnScan extends Scan {

    private final String isbn;

    public IsbnScan(Context context, String isbn) {
        super(context);
        this.isbn = isbn;
        super.NAME = "isbn";
        add();
    }

    @Override
    public String get_raw() {
        return this.isbn;
    }

    public String getIsbn() {
        return isbn;
    }
}
