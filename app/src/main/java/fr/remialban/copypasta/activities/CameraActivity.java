package fr.remialban.copypasta.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;

import fr.remialban.copypasta.R;
import fr.remialban.copypasta.tools.ScanHelper;

public class CameraActivity extends AppCompatActivity {

    CoordinatorLayout layout;
    ImageButton imageButton;
    PreviewView previewView;
    TextView textResult;
    ExtendedFloatingActionButton submitButton;
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initResources();
        init();
        initEvents();
    }

    private void initResources() {
        imageButton = findViewById(R.id.imageButton);
        previewView = findViewById(R.id.previewView);
        textResult = findViewById(R.id.text_result);
        submitButton = findViewById(R.id.submit_button);
        layout = findViewById(R.id.layout);
    }
    private void initEvents() {

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("response", true);
                intent.putExtra("content", textResult.getText());
                setResult(1, intent);
                finish();
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, ImageActivity.class);
                intent.putExtra("request", getIntent().getIntExtra("request", -1));
                startActivityForResult(intent, 1);
            }
        });
    }
    private void init() {
        checkPermission();
        Button button = findViewById(R.id.manual_button);

        if(getIntent().getBooleanExtra("addDevice", false))
        {
            submitButton.setVisibility(View.INVISIBLE);
            button.setVisibility(View.VISIBLE);
            textResult.setText(getString(R.string.add_device_scan_text));
            textResult.setTypeface(Typeface.DEFAULT);
        } else {
            button.setHeight(0);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                intent.putExtra("response", true);
                intent.putExtra("content", textResult.getText());
                setResult(2, intent);
                finish();
            }
        });
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
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = imageProxy.getImage();
                InputImage inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees);

                ScanHelper scan = new ScanHelper(getApplicationContext(), inputImage, CameraActivity.this.getIntent().getIntExtra("request", -1), (Vibrator) getSystemService(Context.VIBRATOR_SERVICE)) {
                    @Override
                    public void onSuccess(String text) {
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
                            intent.putExtra("content", textResult.getText());
                            setResult(1, intent);
                            finish();
                        }
                    }

                    @Override
                    public void onComplete(String text) {
                        imageProxy.close();
                    }
                };

            }
        });


        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1)
        {
            Intent intent = getIntent();
            intent.putExtra("content", data.getStringExtra("content"));
            setResult(1, intent);
            finish();
        }
        if(requestCode == 2)
        {
            checkPermission();
        }
    }

    private void checkPermission() {
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            initCamera();
        } else {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(CameraActivity.this, Manifest.permission.CAMERA))
            {
                String[] permissions = {Manifest.permission.CAMERA};
                ActivityCompat.requestPermissions(CameraActivity.this, permissions, 2);

            } else {
                Snackbar snackbar = Snackbar.make(layout, getString(R.string.camera_ask_permission_camera), Snackbar.LENGTH_LONG);
                snackbar.setAction(getString(R.string.camera_ask_permission_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", CameraActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 2);
                    }
                });
                snackbar.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 2)
        {
            initCamera();
        }
        if(requestCode == 3)
        {

        }
    }
}