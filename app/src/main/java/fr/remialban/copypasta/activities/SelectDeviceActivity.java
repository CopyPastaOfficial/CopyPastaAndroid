package fr.remialban.copypasta.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import fr.remialban.copypasta.R;
import fr.remialban.copypasta.adapters.DevicesAdapter;
import fr.remialban.copypasta.models.Device;
import fr.remialban.copypasta.tools.DatabaseManager;

public class SelectDeviceActivity extends AppCompatActivity {

    DevicesAdapter adapter;
    DatabaseManager databaseManager;

    ListView devices_list;
    Button addDeviceButton;
    Button localButton;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);

        initResources();
        init();
        initEvents();
    }

    private void init() {
        setSupportActionBar(toolbar);
        this.databaseManager = new DatabaseManager(getApplicationContext());
        this.adapter = new DevicesAdapter(getApplicationContext(), this.databaseManager);
        this.adapter.notifyDataSetChanged();
        devices_list.setAdapter(this.adapter);
    }
    private void initEvents() {
        devices_list.setOnItemLongClickListener(this.listOnItemLongClickListener());
        devices_list.setOnItemClickListener(this.listOnItemClickListener());
        addDeviceButton.setOnClickListener(this.addDeviceClic());
        localButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectModeActivity.class);
                intent.putExtra("isLocally", true);
                startActivityForResult(intent, 1);
            }
        });
    }
    private void initResources() {
        this.devices_list = findViewById(R.id.devices_list);
        this.addDeviceButton = findViewById(R.id.add_device_button);
        this.localButton = findViewById(R.id.local_button);
        this.toolbar = findViewById(R.id.toolbar);
    }
    private AdapterView.OnItemClickListener listOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = adapter.getItem(position);
                device.lastUse();
                databaseManager.updateDevice(device);
                Intent intent = new Intent(getApplicationContext(), SelectModeActivity.class);
                intent.putExtra("name", device.getName());
                intent.putExtra("ip", device.getIp());
                startActivityForResult(intent, 1);
            }
        };
    }

    private AdapterView.OnItemLongClickListener listOnItemLongClickListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SelectDeviceActivity.this);
                alert.setTitle(SelectDeviceActivity.this.getString(R.string.select_mode_alert_remove_title));
                alert.setNegativeButton(SelectDeviceActivity.this.getString(R.string.select_mode_alert_remove_cancel), null);
                alert.setPositiveButton(SelectDeviceActivity.this.getString(R.string.select_mode_alert_remove_validate), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.removeDevice(adapter.getItem(position));
                    }
                });
                alert.show();
                return true;
            }
        };
    }

    private View.OnClickListener addDeviceClic() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_device = new Intent(SelectDeviceActivity.this, AddDeviceActivity.class);
                startActivityForResult(add_device, 1);
            }
        };
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            String name = data.getStringExtra("name");
            String ip = data.getStringExtra("ip");
            this.adapter.addDevice(new Device(name, ip));
        }
        this.adapter.notifyDataSetChanged();
    }
}