package fr.unrealsoftwares.copypasta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.models.Device;
import fr.unrealsoftwares.copypasta.tools.DatabaseManager;

public class DevicesAdapter extends BaseAdapter {

    Context context;
    List<Device> devices;
    LayoutInflater inflater;
    DatabaseManager db;
    public DevicesAdapter(Context context, DatabaseManager db) {
        this.db = db;
        this.context = context;
        this.devices = new ArrayList<>();
        this.inflater = LayoutInflater.from(this.context);
    }

    @Override
    public void notifyDataSetChanged() {
        this.devices = this.db.getElements();
        super.notifyDataSetChanged();
    }

    public void addDevice(Device device) {
        //this.devices.add(device);
        this.db.addDevice(device);
        this.notifyDataSetChanged();
    }

    public void removeDevice(Device device) {
        this.db.removeDevice(device);
        this.notifyDataSetChanged();
    }



    public void updateDevice(int position, Device device) {
        this.devices.set(position, device);
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return this.devices.size();
    }

    @Override
    public Device getItem(int position) {
        return this.devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = this.inflater.inflate(R.layout.device_list, null);
        TextView textViewName = convertView.findViewById(R.id.device_name);
        TextView textViewIp = convertView.findViewById(R.id.device_ip);
        TextView textViewConnected = convertView.findViewById(R.id.device_connected);

        Device currentDevice = this.getItem(position);

        textViewName.setText(currentDevice.getName());
        textViewIp.setText(currentDevice.getIp());

        textViewConnected.setText(currentDevice.getLastUse());
        return convertView;
    }
}
