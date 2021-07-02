package fr.unrealsoftwares.copypasta.tools;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
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

    private Boolean advertLoad;

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
        advertLoad = false;
        Random random=new Random();
        int number = random.nextInt(100);
        int proportion = Meta.getAdvertProportion(activity);
        if(Meta.getVersion(activity).trim().equals("lite") && number <= proportion)
        {
            advertLoad = true;
            MobileAds.initialize(activity, initializationStatus -> {});
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(activity,key, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    isLoaded = true;
                    ad = interstitialAd;
                    if(isWantLoad)
                    {
                        showAdvert();
                    }
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
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
     * @see Callback
     */
    public void show(Callback callback)
    {
        this.callback = callback;
        if(Meta.getVersion(activity).trim().equals("pro") || !advertLoad)
        {
            callback.onAdvertLoaded();
            return;
        }
        if(isLoaded)
        {
            showAdvert();
        } else
        {
            isWantLoad = true;
        }
    }

    /**
     * Show the advert
     */
    public void showAdvert()
    {
        if(ad != null)
        {
            ad.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdDismissedFullScreenContent() {
                    callback.onAdvertLoaded();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    callback.onAdvertLoaded();
                }

            });
            ad.show(activity);
            isWantLoad = false;
        } else {
            callback.onAdvertLoaded();
        }
    }
}
