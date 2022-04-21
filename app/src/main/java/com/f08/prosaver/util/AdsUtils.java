package com.f08.prosaver.util;

import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class AdsUtils {

    public static void showGoogleBannerAd(Context context, com.google.android.gms.ads.AdView googleAdView) {

        googleAdView.setVisibility(View.VISIBLE);
        //Load Banner Ad
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        googleAdView.loadAd(adRequest);
    }



}
