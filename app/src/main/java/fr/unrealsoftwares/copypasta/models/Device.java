package fr.unrealsoftwares.copypasta.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Device {

    /**
     * IP of the device
     */
    private int id;

    /**
     * Device name
     */
    private String name;

    /**
     * Device IP
     */
    private String ip;

    /**
     * Date of last use (last use when the user click on the device in the ListView
     */
    private String lastUse;

    public Device(String name, String ip) {
        this.name = name;
        this.ip = ip;

        this.lastUse();
    }

    public Device(int id, String name, String ip, String lastUse) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.lastUse = lastUse;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void lastUse() {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        this.lastUse = format.format(date);
    }

    public String getIp() {
        return ip;
    }

    public String getLastUse() {
        return lastUse;
    }
}
