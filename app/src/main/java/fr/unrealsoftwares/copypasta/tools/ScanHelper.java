package fr.unrealsoftwares.copypasta.tools;

import android.content.Context;
import android.location.Address;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.unrealsoftwares.copypasta.R;
import fr.unrealsoftwares.copypasta.models.Scan;
import fr.unrealsoftwares.copypasta.models.scans.ContactScan;
import fr.unrealsoftwares.copypasta.models.scans.EmailScan;
import fr.unrealsoftwares.copypasta.models.scans.EventScan;
import fr.unrealsoftwares.copypasta.models.scans.IsbnScan;
import fr.unrealsoftwares.copypasta.models.scans.LocationScan;
import fr.unrealsoftwares.copypasta.models.scans.PhoneScan;
import fr.unrealsoftwares.copypasta.models.scans.SmsScan;
import fr.unrealsoftwares.copypasta.models.scans.TextScan;
import fr.unrealsoftwares.copypasta.models.scans.UrlScan;
import fr.unrealsoftwares.copypasta.models.scans.WifiScan;


public abstract class ScanHelper {


    public static final int CODE_SCAN_BARCODE = 1;
    public static final int CODE_SCAN_TEXT = 2;
    public static final int CODE_SCAN_IMAGE = 3;

    private final Vibrator vibrator;
    private final InputImage inputImage;
    private final Context context;

