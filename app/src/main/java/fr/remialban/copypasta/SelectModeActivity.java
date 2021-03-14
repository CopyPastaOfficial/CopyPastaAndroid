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

import fr.remialban.copypasta.tools.Scan;

public class SelectModeActivity extends AppCompatActivity {

    String ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        ip = getIntent().getStringExtra("ip");
        Button button = findViewById(R.id.scan_qr_code_button);
        Button upload_image_button = findViewById(R.id.upload_image_button);
        if(!getIntent().getBooleanExtra("isLocally", false))
        {
            upload_image_button.setVisibility(View.VISIBLE);
        }
        upload_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, 4);
            }
        });
        findViewById(R.id.scan_qr_code_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                intent.putExtra("request", Scan.CODE_SCAN_BARCODE);
                startActivityForResult(intent, 1);
            }
        });
        findViewById(R.id.scan_text_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                intent.putExtra("request", Scan.CODE_SCAN_TEXT);
                startActivityForResult(intent, 1);
            }
        });
        findViewById(R.id.scan_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                intent.putExtra("request", Scan.CODE_SCAN_IMAGE);
                startActivityForResult(intent, 1);
            }
        });
        MaterialButton copyButton = findViewById(R.id.card_copy_button);
        TextView content = findViewById(R.id.card_content);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Scan of Copy Pasta app", content.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(SelectModeActivity.this, getString(R.string.select_mode_copy_success), Toast.LENGTH_LONG).show();
            }
        });

        MaterialButton shareButton = findViewById(R.id.card_share_button);
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
                Log.i("test","A");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("test","B");

                        try {
                            //lis l'ip depuis un fichier, à remplacer par ta méthode pour l'IP, le port ne change pas
                            Log.i("test","C");

                            Socket s = new Socket(ip, 8835);
                            Log.i("test","D");

                            OutputStream out = s.getOutputStream();
                            Log.i("test","E");

                            PrintWriter output = new PrintWriter(out);
                            Log.i("test","F");

                            output.print(data.getStringExtra("content"));
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
                        } catch (IOException e) {
                            Log.i("erreur", "test");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    alertErrorConnection();
                                }
                            });
                        } catch (Exception e) {
                            //Toast.makeText(SelectModeActivity.this, "Erreur le message n'a pas été envoyé", Toast.LENGTH_SHORT).show();
                            Log.i("erreur", "erreur");
                        }
                    }
                });
                thread.start();


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

                sendMessage(bytes);

                Log.d("MESSAGE","ALL BYTES ARE SENT");

            } catch (Exception e) {
                Log.i("TEST", "DANS EXCEPTION");
               // e.printStackTrace();

            }

        }
    }
    private void sendMessage(final byte[] msgbytes) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //lis l'ip depuis un fichier, à remplacer par ta méthode pour l'IP, le port ne change pas
                    Socket socket = new Socket(ip, 8836);

                    DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                    dOut.write(msgbytes);
                    dOut.close();
                    socket.close();

                } catch (java.io.IOException e) {
                    Log.i("erreur", "erreur");
                    runOnUiThread(new Runnable() {
                        public void run() {
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