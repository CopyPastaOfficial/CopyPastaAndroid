package fr.unrealsoftwares.copypasta.activities;

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

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.adapters.DevicesAdapter;
import fr.unrealsoftwares.copypasta.models.Device;
import fr.unrealsoftwares.copypasta.tools.Advert;
import fr.unrealsoftwares.copypasta.tools.DatabaseManager;

public class SelectDeviceActivity extends AppCompatActivity {

    /**
     * Device adapter, it is to manage the list view
     */
    private DevicesAdapter adapter;

    private DatabaseManager databaseManager;

    /**
     * ListView in the layout, contains the list of the devices
     */
    private ListView devices_list;

    /**
     * Button in the layout to add a device
     */
    private Button addDeviceButton;

    /**
     * Button in the layout to use the app locally
     */
    private Button localButton;

    /**
     * Toolbar in the layout
     */
    private Toolbar toolbar;
    
    private Advert advertLocalButton;

    private Advert advertAddButton;

    private  Advert advertOnClickDeviceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);

        initToolbar();
        initResources();
        init();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        advertLocalButton = new Advert(this , getString(R.string.ad_local_button));
        advertAddButton = new Advert(this, getString(R.string.ad_add_device));
        advertOnClickDeviceButton = new Advert(this, getString(R.string.ad_select_device));
    }

    private void initToolbar()
    {
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Init the elements from the layout
     */
    private void initResources() {
        this.devices_list = findViewById(R.id.devices_list);
        this.addDeviceButton = findViewById(R.id.add_device_button);
        this.localButton = findViewById(R.id.local_button);
    }

    /**
     * Initialisation
     */
    private void init() {
        this.databaseManager = new DatabaseManager(getApplicationContext());
        this.adapter = new DevicesAdapter(getApplicationContext(), this.databaseManager);
        this.adapter.notifyDataSetChanged();
        devices_list.setAdapter(this.adapter);
    }

    /**
     * Init the events of the elements contain in the layout
     */
    private void initEvents() {
        devices_list.setOnItemLongClickListener(this.listOnItemLongClickListener());
        devices_list.setOnItemClickListener(this.listOnItemClickListener());
        addDeviceButton.setOnClickListener(this.addDeviceClick());
        localButton.setOnClickListener(v -> advertLocalButton.show(() -> {
            Intent intent = new Intent(getApplicationContext(), SelectModeActivity.class);
            intent.putExtra("isLocally", true);
            startActivityForResult(intent, 1);
        }));
    }

    /**
     * When clicking on a item of ListView
     */
    private AdapterView.OnItemClickListener listOnItemClickListener() {
        return (parent, view, position, id) -> advertOnClickDeviceButton.show(() -> {
            Device device = adapter.getItem(position);
            device.lastUse();
            databaseManager.updateDevice(device);
            Intent intent = new Intent(getApplicationContext(), SelectModeActivity.class);
            intent.putExtra("name", device.getName());
            intent.putExtra("ip", device.getIp());
            startActivityForResult(intent, 1);
        });
    }

    /**
     * After a long click of ListView
     */
    private AdapterView.OnItemLongClickListener listOnItemLongClickListener() {
        return (parent, view, position, id) -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(SelectDeviceActivity.this);
            alert.setTitle(SelectDeviceActivity.this.getString(R.string.select_mode_alert_remove_title));
            alert.setNegativeButton(SelectDeviceActivity.this.getString(R.string.select_mode_alert_remove_cancel), null);
            alert.setPositiveButton(SelectDeviceActivity.this.getString(R.string.select_mode_alert_remove_validate), (dialog, which) -> adapter.removeDevice(adapter.getItem(position)));
            alert.show();
            return true;
        };
    }

    /**
     * When clicking of the add device button
     */
    private View.OnClickListener addDeviceClick() {
        return v -> advertAddButton.show(() -> {
            Intent add_device = new Intent(SelectDeviceActivity.this, AddDeviceActivity.class);
            startActivityForResult(add_device, 1);
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            assert data != null;
            String name = data.getStringExtra("name");
            String ip = data.getStringExtra("ip");
            this.adapter.addDevice(new Device(name, ip));
        }
        this.adapter.notifyDataSetChanged();
    }
}