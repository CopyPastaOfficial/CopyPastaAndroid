package fr.unrealsoftwares.copypasta.models;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.unrealsoftwares.copypasta.tools.DatabaseManager;

public abstract class Scan {

    /**
     * Contains the type's name of the scan
     */
    protected String NAME;

    /**
     * Contains the id, if the scan saved in database, that allow to remove the scan
     */
    private int id;

    protected Context context;

    /**
     * @see DatabaseManager
     */
    private DatabaseManager databaseManager;

    /**
     * @return Returns the raw data of the scan which will be stored in the database
     */
    public abstract String getRaw();

    /**
     * @return Returns the value to show in the application
     */
    public abstract String getPlainText();

    /**
     * @param context Context of activity or directly the activity
     */
    public Scan(Context context) {
        this.context = context;
        databaseManager = new DatabaseManager(context);
    }

    /**
     * Remove the scan in the database
     */
    public void remove()
    {
        databaseManager.remove("DELETE FROM scans WHERE id = " + this.id + ";");
    }

    /**
     * Add the scan in the database
     */
    public void add()
    {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        databaseManager.add("INSERT INTO scans (type, content, scannedAt) VALUES('" +
                this.NAME +
                "','" +
                this.getRaw() +
                "','" +
                format.format(date) +
                "');");
    }

    public String getJson()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", this.NAME);
            try {
                JSONObject jsonContent = new JSONObject(this.getRaw());
                jsonObject.put("content", jsonContent);
            } catch (JSONException err)
            {
                jsonObject.put("content", this.getRaw());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
}
