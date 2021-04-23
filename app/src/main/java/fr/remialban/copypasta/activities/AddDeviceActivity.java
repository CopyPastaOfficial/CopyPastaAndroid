package fr.remialban.copypasta.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import fr.remialban.copypasta.R;
import fr.remialban.copypasta.fragments.AddDeviceFragment;
import fr.remialban.copypasta.fragments.AddDeviceQrCodeFragment;
import fr.remialban.copypasta.tools.FragmentInterface;
import fr.remialban.copypasta.tools.ScanHelper;

public class AddDeviceActivity extends AppCompatActivity implements AddDeviceFragment.OnButtonClickedListener, AddDeviceQrCodeFragment.AddDeviceQrCodeFragmentCallback {

    Toolbar toolbar;
    FrameLayout frameLayout;
    Boolean cameraEnable;
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
        /*cameraEnable = true;

        if(savedInstanceState != null)
        {
            cameraEnable = savedInstanceState.getBoolean("cameraEnable");
            nameInput.setText(savedInstanceState.getString("name"));
            ipInput.setText(savedInstanceState.getString("ip"));
        }
        if(cameraEnable)
        {
            startCamera();
        }*/
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

    /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1)
        {
            cameraEnable = false;
            if(data.getBooleanExtra("response", false))
            {
                return;
            }
            try {
                String jsonText = data.getStringExtra("content");
                JSONObject json = new JSONObject(jsonText);

                String name = json.getString("name");
                String ip = json.getString("ip");
                nameInput.setText(json.getString("name"));
                ipInput.setText(ip);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, getText(R.string.add_device_error_qr_code), Toast.LENGTH_LONG).show();
                startCamera();
            }
            return;
        } if(resultCode != 2) {
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("name", nameInput.getText().toString());
        outState.putString("ip", ipInput.getText().toString());
        outState.putBoolean("cameraEnable", cameraEnable);
    }*/
}