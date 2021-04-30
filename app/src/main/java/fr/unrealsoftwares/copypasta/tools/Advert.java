package fr.unrealsoftwares.copypasta.tools;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Random;

/**
 * Class to create a popup advert
 */
public class Advert {

    /**
     * Content the advert when it is loaded
     */
    private InterstitialAd ad;
    /**
     * Activity where is the advert
     */
    private final Activity activity;
    /**
     *
     */
    private Callback callback;

    /**
     * Is true when the advert is loaded, is false when the advert isn't loaded
     */
    private Boolean isLoaded;

    /**
     * Is true when the show method is called
     */
    private Boolean isWantLoad;

    /**
     *
     * @param activity Activity where is the advert
     * @param key Admob key
     */
    public Advert(Activity activity, String key)
    {
        this.activity = activity;
        ad = null;
        callback = null;
        isLoaded = false;
        isWantLoad = false;
        Random random=new Random();
        int number = random.nextInt(100);
        int proportion = Meta.getAdvertProportion(activity);
        Log.i("DEBUG", "IN ADVERT");
        Log.i("DEBUG", Meta.getVersion(activity));
        Log.i("DEBUG", String.valueOf(number));
        if(Meta.getVersion(activity).trim().equals("lite") && number <= proportion)
        {
            Log.i("DEBUG", "IN LITE");
            MobileAds.initialize(activity, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {}
            });
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(activity,key, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    Log.i("DEBUG", "IN ONADLOAD");

                    isLoaded = true;
                    ad = interstitialAd;
                    if(isWantLoad)
                    {
                        showAdvert();
                    }
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.i("DEBUG", "IN LOAD AD ERROR");

                    isLoaded = true;
                    ad = null;
                    if(isWantLoad)
                    {
                        callback.onAdvertLoaded();
                    }
                }
            });
        }
    }

    /**
     * Interface to create callback
     */
    public interface Callback
    {
        /**
         * Function called when the advert is closed
         */
        void onAdvertLoaded();
    }

    /**
     * Show the advert
     * @param callback
     * @see Callback
     */
    public void show(Callback callback)
    {
        Log.i("DEBUG", "SHOW");
        this.callback = callback;
        if(Meta.getVersion(activity).trim().equals("pro"))
        {
            Log.i("DEBUG", "IN PRO");
            callback.onAdvertLoaded();
            return;
        }
        if(isLoaded)
        {
            Log.i("DEBUG", "IN LOAD");
            showAdvert();
        } else
        {
            Log.i("DEBUG", "IN ELSE");
            isWantLoad = true;
        }
    }

    /**
     * Show the advert
     */
    public void showAdvert()
    {
        Log.i("DEBUG", "IN SHOW ADVERT");
        ad.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdDismissedFullScreenContent() {
                callback.onAdvertLoaded();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                callback.onAdvertLoaded();
            }

        });
        isWantLoad = false;
        ad.show(activity);
    }
}
