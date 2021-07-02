package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.models.Scan;

/**
 * Represent email of QR Codes
 */
public class EmailScan extends Scan {

    public String getEmail() {
        return email;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    private final String email;
    private final String subject;
    private final String content;

    public EmailScan(Context context, String email, String subject, String content) {
        super(context);
        this.email = email;
        this.subject = subject;
        this.content = content;
        super.NAME = "email";
        add();
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
        return super.context.getString(R.string.scan_qr_code_email_address) + " :\n" + this.email + "\n" + super.context.getString(R.string.scan_qr_code_email_subject) + " :\n" + this.subject + "\n" + super.context.getString(R.string.scan_qr_code_email_body) + " :\n" + this.content;
    }
}
