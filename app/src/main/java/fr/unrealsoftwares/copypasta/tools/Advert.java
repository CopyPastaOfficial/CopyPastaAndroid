package fr.unrealsoftwares.copypasta.tools;

import android.app.Activity;

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

public abstract class Advert {

    InterstitialAd ad;
    Activity activity;

    public Advert(Activity context, String key)
    {
        activity = context;
        ad = null;
        Random random=new Random();
        int number = random.nextInt(100);
        int proportion = Meta.getAdvertProportion(context);
        if(Meta.getVersion(context).trim().equals("lite") && number <= proportion)
        {
            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {}
            });
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(context,key, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    ad = interstitialAd;
                    show();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    ad = null;
                    onAdvertLoaded();
                }
            });
        } else
        {
            onAdvertLoaded();
        }
    }

    public abstract void onAdvertLoaded();

    public void show()
    {
        if(ad != null)
        {
            ad.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdDismissedFullScreenContent() {
                    onAdvertLoaded();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    onAdvertLoaded();
                }

            });

            ad.show(activity);
        }
    }

}
