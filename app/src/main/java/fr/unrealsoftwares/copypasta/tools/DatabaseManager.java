package fr.unrealsoftwares.copypasta.tools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import fr.unrealsoftwares.copypasta.models.Device;

/**
 * Manage the database
 */
public class DatabaseManager extends SQLiteOpenHelper {

    /**
     * Activity context
     */
    private Context context;

    /**
     * Database
     */
    private SQLiteDatabase db;

    /**
     * Constructor
     * @param context Activity context
     */
    public DatabaseManager(@Nullable Context context) {
        super(context, "CopyPasta.database", null, 1);
        this.context = context;
        getReadableDatabase();
    }

    /**
     * Called when creating of the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE devices (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "name TEXT NOT NULL," +
                "ip TEXT NOT NULL," +
                "lastUse TEXT NOT NULL" +
                ");";
        db.execSQL(query);
        this.db = db;

    }

    /**
     * Called when updating of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onCreate(db);
    }

    /**
     * @return List of devices
     */
    public List<Device> getDevices() {
        List<Device> devices = new ArrayList<>();

        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM devices ORDER BY lastUse DESC;", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Device device = new Device(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            devices.add(device);
            cursor.moveToNext();
        }
        cursor.close();
        return devices;
    }

    /**
     * Remove a device
     * @param device Device to remove
     */
    public void removeDevice(Device device) {
        String query = "DELETE FROM devices WHERE id = " + device.getId() + ";";
        getWritableDatabase().execSQL(query);
    }

    /**
     * Add a device
     * @param device Device to add
     */
    public void addDevice(Device device){
        String query = "INSERT INTO devices (name, ip, lastUse) VALUES ('" +
                device.getName() +
                "','" +
                device.getIp() +
                "','" +
                device.getLastUse() +
                "');";
        getWritableDatabase().execSQL(query);

    }

    /**
     * Update a device
     * @param device Device to update
     */
    public void updateDevice(Device device){
        String query = "UPDATE devices SET name='" + device.getName() + "', ip='" + device.getIp() + "', lastUse='" + device.getLastUse() + "' WHERE id=" + device.getId() + ";";
        getReadableDatabase().execSQL(query);
    }
}
