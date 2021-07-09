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

public class ContactScan extends Scan {

    private String organization;
    private String title;
    private String firstName;
    private String lastName;

    /**
     * @param context Context of activity or directly the activity
     * @param organization Name of society of the job
     * @param title Job
     * @param firstName
     * @param lastName
     */
    public ContactScan(Context context, String organization, String title, String firstName, String lastName) {
        super(context);
        super.NAME = "contact";
        this.organization = organization;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public ContactScan(Parcel in)
    {
        this.organization = in.readString();
        this.title = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        super.NAME = "contact";
    }

    @Override
    public String getRaw() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("organization", this.organization);
            jsonObject.put("title", this.title);
            jsonObject.put("firstName", this.firstName);
            jsonObject.put("lastName", this.lastName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    @Override
    public String getPlainText() {
        return "<b>" + context.getString(R.string.scan_qr_code_contact_name) + "</b><br/>" + this.firstName + " " + this.lastName + "<br/><b>" + context.getString(R.string.scan_qr_code_contact_title) + "</b><br/>" + this.title + "<br/><b>" + context.getString(R.string.scan_qr_code_contact_organization) + "</b><br/>" + this.organization;
    }

    @Override
    public Drawable getComplementaryButtonDrawable() {
        return ContextCompat.getDrawable(context, R.drawable.ic_contact);
    }

    @Override
    public String getComplementaryButtonText() {
        return context.getString(R.string.select_mode_contact_button);
    }

    @Override
    public void complementaryButtonAction() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, this.firstName + " " + this.lastName);
        intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, this.title);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }

    }

    public String getOrganization() {
        return organization;
    }

    public String getTitle() {
        return title;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.organization);
        dest.writeString(this.title);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
    }

    public static final Creator<ContactScan> CREATOR = new Creator<ContactScan>() {
        @Override
        public ContactScan createFromParcel(Parcel in) {
            return new ContactScan(in);
        }

        @Override
        public ContactScan[] newArray(int size) {
            return new ContactScan[size];
        }
    };
}
