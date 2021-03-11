package fr.remialban.copypasta.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Device {

    int id;
    String name;
    String ip;
    String lastUse;

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
