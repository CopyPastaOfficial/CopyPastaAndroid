package fr.remialban.copypasta.tools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import fr.remialban.copypasta.models.Device;

public class DatabaseManager extends SQLiteOpenHelper {
    Context context;
    SQLiteDatabase db;
    public DatabaseManager(@Nullable Context context) {
        super(context, "CopyPasta.database", null, 1);
        this.context = context;
        getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DATABASE", "onCreate1");
        String query = "CREATE TABLE devices (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "name TEXT NOT NULL," +
                "ip TEXT NOT NULL," +
                "lastUse TEXT NOT NULL" +
                ");";
        db.execSQL(query);
        this.db = db;

        Log.i("DATABASE", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onCreate(db);
    }

    public List<Device> getElements() {
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
    public void removeDevice(Device device) {
        String query = "DELETE FROM devices WHERE id = " + device.getId() + ";";
        getWritableDatabase().execSQL(query);
    }
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

    public void updateDevice(Device device){
        String query = "UPDATE devices SET name='" + device.getName() + "', ip='" + device.getIp() + "', lastUse='" + device.getLastUse() + "' WHERE id=" + device.getId() + ";";
        getReadableDatabase().execSQL(query);
    }
}
