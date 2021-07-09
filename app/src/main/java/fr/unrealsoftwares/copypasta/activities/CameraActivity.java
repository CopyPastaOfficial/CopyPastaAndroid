package fr.unrealsoftwares.copypasta.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Size;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.tools.Advert;
import fr.unrealsoftwares.copypasta.models.Scan;
import fr.unrealsoftwares.copypasta.tools.ScanHelper;

public class CameraActivity extends AppCompatActivity {

    /**
     * Button to recognize image from gallery
     */
    ImageButton imageButton;

    /**
     * Preview contains the display of camera
     */
    PreviewView previewView;

    /**
     * Result of text recognition
     */
    TextView textResult;

    /**
     * Button to valid the recognition text result
     */
    ExtendedFloatingActionButton submitButton;

    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    /**
     * Toolbar of the activity
     */
    Toolbar toolbar;

    /**
     * Advert
     */
    Advert advert;

    /**
     * Raw contains of the scan to transmit in SelectModeActivity
     * @see Scan
     */
    String json;

    Scan scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initResources();
        init();
        initEvents();
    }

    private void initResources() {
        imageButton = findViewById(R.id.imageButton);
        previewView = findViewById(R.id.previewView);
        textResult = findViewById(R.id.text_result);
        submitButton = findViewById(R.id.submit_button);
        toolbar = findViewById(R.id.toolbar);
    }
    private void initEvents() {

        submitButton.setOnClickListener(v -> {
            Intent intent = getIntent();
            intent.putExtra("response", true);
            intent.putExtra("scan", scan);
            setResult(1, intent);
            finish();
        });
        imageButton.setOnClickListener(v -> {
            if(checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, CameraActivity.this.getString(R.string.ask_permission_storage), 1))
            {
                advert.show(this::openStorage);
            }
        });
    }
    private void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener((e) -> onBackPressed());
        switch (CameraActivity.this.getIntent().getIntExtra("request", -1))
        {
            case ScanHelper.CODE_SCAN_BARCODE:
                toolbar.setTitle(getString(R.string.select_mode_barcode_button));
                break;
            case ScanHelper.CODE_SCAN_IMAGE:
                toolbar.setTitle(getString(R.string.select_mode_object_button));
                break;
            case ScanHelper.CODE_SCAN_TEXT:
                toolbar.setTitle(getString(R.string.select_mode_text_button));
                break;
        }
        if(checkPermission(Manifest.permission.CAMERA,getString(R.string.ask_permission_camera),2)) {
            initCamera();
        }
    }

    private void openStorage()
    {
        Intent intent = new Intent(CameraActivity.this, ImageActivity.class);
        intent.putExtra("request", getIntent().getIntExtra("request", -1));
        startActivityForResult(intent, 1);
    }
    private void initCamera()
    {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));

    }

    private void bindPreview(ProcessCameraProvider cameraProvider)
    {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
            @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = imageProxy.getImage();
            InputImage inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees);

            ScanHelper scan = new ScanHelper(getApplicationContext(), inputImage, CameraActivity.this.getIntent().getIntExtra("request", -1), (Vibrator) getSystemService(Context.VIBRATOR_SERVICE)) {
                @Override
                public void onSuccess(Scan scan) {
                    CameraActivity.this.scan = scan;
                    String text = scan.getPlainText();
                    json = scan.getJson();
                    textResult.setText(text);
                    submitButton.setEnabled(true);
                    textResult.setTypeface(Typeface.DEFAULT);
                    if(text== null || text.trim().equals(""))
                    {
                        textResult.setText(getString(R.string.camera_message));
                        textResult.setTypeface(Typeface.DEFAULT_BOLD);
                        submitButton.setEnabled(false);
                    }
                    if(getIntent().getIntExtra("request", -1) == CODE_SCAN_BARCODE)
                    {
                        Intent intent = getIntent();
                        intent.putExtra("scan", (Parcelable) scan);
                        setResult(1, intent);
                        finish();
                    }
                }

                @Override
                public void onComplete(String text) {
                    imageProxy.close();
                }
            };

        });


        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1)
        {
            Intent intent = getIntent();
            intent.putExtra("scan", (Scan) data.getParcelableExtra("scan"));
            setResult(1, intent);
            finish();
        }
        if(requestCode == 2)
        {
            if(checkPermission(Manifest.permission.CAMERA, getString(R.string.ask_permission_camera),2))
            {
                initCamera();
            }
        }
    }

    public Boolean checkPermission(String permission, String contentMessage, int requestCode)
    {
        boolean isEnable = false;
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED)
        {
            isEnable = true;
        } else {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(CameraActivity.this, permission))
            {
                String[] permissions = {permission};
                ActivityCompat.requestPermissions(CameraActivity.this, permissions, requestCode);
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(CameraActivity.this);
                alert.setTitle(CameraActivity.this.getString(R.string.ask_permission_title));
                alert.setMessage(contentMessage);
                alert.setNeutralButton(CameraActivity.this.getString(R.string.ask_permission_cancel), null);
                alert.setPositiveButton(CameraActivity.this.getString(R.string.ask_permission_settings), (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", CameraActivity.this.getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, requestCode);
                });
                alert.show();
            }
        }
        return isEnable;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 2)
        {
            initCamera();
        }
        if(requestCode == 1)
        {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        openStorage();
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        advert = new Advert(this, getString(R.string.ad_image_button));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}