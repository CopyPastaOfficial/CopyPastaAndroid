package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.provider.ContactsContract;

import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.models.Scan;

/**
 * Represent email of QR Codes
 */
public class EmailScan extends Scan {

    private String email;
    private String subject;
    private String content;

    public EmailScan(Context context, String email, String subject, String content) {
        super(context);
        this.email = email;
        this.subject = subject;
        this.content = content;
        super.NAME = "email";
        add();
    }

    public EmailScan(Parcel in) {
        super();
        this.email = in.readString();
        this.subject = in.readString();
        this.content = in.readString();
        super.NAME = "email";
    }

    public String getEmail() {
        return email;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String getRaw() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("address", this.email);
            jsonObject.put("subject", this.subject);
            jsonObject.put("content", this.content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    @Override
    public String getPlainText() {
        return "<b>" + super.context.getString(R.string.scan_qr_code_email_address) + " </b><br/>" + this.email + "<br/><b>" + super.context.getString(R.string.scan_qr_code_email_subject) + "</b><br/>" + this.subject + "<br/><b>" + super.context.getString(R.string.scan_qr_code_email_body) + "</b><br/>" + this.content;
    }

    @Override
    public Drawable getComplementaryButtonDrawable() {
        return ContextCompat.getDrawable(context, R.drawable.ic_email);
    }

    @Override
    public String getComplementaryButtonText() {
        return context.getString(R.string.select_mode_email_button);
    }

    @Override
    public void complementaryButtonAction() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {this.email});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, this.content);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.subject);
        dest.writeString(this.content);
    }

    public static final Creator<EmailScan> CREATOR = new Creator<EmailScan>() {
        @Override
        public EmailScan createFromParcel(Parcel in) {
            return new EmailScan(in);
        }

        @Override
        public EmailScan[] newArray(int size) {
            return new EmailScan[size];
        }
    };
}
