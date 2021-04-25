package fr.unrealsoftwares.copypasta.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.fragments.AddDeviceFragment;
import fr.unrealsoftwares.copypasta.fragments.AddDeviceQrCodeFragment;
import fr.unrealsoftwares.copypasta.tools.FragmentInterface;
import fr.unrealsoftwares.copypasta.tools.ScanHelper;

public class AddDeviceActivity extends AppCompatActivity implements AddDeviceFragment.OnButtonClickedListener, AddDeviceQrCodeFragment.AddDeviceQrCodeFragmentCallback {

    Toolbar toolbar;
    FrameLayout frameLayout;
    AddDeviceFragment addDeviceFragment;
    AddDeviceQrCodeFragment addDeviceQrCodeFragment;
    FragmentInterface parentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        initResources();
        init(savedInstanceState);

    }

    private void initResources()
    {
        toolbar = findViewById(R.id.toolbar);
        frameLayout = findViewById(R.id.frame_layout);
    }

    private void init(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addDeviceFragment = new AddDeviceFragment();
        addDeviceQrCodeFragment = new AddDeviceQrCodeFragment();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        changeFragment(addDeviceFragment);
    }

    private void startCamera() {
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        intent.putExtra("request", ScanHelper.CODE_SCAN_BARCODE);
        intent.putExtra("addDevice", true);
        startActivityForResult(intent, 1);
    }

    private void changeFragment(FragmentInterface fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, (Fragment) fragment).commit();
        toolbar.setTitle(getString(fragment.getTitleId()));
        if(fragment.getName() == "AddDevice")
        {
            parentFragment = null;
        } else
        {
            parentFragment = addDeviceFragment;
        }
    }
    @Override
    public void onAddButtonClic(String name, String ip) {
        Intent intent = new Intent();
        intent.putExtra("name", name);
        intent.putExtra("ip", ip);
        setResult(1,intent);
        finish();
    }

    @Override
    public void onScanButtonClic() {
        changeFragment(addDeviceQrCodeFragment);
    }

    @Override
    public void onQrCodeScanned(String name, String ip) {
        addDeviceFragment.setName(name);
        addDeviceFragment.setIp(ip);
        changeFragment(addDeviceFragment);

    }

    @Override
    public void onBackPressed() {
        if(parentFragment == null)
        {
            super.onBackPressed();
        } else
        {
            changeFragment(parentFragment);
        }
    }
}