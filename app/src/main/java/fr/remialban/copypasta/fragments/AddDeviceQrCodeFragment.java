package fr.remialban.copypasta.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.os.Vibrator;
import android.provider.Settings;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import fr.remialban.copypasta.R;
import fr.remialban.copypasta.activities.CameraActivity;
import fr.remialban.copypasta.activities.SelectModeActivity;
import fr.remialban.copypasta.tools.FragmentInterface;
import fr.remialban.copypasta.tools.ScanHelper;

public class AddDeviceQrCodeFragment extends Fragment implements FragmentInterface {

    View view;
    PreviewView previewView;
    TextView textView;
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private AddDeviceQrCodeFragmentCallback mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_device_qr_code, container, false);

        initResources();
        init();

        return view;
    }

    private void initResources()
    {
        previewView = view.findViewById(R.id.previewView);
        textView = view.findViewById(R.id.text_result);
    }

    private void init()
    {

        if(checkPermission(Manifest.permission.CAMERA,getString(R.string.ask_permission_camera),2))
        {
            initCamera();
        }

    }

    private void initCamera()
    {
        cameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(getActivity()));

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
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(getActivity()), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = imageProxy.getImage();
                InputImage inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees);

                ScanHelper scan = new ScanHelper(getActivity(), inputImage, ScanHelper.CODE_SCAN_BARCODE, (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)) {
                    @Override
                    public void onSuccess(String text) {
                        try {
                            JSONObject json = new JSONObject(text);
                            mCallback.onQrCodeScanned(json.getString("name"), json.getString("ip"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setTitle(getString(R.string.add_device_error_qr_code));
                            alert.setPositiveButton("OK", null);
                            alert.show();
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

    public Boolean checkPermission(String permission, String contentMessage, int requestCode)
    {
        Boolean isEnable = false;
        if(ActivityCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED)
        {
            isEnable = true;
        } else {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission))
            {
                String[] permissions = {permission};
                ActivityCompat.requestPermissions(getActivity(), permissions, requestCode);
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(getActivity().getString(R.string.ask_permission_title));
                alert.setMessage(contentMessage);
                alert.setNeutralButton(getActivity().getString(R.string.ask_permission_cancel), null);
                alert.setPositiveButton(getActivity().getString(R.string.ask_permission_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, requestCode);
                    }
                });
                alert.show();
            }
        }
        return isEnable;
    }

    @Override
    public String getName() {
        return "AddDeviceQrCode";
    }

    @Override
    public int getTitleId() {
        return R.string.select_mode_barcode_button;
    }

    public interface AddDeviceQrCodeFragmentCallback {
        public void onQrCodeScanned(String name, String ip);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.createCallbackToParentActivity();
    }

    private void createCallbackToParentActivity(){
        try {
            //Parent activity will automatically subscribe to callback
            mCallback = (AddDeviceQrCodeFragmentCallback) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString()+ " must implement OnButtonClickedListener");
        }
    }

}