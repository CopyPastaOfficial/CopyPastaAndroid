package fr.remialban.copypasta.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import fr.remialban.copypasta.R;
import fr.remialban.copypasta.tools.ScanHelper;

public class AddDeviceActivity extends AppCompatActivity {

    EditText nameInput;
    EditText ipInput;
    Button addButton;
    ExtendedFloatingActionButton scanButton;
    Boolean cameraEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        initResources();
        init(savedInstanceState);
        initEvents();
    }
    private void initResources() {
        ipInput = findViewById(R.id.input_ip);
        nameInput = findViewById(R.id.input_name);
        addButton = findViewById(R.id.add_button);
        scanButton = findViewById(R.id.scan_button);
    }

    private void initEvents() {
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /** Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                intent.putExtra("request", Scan.CODE_SCAN_BARCODE);
                startActivityForResult(intent, 1);*/
               startCamera();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean fieldsAreCorrect = true;
                if(ipInput.getText().toString().trim().equals(""))
                {
                    ipInput.setError(AddDeviceActivity.this.getString(R.string.add_device_error));
                    fieldsAreCorrect = false;
                    ipInput.requestFocus();
                }
                if(nameInput.getText().toString().trim().equals(""))
                {
                    nameInput.setError(AddDeviceActivity.this.getString(R.string.add_device_error));
                    fieldsAreCorrect = false;
                    nameInput.requestFocus();
                }
                if(fieldsAreCorrect)
                {
                    Intent intent = new Intent();
                    intent.putExtra("name", nameInput.getText().toString());
                    intent.putExtra("ip", ipInput.getText().toString());
                    setResult(1,intent);
                    finish();
                }
            }
        });
    }
    private void init(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cameraEnable = true;

        if(savedInstanceState != null)
        {
            cameraEnable = savedInstanceState.getBoolean("cameraEnable");
            nameInput.setText(savedInstanceState.getString("name"));
            ipInput.setText(savedInstanceState.getString("ip"));
        }
        if(cameraEnable)
        {
            startCamera();
        }
    }

    private void startCamera() {
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        intent.putExtra("request", ScanHelper.CODE_SCAN_BARCODE);
        intent.putExtra("addDevice", true);
        startActivityForResult(intent, 1);
    }
    @Override
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
    }
}