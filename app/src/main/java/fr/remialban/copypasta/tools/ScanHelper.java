package fr.remialban.copypasta.tools;

import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.util.List;

import javax.xml.transform.Result;

public abstract class ScanHelper {

    public static final int CODE_SCAN_BARCODE = 1;
    public static final int CODE_SCAN_TEXT = 2;
    public static final int CODE_SCAN_IMAGE = 3;

    private Vibrator vibrator;
    private InputImage inputImage;
    public ScanHelper(InputImage inputImage, int code, Vibrator vibrator) {
        this.inputImage = inputImage;
        this.vibrator = vibrator;
        switch (code) {
            case CODE_SCAN_TEXT:
                this.scanText();
                break;
            case CODE_SCAN_BARCODE:
                this.scanBarcode();
                break;
            case CODE_SCAN_IMAGE:
                this.scanImage();
                break;

        }
    }

    private void scanText() {
        TextRecognizer recognizer = TextRecognition.getClient();
        Task<Text> result =
                recognizer.process(this.inputImage)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {

                        ScanHelper.this.onSuccess(text.getText());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Text>() {
                    @Override
                    public void onComplete(@NonNull Task<Text> task) {
                        ScanHelper.this.onComplete(task.getResult().getText());
                    }
                })
                ;
    }
    private void scanBarcode() {
        BarcodeScanner recognizer = BarcodeScanning.getClient();

        Task<List<Barcode>> result =
                recognizer.process(this.inputImage)
                        .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                            @Override
                            public void onSuccess(List<Barcode> barcodes) {
                                String result = "";
                                try {
                                    Barcode barcode = barcodes.get(0);
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                    {
                                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                    } else
                                    {
                                        vibrator.vibrate(500);
                                    }
                                    switch (barcode.getValueType()) {
                                        case Barcode.TYPE_PHONE:
                                            result = barcode.getPhone().getNumber();
                                            break;
                                        case Barcode.TYPE_EMAIL:
                                            result = "Address : \n" + barcode.getEmail().getAddress() + "\n Subject :\n" + barcode.getEmail().getSubject() + "\n Content :\n" + barcode.getEmail().getBody();
                                            break;
                                        case Barcode.TYPE_WIFI:
                                            String encryption = "";
                                            switch (barcode.getWifi().getEncryptionType()) {
                                                case Barcode.WiFi.TYPE_OPEN:
                                                    encryption = "Open";
                                                    break;
                                                case Barcode.WiFi.TYPE_WEP:
                                                    encryption = "WEP";
                                                    break;
                                                case Barcode.WiFi.TYPE_WPA:
                                                    encryption = "WPA/WPA2/WPA3";
                                                    break;
                                            }
                                            result = "SSID : " + barcode.getWifi().getSsid();
                                            result += "\n Encryption : " + encryption + "\n Password : " + barcode.getWifi().getPassword();
                                            break;
                                        case Barcode.TYPE_TEXT:
                                            result = barcode.getDisplayValue();
                                            break;
                                        case Barcode.TYPE_CONTACT_INFO:
                                            Barcode.ContactInfo contact = barcode.getContactInfo();
                                            result = contact.getOrganization();
                                            result += "\n" + contact.getTitle();
                                            result += "\n" + contact.getName().getPronunciation() + " " + contact.getName().getFormattedName();
                                            result += "\n" + contact.getTitle();

                                            for (Barcode.Email email : contact.getEmails()) {
                                                result += "\n" + email.getAddress();
                                            }

                                            for (Barcode.Address address : contact.getAddresses()) {
                                                result += "\n" + address.getAddressLines();
                                            }

                                            for (Barcode.Phone phone : contact.getPhones()) {
                                                result += "\n" + phone.getNumber();
                                            }

                                            for (String url : contact.getUrls()) {
                                                result += "\n" + url;
                                            }
                                            break;
                                        case Barcode.TYPE_ISBN:
                                            result = barcode.getRawValue();
                                            break;
                                        case Barcode.TYPE_URL:
                                            result = barcode.getUrl().getUrl();
                                            break;
                                        case Barcode.TYPE_SMS:
                                            result = "Phone : \n";
                                            result += barcode.getSms().getPhoneNumber();
                                            result += "\nContent : \n";
                                            result += barcode.getSms().getMessage();
                                            break;
                                        case Barcode.TYPE_GEO:
                                            result = "Lat : " + barcode.getGeoPoint().getLat();
                                            result += "\n Long :" + barcode.getGeoPoint().getLng();
                                            break;
                                        case Barcode.TYPE_CALENDAR_EVENT:
                                            Barcode.CalendarEvent event = barcode.getCalendarEvent();
                                            result += "\n Summary : " + event.getSummary();
                                            result += "\n Description : " + event.getDescription();
                                            result += "\n Location : " + event.getLocation();
                                            result += "\n Start : " + event.getStart().getYear() + "-" + event.getStart().getMonth() + "-" + event.getStart().getDay() + " to " + event.getStart().getHours() + ":"+event.getStart().getMonth();
                                            result += "\n End : " + event.getEnd().getYear() + "-" + event.getEnd().getMonth() + "-" + event.getEnd().getDay() + " to " + event.getEnd().getHours() + ":"+event.getEnd().getMonth();
                                        default:
                                            result = barcode.getRawValue();
                                    }
                                    ScanHelper.this.onSuccess(result);

                                } catch (Exception e) {

                                }


                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                            @Override
                            public void onComplete(@NonNull Task<List<Barcode>> task) {
                                ScanHelper.this.onComplete("");
                            }
                        })
                ;
    }
    private void scanImage() {

        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        ImageLabel imageLabel = labels.get(0);

                        ScanHelper.this.onSuccess(imageLabel.getText());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<List<ImageLabel>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<ImageLabel>> task) {
                        ScanHelper.this.onComplete("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });

    }
    public abstract void onSuccess(String text);
    public abstract void onComplete(String text);

}