    /**
     * @param context Context or directly the activity
     * @param code Code contains the mode to scan. Constants declared as static in this class under the form CODE_SCAN_{MODE}
     * @param vibrator Represent the vibrator object which will use when a QR Code will scanned
     */
    public ScanHelper(Context context, InputImage inputImage, int code, Vibrator vibrator) {
        this.inputImage = inputImage;
        this.vibrator = vibrator;
        this.context = context;

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
        recognizer.process(this.inputImage)
                .addOnSuccessListener(text -> ScanHelper.this.onSuccess(new TextScan(context, text.getText())))
                .addOnCompleteListener(task -> ScanHelper.this.onComplete(task.getResult().getText()))
                ;
    }
    private void scanBarcode() {
        BarcodeScanner recognizer = BarcodeScanning.getClient();

        recognizer.process(this.inputImage)
                        .addOnSuccessListener(barcodes -> {
                            String result1 = "";
                            try {
                                Barcode barcode = barcodes.get(0);
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                {
                                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else
                                {
                                    vibrator.vibrate(500);
                                }
                                Scan scan;
                                barcode.getFormat();
                                switch (barcode.getValueType()) {
                                    case Barcode.TYPE_PHONE:
                                        result1 = barcode.getPhone().getNumber();
                                        scan = new PhoneScan(context, barcode.getPhone().getNumber());
                                        break;
                                    case Barcode.TYPE_EMAIL:
                                        result1 = context.getString(R.string.scan_qr_code_email_address) + "\n" + barcode.getEmail().getAddress() + "\n"+ context.getString(R.string.scan_qr_code_email_subject) + "\n" + barcode.getEmail().getSubject() + "\n" + context.getString(R.string.scan_qr_code_email_body) + "\n" + barcode.getEmail().getBody();
                                        scan = new EmailScan(context, barcode.getEmail().getAddress(), barcode.getEmail().getSubject(), barcode.getEmail().getBody());
                                        break;
                                    case Barcode.TYPE_WIFI:
                                        String encryption = "";
                                        switch (barcode.getWifi().getEncryptionType()) {
                                            case Barcode.WiFi.TYPE_OPEN:
                                                encryption = "OPEN";
                                                break;
                                            case Barcode.WiFi.TYPE_WEP:
                                                encryption = "WEP";
                                                break;
                                            case Barcode.WiFi.TYPE_WPA:
                                                encryption = "WPA/WPA2";
                                                break;
                                        }
                                        scan = new WifiScan(context, barcode.getWifi().getSsid(), barcode.getWifi().getPassword(), encryption);
                                        result1 = context.getString(R.string.scan_qr_code_wireless_ssid) + "\n" + barcode.getWifi().getSsid() + "\n";
                                        result1 += context.getString(R.string.scan_qr_code_wireless_encryption) + "\n" + encryption;
                                        if(barcode.getWifi().getEncryptionType() != Barcode.WiFi.TYPE_OPEN)
                                        {
                                            result1 += "\n" + context.getString(R.string.scan_qr_code_wireless_password) + "\n" + barcode.getWifi().getPassword();
                                        }
                                        break;
                                    case Barcode.TYPE_TEXT:
                                        result1 = barcode.getDisplayValue();
                                        scan = new TextScan(context, barcode.getDisplayValue());
                                        break;
                                    case Barcode.TYPE_CONTACT_INFO:
                                        Barcode.ContactInfo contact = barcode.getContactInfo();
                                        result1 = contact.getOrganization();
                                        result1 += "\n" + contact.getTitle();
                                        result1 += "\n" + contact.getName().getPronunciation() + " " + contact.getName().getFormattedName();
                                        result1 += "\n" + contact.getTitle();

                                        for (Barcode.Email email : contact.getEmails()) {
                                            result1 += "\n" + email.getAddress();
                                        }

                                        for (Barcode.Address address : contact.getAddresses()) {
                                            result1 += "\n" + address.getAddressLines();
                                        }

                                        for (Barcode.Phone phone : contact.getPhones()) {
                                            result1 += "\n" + phone.getNumber();
                                        }

                                        for (String url : contact.getUrls()) {
                                            result1 += "\n" + url;
                                        }

                                        scan = new ContactScan(context, contact.getOrganization(), contact.getTitle(), contact.getName().getFirst(), contact.getName().getLast());
                                        break;
                                    case Barcode.TYPE_ISBN:
                                        result1 = barcode.getRawValue();
                                        scan = new IsbnScan(context, barcode.getRawValue());
                                        break;
                                    case Barcode.TYPE_URL:
                                        result1 = barcode.getUrl().getUrl();
                                        scan = new UrlScan(context, barcode.getUrl().getUrl());
                                        break;
                                    case Barcode.TYPE_SMS:
                                        result1 = context.getString(R.string.scan_qr_code_sms_number)+ "\n";
                                        result1 += barcode.getSms().getPhoneNumber();
                                        result1 += "\n" + context.getString(R.string.scan_qr_code_sms_content)+"\n";
                                        result1 += barcode.getSms().getMessage();
                                        scan = new SmsScan(context, barcode.getSms().getPhoneNumber(), barcode.getSms().getMessage());
                                        break;
                                    case Barcode.TYPE_GEO:
                                        result1 = context.getString(R.string.scan_qr_code_location_lat) + " " + barcode.getGeoPoint().getLat();
                                        result1 += "\n" + context.getString(R.string.scan_qr_code_location_long) + " " + barcode.getGeoPoint().getLng();
                                        scan = new LocationScan(context, barcode.getGeoPoint().getLat(), barcode.getGeoPoint().getLng());
                                        break;
                                    case Barcode.TYPE_CALENDAR_EVENT:
                                        Barcode.CalendarEvent event = barcode.getCalendarEvent();
                                        result1 += "\n" + context.getString(R.string.scan_qr_code_event_summary) + " " + event.getSummary();
                                        result1 += "\n" + context.getString(R.string.scan_qr_code_event_description) + " " + event.getDescription();
                                        result1 += "\n" + context.getString(R.string.scan_qr_code_event_location) + " " + event.getLocation();
                                        result1 += "\n" + context.getString(R.string.scan_qr_code_event_start) + " " + event.getStart().getYear() + "-" + event.getStart().getMonth() + "-" + event.getStart().getDay() + " to " + event.getStart().getHours() + ":"+event.getStart().getMonth();
                                        result1 += "\n" + context.getString(R.string.scan_qr_code_event_end) + " " + event.getEnd().getYear() + "-" + event.getEnd().getMonth() + "-" + event.getEnd().getDay() + " to " + event.getEnd().getHours() + ":"+event.getEnd().getMonth();
                                        scan = new EventScan(context, event.getSummary(), event.getDescription(), event.getOrganizer(), event.getLocation(), EventScan.formatDate(event.getStart()), EventScan.formatDate(event.getEnd()));
                                        break;
                                    default:
                                        result1 = barcode.getRawValue();
                                        scan = new TextScan(context, barcode.getRawValue());
                                }

                                if ((barcode.getFormat() == Barcode.FORMAT_CODE_39) || (barcode.getFormat() == Barcode.FORMAT_EAN_8) || (barcode.getFormat() == Barcode.FORMAT_CODABAR) || (barcode.getFormat() == Barcode.FORMAT_CODE_93) || (barcode.getFormat() == Barcode.FORMAT_CODE_128) || (barcode.getFormat() == Barcode.FORMAT_CODE_93) || (barcode.getFormat() == Barcode.FORMAT_EAN_13) || (barcode.getFormat() == Barcode.FORMAT_ITF) || (barcode.getFormat() == Barcode.FORMAT_UPC_A) || (barcode.getFormat() == Barcode.FORMAT_UPC_E))
                                {
                                    scan = new IsbnScan(context, barcode.getRawValue());
                                }

                                //Pattern pattern = Pattern.compile("^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$");
                                //Pattern pattern = Pattern.compile("(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\\\".,<>?«»“”‘’]))");
                               // Pattern pattern = Pattern.compile("\"(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\\\".,<>?«»“”‘’]))\"");
                                Pattern pattern = Pattern.compile("^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$");
                                Matcher matcher = pattern.matcher(barcode.getRawValue());
                                if (matcher.find())
                                {
                                    scan = new UrlScan(context, barcode.getRawValue());
                                }
                                ScanHelper.this.onSuccess(scan);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        })
                        .addOnCompleteListener(task -> ScanHelper.this.onComplete(""))
                ;
    }
    private void scanImage() {

        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(inputImage)
                .addOnSuccessListener(labels -> {
                    try {
                        ImageLabel imageLabel = labels.get(0);
                        ScanHelper.this.onSuccess(new TextScan(context, imageLabel.getText()));
                    } catch (IndexOutOfBoundsException e)
                    {
                        e.printStackTrace();
                    }
                })
                .addOnCompleteListener(task -> ScanHelper.this.onComplete(""))
                .addOnFailureListener(e -> {
                    // Task failed with an exception
                    // ...
                });

    }
    public abstract void onSuccess(Scan scan);
    public abstract void onComplete(String text);

}
