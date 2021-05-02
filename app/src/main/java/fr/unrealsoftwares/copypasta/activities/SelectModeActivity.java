package fr.unrealsoftwares.copypasta.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.tools.Advert;
import fr.unrealsoftwares.copypasta.tools.ScanHelper;

public class SelectModeActivity extends AppCompatActivity {

    /**
     * IP of the last scan
     */
    private String ip;

    /**
     * Is true if the app is run locally, if false if this is the opposite
     */
    private Boolean isLocally;

    /**
     * Progress bar in the layout to show during sending the content to the computer
     */
    private ProgressBar progressBar;

    /**
     * Button to scan QR Code in the layout
     */
    private Button scanQrCodeButton;

    /**
     * Button to upload image in the layout
     */
    private Button uploadImageButton;

    /**
     * Button to recognize text in the layout
     */
    private Button scanTextButton;

    /**
     * Button to recognize object in the layout
     */
    private Button scanObjectButton;

    /**
     * Button to send clipboard in the layout
     */
    private Button sendClipboard;

    /**
     * Button in the CardView to copy button
     */
    private MaterialButton copyButton;

    /**
     * TextView in the CardView contains the last scanned text
     */
    private TextView content;

    /**
     * Button in the CardView to share the last scanned text
     */
    private MaterialButton shareButton;

    /**
     * Button in the CardView to resend the last scanned text
     */
    private MaterialButton sendButton;

    /**
     * Is true if a text is already scanned. Is false if this is the opposite
     */
    private Boolean textAlreadyScanned;

    /**it
     * Activity toolbar
     */
    private Toolbar toolbar;

