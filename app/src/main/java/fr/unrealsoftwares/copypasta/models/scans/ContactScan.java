package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import fr.unrealsoftwares.copypasta.models.Scan;

public class ContactScan extends Scan {

    private String organization;
    private String title;
    private String firstName;
    private String lastName;

    /**
     * @param context Context of activity or directly the activity
     * @param organization
     * @param title
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
        return this.firstName + " " + this.lastName + "\n" + this.title + "\n" + this.organization;
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
}
