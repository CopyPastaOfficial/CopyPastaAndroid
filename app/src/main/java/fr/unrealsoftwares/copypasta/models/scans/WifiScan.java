package fr.unrealsoftwares.copypasta.models.scans;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Parcel;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

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
    private String ssid;

    /**
     * Key (password) to connect to the WiFi
     */
    private String key;

    /**
     * Encryption : For example WPA/WPA2, Open, WEP, ...
     */
    private String encryption;

    public WifiScan(Context context, String ssid, String key, String encryption) {
        super(context);
        this.ssid = ssid;
        this.key = key;
        this.encryption = encryption;
        super.NAME = "wifi";
        add();
    }

    public WifiScan(Parcel in) {
        super();
        this.ssid = in.readString();
        this.encryption = in.readString();
        this.key = in.readString();
        super.NAME = "wifi";
    }

    @Override
    public String getRaw() {
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
        String encryption = this.encryption;

        if (encryption.equals("OPEN"))
        {
            encryption = context.getString(R.string.scan_qr_code_wireless_encryption_open);
        }
        return "<b>" + context.getString(R.string.scan_qr_code_wireless_ssid) + "</b><br/>" + this.ssid + "<br/><b>" + context.getString(R.string.scan_qr_code_wireless_encryption) + "</b><br/>" + encryption + "<br/><b>" + context.getString(R.string.scan_qr_code_wireless_password) + "</b><br/>" + this.key;
    }

    @Override
    public Drawable getComplementaryButtonDrawable() {
        return ContextCompat.getDrawable(context, R.drawable.ic_wifi);
    }

    @Override
    public String getComplementaryButtonText() {
        return context.getString(R.string.select_mode_wifi_button);
    }

    @Override
    public void complementaryButtonAction() {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + this.ssid + "\"";   // Please note the quotes. String should contain ssid in quotes
        if (this.encryption.equals("WPA/WPA2"))
        {
            conf.preSharedKey = "\""+ this.key +"\"";
        } else if (this.encryption.equals("WEP"))
        {
            conf.wepKeys[0] = "\"" + this.key + "\"";
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if (this.encryption.equals("OPEN"))
        {
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);
        }

        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ssid);
        dest.writeString(this.encryption);
        dest.writeString(this.key);
    }

    public static final Creator<WifiScan> CREATOR = new Creator<WifiScan>() {
        @Override
        public WifiScan createFromParcel(Parcel in) {
            return new WifiScan(in);
        }

        @Override
        public WifiScan[] newArray(int size) {
            return new WifiScan[size];
        }
    };
}
