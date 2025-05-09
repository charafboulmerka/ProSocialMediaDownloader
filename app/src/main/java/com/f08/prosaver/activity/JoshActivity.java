package com.f08.prosaver.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.f08.prosaver.R;
import com.f08.prosaver.api.CommonClassForAPI;
import com.f08.prosaver.databinding.LayoutGlobalUiBinding;
import com.f08.prosaver.util.AppLangSessionManager;
import com.f08.prosaver.util.SharePrefs;
import com.f08.prosaver.util.Utils;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static com.f08.prosaver.util.Utils.ROOTDIRECTORYJOSH;
import static com.f08.prosaver.util.Utils.createFileFolder;
import static com.f08.prosaver.util.Utils.startDownload;

public class JoshActivity extends AppCompatActivity {
    LayoutGlobalUiBinding binding;
    JoshActivity activity;
    CommonClassForAPI commonClassForAPI;
    private String VideoUrl;
    private ClipboardManager clipBoard;
    AppLangSessionManager appLangSessionManager;

    UnifiedNativeAd admobNativeAD;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.layout_global_ui);
        activity = this;
        commonClassForAPI = CommonClassForAPI.getInstance(activity);
        createFileFolder();
        initViews();

        binding.imAppIcon.setImageDrawable(getResources().getDrawable(R.drawable.josh_logo));
        binding.tvAppName.setText(getResources().getString(R.string.josh_app_name));


        appLangSessionManager = new AppLangSessionManager(activity);
        setLocale(appLangSessionManager.getLanguage());


        LoadNativeAd();
        InterstitialAdsINIT();

    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        assert activity != null;
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        PasteText();
    }

    private void initViews() {
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);

        binding.imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.imInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //binding.layoutHowTo.LLHowToLayout.setVisibility(View.VISIBLE);
            }
        });
/*
        Glide.with(activity)
                .load(R.drawable.sc1)
                .into(binding.layoutHowTo.imHowto1);

        Glide.with(activity)
                .load(R.drawable.sc2)
                .into(binding.layoutHowTo.imHowto2);

        Glide.with(activity)
                .load(R.drawable.sc1)
                .into(binding.layoutHowTo.imHowto3);

        Glide.with(activity)
                .load(R.drawable.jo2)
                .into(binding.layoutHowTo.imHowto4);

        binding.layoutHowTo.tvHowToHeadOne.setVisibility(View.GONE);
        binding.layoutHowTo.LLHowToOne.setVisibility(View.GONE);
        binding.layoutHowTo.tvHowToHeadTwo.setText(getResources().getString(R.string.how_to_download));

        binding.layoutHowTo.tvHowTo1.setText(getResources().getString(R.string.open_josh));
        binding.layoutHowTo.tvHowTo3.setText(getResources().getString(R.string.cop_link_from_josh));
        if (!SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISSHOWHOWTOJOSH)) {
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.ISSHOWHOWTOJOSH, true);
            binding.layoutHowTo.LLHowToLayout.setVisibility(View.VISIBLE);
        } else {
            binding.layoutHowTo.LLHowToLayout.setVisibility(View.GONE);
        }*/


        binding.loginBtn1.setOnClickListener(v -> {
            String LL = binding.etText.getText().toString();
            if (LL.equals("")) {
                Utils.setToast(activity, getResources().getString(R.string.enter_url));
            } else if (!Patterns.WEB_URL.matcher(LL).matches()) {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            } else {
                Utils.showProgressDialog(activity);
                getJoshData();
            }
        });

        binding.tvPaste.setOnClickListener(v -> {
            PasteText();
        });

        binding.LLOpenApp.setOnClickListener(v -> {
            Utils.OpenApp(activity, "com.eterno.shortvideos");
        });
    }

    private void getJoshData() {
        try {
            createFileFolder();
            URL url = new URL(binding.etText.getText().toString());
            String host = url.getHost();
            if (host.contains("myjosh")) {
                Utils.showProgressDialog(activity);
                new callGetJoshData().execute(binding.etText.getText().toString());
                showInterstitialAds();
            } else {
                Utils.setToast(activity, getResources().getString(R.string.enter_url));
            }

            showInterstitialAds();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PasteText() {
        try {
            binding.etText.setText("");
            String CopyIntent = getIntent().getStringExtra("CopyIntent");
            if (CopyIntent.equals("")) {

                if (!(clipBoard.hasPrimaryClip())) {

                } else if (!(clipBoard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                    if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("myjosh")) {
                        binding.etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }

                } else {
                    ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                    if (item.getText().toString().contains("myjosh")) {
                        binding.etText.setText(item.getText().toString());
                    }

                }
            } else {
                if (CopyIntent.contains("myjosh")) {
                    binding.etText.setText(CopyIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class callGetJoshData extends AsyncTask<String, Void, Document> {
        Document JoshDoc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... urls) {
            try {
                JoshDoc = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return JoshDoc;
        }

        protected void onPostExecute(Document result) {
            Utils.hideProgressDialog(activity);
            try {
                String url = result.select("script[id=\"__NEXT_DATA__\"]").last().html();

                if (!url.equals("")) {
                    JSONObject jsonObject = new JSONObject(url);
                    VideoUrl = String.valueOf(jsonObject.getJSONObject("props")
                            .getJSONObject("pageProps").getJSONObject("detail")
                            .getJSONObject("data").
                                    getString("download_url"));
                    //Mp3 Available
                    String mp3Url = String.valueOf(jsonObject.getJSONObject("props")
                            .getJSONObject("pageProps").getJSONObject("detail")
                            .getJSONObject("data").getJSONObject("audio_track_meta").
                                    getString("url"));

                    VideoUrl = VideoUrl.replace("r4_wmj_480.mp4","r4_480.mp4");
                    startDownload(VideoUrl, ROOTDIRECTORYJOSH, activity, "josh_" + System.currentTimeMillis() + ".mp4");
                    VideoUrl = "";
                    binding.etText.setText("");
                    showInterstitialAds();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }


    public void InterstitialAdsINIT() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_ad));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }


    private void showInterstitialAds() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    //RewardedAds : End


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
}