package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.models.Scan;

/**
 * Represent the objets contains QR Code which contains Wi-Fi
 */
public class WifiScan extends Scan {

    /**
     * SSID of the Wifi
     */
    private final String ssid;

    /**
     * Key (password) to connect to the WiFi
     */
    private final String key;

    /**
     * Encryption : For example WPA/WPA2, Open, WEP, ...
     */
    private final String encryption;

    public WifiScan(Context context, String ssid, String key, String encryption) {
        super(context);
        this.ssid = ssid;
        this.key = key;
        this.encryption = encryption;
        super.NAME = "wifi";
        add();
    }

    @Override
    public String get_raw() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ssid", this.ssid);
            jsonObject.put("key", this.key);
            jsonObject.put("encryption", this.encryption);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    @Override
    public String getPlainText() {
        return context.getString(R.string.scan_qr_code_wireless_ssid) + "\n" + this.ssid + "\n" + context.getString(R.string.scan_qr_code_wireless_encryption) + "\n" + encryption;
    }

    public String getSsid() {
        return ssid;
    }

    public String getKey() {
        return key;
    }

    public String getEncryption() {
        return encryption;
    }
}
