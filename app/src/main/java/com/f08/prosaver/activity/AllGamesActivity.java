package com.f08.prosaver.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.f08.prosaver.R;
import com.f08.prosaver.databinding.ActivityAllGamesBinding;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class AllGamesActivity extends AppCompatActivity {
    ActivityAllGamesBinding binding;
    AllGamesActivity activity;
    UnifiedNativeAd admobNativeAD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_games);
        activity = this;
        LoadNativeAd();
        initViews();
    }

    public void LoadNativeAd() {
        {
            binding.myTemplate.setVisibility(View.GONE);

            MobileAds.initialize(activity, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            AdLoader adLoader = new AdLoader.Builder(activity, getResources().getString(R.string.admob_native_ad))
                    .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            if (admobNativeAD != null) {
                                admobNativeAD.destroy();
                            }
                            admobNativeAD = unifiedNativeAd;

                            NativeTemplateStyle styles = new
                                    NativeTemplateStyle.Builder()
                                    .withCallToActionBackgroundColor
                                            (new ColorDrawable(ContextCompat.getColor(activity, R.color.colorAccent)))
                                    .build();
                            binding.myTemplate.setStyles(styles);
                            binding.myTemplate.setNativeAd(unifiedNativeAd);
                            binding.myTemplate.setBackground(getResources().getDrawable(R.drawable.rectangle_white));
                            binding.myTemplate.setVisibility(View.VISIBLE);
                        }
                    })
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }


    private void initViews() {
        binding.imBack.setOnClickListener(v -> onBackPressed());
        binding.RL2048.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, GamesPlayActivity.class);
                i.putExtra("url","2048");
                startActivity(i);
            }
        });
        binding.RLHelix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, GamesPlayActivity.class);
                i.putExtra("url","Helix");
                startActivity(i);
            }
        });
    }
}