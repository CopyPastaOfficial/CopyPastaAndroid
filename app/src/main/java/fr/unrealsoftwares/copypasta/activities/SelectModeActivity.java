package fr.unrealsoftwares.copypasta.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.text.HtmlCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.models.scans.TextScan;
import fr.unrealsoftwares.copypasta.tools.Advert;
import fr.unrealsoftwares.copypasta.tools.FileUtil;
import fr.unrealsoftwares.copypasta.tools.ScanHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
     * Button to upload file in the layout
     */
    private Button uploadFileButton;


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

    /**
     * Contains the json of the last scan to send at computer
     * @see fr.unrealsoftwares.copypasta.models.Scan
     */
    private String jsonLastScan;

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
        toolbar.setNavigationOnClickListener((e) -> {onBackPressed();});
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Init the elements from the layout
     */
    private void initResources()
    {
        scanQrCodeButton = findViewById(R.id.scan_qr_code_button);
        uploadFileButton = findViewById(R.id.upload_files_button);
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
        uploadFileButton.setOnClickListener(v -> {
            if(checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, SelectModeActivity.this.getString(R.string.ask_permission_storage), 1))
            {
                advert.show(this::uploadFile);
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
        sendButton.setOnClickListener(v -> sendMessage(jsonLastScan));

        sendClipboard.setOnClickListener(v -> {
            try {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                String clipboard = clipboardManager.getPrimaryClip().getItemAt(0).coerceToText(getApplicationContext()).toString();
                CardView cardView = findViewById(R.id.card);
                TextView content = findViewById(R.id.card_content);
                cardView.setVisibility(View.VISIBLE);
                content.setText(clipboard);
                textAlreadyScanned = true;
                TextScan textScan = new TextScan(getApplicationContext(), clipboard);
                jsonLastScan = textScan.getJson();
                sendMessage(jsonLastScan);
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
            uploadFileButton.setVisibility(View.VISIBLE);
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
                content.setText(HtmlCompat.fromHtml(savedInstanceState.getString("content"), HtmlCompat.FROM_HTML_MODE_COMPACT));
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
     * Open the gallery to upload file
     */
    private void uploadFile()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimes = {"image/*", "application/*", "audio/*", "text/*", "video/*", "font/*", "message/*", "multipart/*", "model/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimes);
        startActivityForResult(intent, 4);
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
            content.setText(HtmlCompat.fromHtml(data.getStringExtra("content"), HtmlCompat.FROM_HTML_MODE_COMPACT));
            textAlreadyScanned = true;
            jsonLastScan = data.getStringExtra("json");
            if(!getIntent().getBooleanExtra("isLocally", false))
            {
                sendMessage(data.getStringExtra("json"));
            }

        }
        if (requestCode == 4 && resultCode == RESULT_OK){

            Bitmap bitmap;
            try {
                assert data != null;
                List<Uri> files = new ArrayList<Uri>();
                ClipData clipData = data.getClipData();
                if (data.getClipData() != null)
                {
                    for(int i = 0; i < clipData.getItemCount(); i++)
                    {
                        ClipData.Item path = clipData.getItemAt(i);
                        files.add(path.getUri());


                    }
                } else
                {
                    files.add(data.getData());
                }

                sendFile(files);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Send message
     * @param json Text to send
     */
    private void sendMessage(String json)
    {
        progressBar.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);
        MediaType JSON = MediaType.get("application/json; charset=UTF-8");

        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", "Test");
            jsonObject.put("body", "Contenu");
            jsonObject.put("userId", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url("http://" + ip + ":21987/upload")
                //.url("https://jsonplaceholder.typicode.com/posts")
                .post(body)
                .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        sendButton.setEnabled(true);

                        alertErrorConnection();
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        sendButton.setEnabled(true);

                        if (!response.isSuccessful()) {
                            alertErrorConnection();
                        } else
                        {
                            Toast.makeText(SelectModeActivity.this, getString(R.string.select_mode_upload_scan_successful), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

    }

    /**
     * Send file at computer
     * @param files
     */
    private void sendFile(final List<Uri> files) throws IOException {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "uploadFiles")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.select_mode_upload_files_notification_title))
                .setProgress(0, 0, true)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager  notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.select_mode_upload_files_notification_channel_title);
            String description = getString(R.string.select_mode_upload_files_notification_channel_description);
            NotificationChannel channel = new NotificationChannel("uploadFiles", name, NotificationManager.IMPORTANCE_MIN);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1, notificationBuilder.build());

        progressBar.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
                //.addFormDataPart("title", "Square Logo");

        for (Uri uri:files) {
            String path;

            File file = FileUtil.from(getApplicationContext(), uri);

            builder.addFormDataPart("files", file.getName(),
                    RequestBody.create(MediaType.parse(getContentResolver().getType(uri)), file));
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url("http://" + ip + ":21987/upload")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);
                    notificationManager.cancelAll();
                    alertErrorConnection();

                });
                e.printStackTrace();

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);
                    notificationManager.cancelAll();

                    if (response.isSuccessful())
                    {
                        Toast.makeText(SelectModeActivity.this, getString(R.string.select_mode_upload_files_successful), Toast.LENGTH_SHORT).show();
                    } else
                    {
                        alertErrorConnection();
                    }
                });
                Log.i("DEBUG", String.valueOf(response.code()));
                Log.i("DEBUG", String.valueOf(response.isSuccessful()));
            }
        });
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
                        uploadFile();
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
    private String uriToFilename(Uri uri) {
        String path = null;

        if ((Build.VERSION.SDK_INT < 19) && (Build.VERSION.SDK_INT > 11)) {
            path = getRealPathFromURI_API11to18(this, uri);
        } else {
            path = getFilePath(this, uri);
        }

        return path;
    }

    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;
        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public String getFilePath(Context context, Uri uri) {
        String filePath = "";
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String[] splits = wholeID.split(":");
            if (splits.length == 2) {
                String id = splits[1];

                String[] column = {MediaStore.Images.Media.DATA};
                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        } else {
            filePath = uri.getPath();
        }
        return filePath;
    }
}