    /**
     * Advert
     */
    private Advert advert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        initToolbar();
        initResources();
        initEvents();
        init(savedInstanceState);
    }

    /**
     * Init toolbar
     */
    private void initToolbar()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Init the elements from the layout
     */
    private void initResources()
    {
        scanQrCodeButton = findViewById(R.id.scan_qr_code_button);
        uploadImageButton = findViewById(R.id.upload_image_button);
        scanTextButton = findViewById(R.id.scan_text_button);
        scanObjectButton = findViewById(R.id.scan_object_button);
        copyButton = findViewById(R.id.card_copy_button);
        content = findViewById(R.id.card_content);
        shareButton = findViewById(R.id.card_share_button);
        sendButton = findViewById(R.id.card_send_button);
        progressBar = findViewById(R.id.progress_bar);
        sendClipboard = findViewById(R.id.send_clipboard_button);
    }

    /**
     * Init events
     */
    private void initEvents()
    {
        uploadImageButton.setOnClickListener(v -> {
            if(checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, SelectModeActivity.this.getString(R.string.ask_permission_storage), 1))
            {
                advert.show(this::uploadImage);
            }
        });

        scanQrCodeButton.setOnClickListener(new OpenCamera(ScanHelper.CODE_SCAN_BARCODE));

        scanTextButton.setOnClickListener(new OpenCamera( ScanHelper.CODE_SCAN_TEXT));
        scanObjectButton.setOnClickListener(new OpenCamera( ScanHelper.CODE_SCAN_IMAGE));

        copyButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Scan of Copy Pasta app", content.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(SelectModeActivity.this, getString(R.string.select_mode_copy_success), Toast.LENGTH_LONG).show();
        });
        sendButton.setOnClickListener(v -> sendMessage(
                content.getText().toString()));

        sendClipboard.setOnClickListener(v -> {
            try {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                String clipboard = clipboardManager.getPrimaryClip().getItemAt(0).coerceToText(getApplicationContext()).toString();
                CardView cardView = findViewById(R.id.card);
                TextView content = findViewById(R.id.card_content);
                cardView.setVisibility(View.VISIBLE);
                content.setText(clipboard);
                textAlreadyScanned = true;
                sendMessage(clipboard);
            } catch (NullPointerException exception) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SelectModeActivity.this);
                alert.setTitle(getString(R.string.select_mode_error_clipboard_title));
                alert.setMessage(getString(R.string.select_mode_error_clipboard_content));
                alert.setPositiveButton("OK", null);
                alert.show();
            }

        });

        shareButton.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, content.getText());
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });
    }

    private void init(Bundle savedInstanceState)
    {
        ip = getIntent().getStringExtra("ip");
        isLocally = getIntent().getBooleanExtra("isLocally", false);

        textAlreadyScanned = false;

        if(!isLocally)
        {
            uploadImageButton.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.VISIBLE);
            sendClipboard.setVisibility(View.VISIBLE);
        }

        if(savedInstanceState != null)
        {
            if(savedInstanceState.getBoolean("textAlreadyScanned"))
            {
                CardView cardView = findViewById(R.id.card);
                TextView content = findViewById(R.id.card_content);
                cardView.setVisibility(View.VISIBLE);
                textAlreadyScanned = true;
                content.setText(savedInstanceState.getString("content"));
            }
        }
    }

    /**
     * Class to open camera
     */
    class OpenCamera implements View.OnClickListener
    {
        private final int request;
        public OpenCamera(int request)
        {
            this.request = request;
        }
        @Override
        public void onClick(View v) {
            advert.show(() -> {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                intent.putExtra("request", request);
                startActivityForResult(intent, 1);
            });
        }
    }

    /**
     * Open the gallery to upload image
     */
    private void uploadImage()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 4);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1)
        {
            CardView cardView = findViewById(R.id.card);
            TextView content = findViewById(R.id.card_content);
            cardView.setVisibility(View.VISIBLE);
            assert data != null;
            content.setText(data.getStringExtra("content"));
            textAlreadyScanned = true;
            if(!getIntent().getBooleanExtra("isLocally", false))
            {
                sendMessage(data.getStringExtra("content"));


            }

        }
        if (requestCode == 4 && resultCode == RESULT_OK){

            Bitmap bitmap;
            try {
                assert data != null;
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();

                sendImage(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Send message
     * @param content Text to send
     */
    private void sendMessage(String content)
    {
        Thread thread = new Thread(() -> {

            try {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.VISIBLE);
                    sendButton.setEnabled(false);

                });

                Socket s = new Socket(ip, 8835);

                OutputStream out = s.getOutputStream();

                PrintWriter output = new PrintWriter(out);
                output.print(content);
                output.flush();

                output.close();
                out.close();
                s.close();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);

                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);

                    alertErrorConnection();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);
                });
            }
        });
        thread.start();
    }

    /**
     * Send image
     * @param msgbytes Image to send
     */
    private void sendImage(final byte[] msgbytes) {
        Thread thread = new Thread(() -> {
            try {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.VISIBLE);
                    sendButton.setEnabled(false);
                });
                Socket socket = new Socket(ip, 8836);

                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                dOut.write(msgbytes);
                dOut.close();
                socket.close();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);

                });

            } catch (IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);
                    alertErrorConnection();
                });
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

    }

    /**
     * Called when there is a connection error
     */
    private void alertErrorConnection()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(SelectModeActivity.this);
        alert.setTitle(getString(R.string.select_mode_error_server_title));
        alert.setMessage(getString(R.string.select_mode_error_server_content));
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("content", content.getText().toString());
        outState.putBoolean("textAlreadyScanned", textAlreadyScanned);
        super.onSaveInstanceState(outState);
    }

    public Boolean checkPermission(String permission, String contentMessage, int requestCode)
    {
        boolean isEnable = false;
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED)
        {
            isEnable = true;
        } else {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(SelectModeActivity.this, permission))
            {
                String[] permissions = {permission};
                ActivityCompat.requestPermissions(SelectModeActivity.this, permissions, requestCode);
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(SelectModeActivity.this);
                alert.setTitle(SelectModeActivity.this.getString(R.string.ask_permission_title));
                alert.setMessage(contentMessage);
                alert.setNeutralButton(SelectModeActivity.this.getString(R.string.ask_permission_cancel), null);
                alert.setPositiveButton(SelectModeActivity.this.getString(R.string.ask_permission_settings), (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", SelectModeActivity.this.getPackageName(), null);
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
        if(requestCode == 1)
        {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        uploadImage();
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        advert = new Advert(this, getString(R.string.ad_upload_image_button));
    }
}