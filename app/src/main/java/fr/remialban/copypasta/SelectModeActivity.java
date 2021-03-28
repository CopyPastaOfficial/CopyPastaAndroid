package fr.remialban.copypasta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import fr.remialban.copypasta.tools.ScanHelper;

public class SelectModeActivity extends AppCompatActivity {

    String ip;
    Boolean isLocally;
    ProgressBar progressBar;
    Button scanQrCodeButton;
    Button uploadImageButton;
    Button scanTextButton;
    Button scanObjectButton;
    Button sendClipboard;

    MaterialButton copyButton;
    TextView content;
    MaterialButton shareButton;
    MaterialButton sendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
        initRessources();
        initEvents();
        init();

    }

    private void initRessources()
    {
        scanQrCodeButton = findViewById(R.id.scan_qr_code_button);
        uploadImageButton = findViewById(R.id.upload_image_button);
        scanTextButton = findViewById(R.id.scan_text_button);
        scanObjectButton = findViewById(R.id.scan_object_button);
        copyButton = findViewById(R.id.card_copy_button);
        content = findViewById(R.id.card_content);
        shareButton = findViewById(R.id.card_share_button);
        ip = getIntent().getStringExtra("ip");
        isLocally = getIntent().getBooleanExtra("isLocally", false);
        sendButton = findViewById(R.id.card_send_button);
        progressBar = findViewById(R.id.progress_bar);
        sendClipboard = findViewById(R.id.send_clipboard_button);
    }

    private void initEvents()
    {
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, 4);
            }
        });

        scanQrCodeButton.setOnClickListener(new OpenCamera(ScanHelper.CODE_SCAN_BARCODE));

        scanTextButton.setOnClickListener(new OpenCamera( ScanHelper.CODE_SCAN_TEXT));
        scanObjectButton.setOnClickListener(new OpenCamera( ScanHelper.CODE_SCAN_IMAGE));

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Scan of Copy Pasta app", content.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(SelectModeActivity.this, getString(R.string.select_mode_copy_success), Toast.LENGTH_LONG).show();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(
                        content.getText().toString());
            }
        });

        sendClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                    String clipboard = (String) clipboardManager.getPrimaryClip().getItemAt(0).coerceToText(getApplicationContext()).toString();
                    CardView cardView = findViewById(R.id.card);
                    TextView content = findViewById(R.id.card_content);
                    cardView.setVisibility(View.VISIBLE);
                    content.setText(clipboard);
                    sendMessage(clipboard);
                } catch (NullPointerException exception) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(SelectModeActivity.this);
                    alert.setTitle(getString(R.string.select_mode_error_clipboard_title));
                    alert.setMessage(getString(R.string.select_mode_error_clipboard_content));
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.show();
                }

            }
        });
    }



    class OpenCamera implements View.OnClickListener
    {
        private int request;
        public OpenCamera(int request)
        {
            this.request = request;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            intent.putExtra("request", request);
            startActivityForResult(intent, 1);
        }
    }
    private void init()
    {

        if(!isLocally)
        {
            uploadImageButton.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.VISIBLE);
            sendClipboard.setVisibility(View.VISIBLE);
        }


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, content.getText());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1)
        {
            CardView cardView = findViewById(R.id.card);
            TextView content = findViewById(R.id.card_content);
            cardView.setVisibility(View.VISIBLE);
            content.setText(data.getStringExtra("content"));
            //Toast.makeText(getApplicationContext(),data.getStringExtra("content"), Toast.LENGTH_LONG).show();
            if(!getIntent().getBooleanExtra("isLocally", false))
            {
                sendMessage(data.getStringExtra("content"));


            }

        }
        if (requestCode == 4 && resultCode == RESULT_OK){

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                StringBuilder b = new StringBuilder();
                byte[] bytes = stream.toByteArray();

                sendImage(bytes);

                Log.d("MESSAGE","ALL BYTES ARE SENT");

            } catch (Exception e) {
                Log.i("TEST", "DANS EXCEPTION");
               // e.printStackTrace();

            }

        }
    }

    private void sendMessage(String content)
    {
        Log.i("test","A");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("test","B");

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                            sendButton.setEnabled(false);

                        }
                    });
                    //lis l'ip depuis un fichier, à remplacer par ta méthode pour l'IP, le port ne change pas
                    Log.i("test","C");

                    Socket s = new Socket(ip, 8835);
                    Log.i("test","D");

                    OutputStream out = s.getOutputStream();
                    Log.i("test","E");

                    PrintWriter output = new PrintWriter(out);
                    Log.i("test","F");

                    output.print(content);
                    Log.i("test","G");

                    output.flush();
                    Log.i("test","H");

                    Log.d("MSG", "msg sent");
                    Log.i("test","I");


                    /*if(st.contains("OK")){
                    }*/
                    Toast.makeText(getApplicationContext(), "Scan sent !", Toast.LENGTH_SHORT).show();

                    output.close();
                    out.close();
                    s.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            sendButton.setEnabled(true);

                        }
                    });
                } catch (IOException e) {
                    Log.i("erreur", "test");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            sendButton.setEnabled(true);

                            alertErrorConnection();
                        }
                    });
                } catch (Exception e) {
                    //Toast.makeText(SelectModeActivity.this, "Erreur le message n'a pas été envoyé", Toast.LENGTH_SHORT).show();
                    Log.i("erreur", "erreur");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            sendButton.setEnabled(true);

                        }
                    });
                }
            }
        });
        thread.start();
    }
    private void sendImage(final byte[] msgbytes) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                            sendButton.setEnabled(false);
                        }
                    });
                    //lis l'ip depuis un fichier, à remplacer par ta méthode pour l'IP, le port ne change pas
                    Socket socket = new Socket(ip, 8836);

                    DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                    dOut.write(msgbytes);
                    dOut.close();
                    socket.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            sendButton.setEnabled(true);

                        }
                    });

                } catch (java.io.IOException e) {
                    Log.i("erreur", "erreur");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            sendButton.setEnabled(true);
                            alertErrorConnection();
                        }
                    });

                    //Toast.makeText(getApplicationContext(), "Erreur le message n'a pas été envoyé", Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    private void alertErrorConnection()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(SelectModeActivity.this);
        alert.setTitle(getString(R.string.select_mode_error_server_title));
        alert.setMessage(getString(R.string.select_mode_error_server_content));
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
            }
        });
        alert.show();
    }



}