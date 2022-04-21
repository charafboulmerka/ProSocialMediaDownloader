package com.f08.prosaver.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import io.reactivex.observers.DisposableObserver;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.ContentValues.TAG;
import static com.f08.prosaver.util.Utils.RootDirectorySnackVideo;
import static com.f08.prosaver.util.Utils.createFileFolder;
import static com.f08.prosaver.util.Utils.startDownload;

public class SnackVideoActivity extends AppCompatActivity {
    private LayoutGlobalUiBinding binding;
    SnackVideoActivity activity;
    CommonClassForAPI commonClassForAPI;
    private String VideoUrl;
    private ClipboardManager clipBoard;
    AppLangSessionManager appLangSessionManager;
    InterstitialAd mInterstitialAd;
    UnifiedNativeAd admobNativeAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.layout_global_ui);
        activity = this;
        commonClassForAPI = CommonClassForAPI.getInstance(activity);
        createFileFolder();
        initViews();

        binding.imAppIcon.setImageDrawable(getResources().getDrawable(R.drawable.snackvideo));
        binding.tvAppName.setText(getResources().getString(R.string.snack_app_name));


        appLangSessionManager = new AppLangSessionManager(activity);
        setLocale(appLangSessionManager.getLanguage());


        InterstitialAdsINIT();
        LoadNativeAd();

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

        binding.imBack.setOnClickListener(view -> onBackPressed());
       // binding.imInfo.setOnClickListener(view -> binding.layoutHowTo.LLHowToLayout.setVisibility(View.VISIBLE));
/*
        Glide.with(activity)
                .load(R.drawable.sn1)
                .into(binding.layoutHowTo.imHowto1);

        Glide.with(activity)
                .load(R.drawable.sn2)
                .into(binding.layoutHowTo.imHowto2);

        Glide.with(activity)
                .load(R.drawable.sn1)
                .into(binding.layoutHowTo.imHowto3);

        Glide.with(activity)
                .load(R.drawable.sn2)
                .into(binding.layoutHowTo.imHowto4);

        binding.layoutHowTo.tvHowToHeadOne.setVisibility(View.GONE);
        binding.layoutHowTo.LLHowToOne.setVisibility(View.GONE);
        binding.layoutHowTo.tvHowToHeadTwo.setText(getResources().getString(R.string.how_to_download));
        binding.layoutHowTo.tvHowTo1.setText(getResources().getString(R.string.open_snack));
        binding.layoutHowTo.tvHowTo3.setText(getResources().getString(R.string.cop_link_from_snack));
        if (!SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISSHOWHOWTOSNACK)) {
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.ISSHOWHOWTOSNACK, true);
            binding.layoutHowTo.LLHowToLayout.setVisibility(View.VISIBLE);
        } else {
            binding.layoutHowTo.LLHowToLayout.setVisibility(View.GONE);
        }
*/

        binding.loginBtn1.setOnClickListener(v -> {
            String LL = binding.etText.getText().toString();
            if (LL.equals("")) {
                Utils.setToast(activity, getResources().getString(R.string.enter_url));
            } else if (!Patterns.WEB_URL.matcher(LL).matches()) {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            } else {
                GetsnackvideoData();
            }
        });

        binding.tvPaste.setOnClickListener(v -> {
            PasteText();
        });

        binding.LLOpenApp.setOnClickListener(v -> {
            Utils.OpenApp(activity, "com.kwai.bulldog");
        });
    }

    private void GetsnackvideoData() {
        try {
            createFileFolder();
            URL url = new URL(binding.etText.getText().toString());
            String host = url.getHost();
            if (host.contains("snackvideo")){
                Utils.showProgressDialog(activity);
                new callGetsnackvideoData().execute(binding.etText.getText().toString());


            }else if (host.contains("sck.io")) {
                getUrlData(binding.etText.getText().toString());
            } else {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
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
                    if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("snackvideo")||clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("sck.io")) {
                        binding.etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }

                } else {
                    ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                    if (item.getText().toString().contains("snackvideo")||item.getText().toString().contains("sck.io")) {
                        binding.etText.setText(item.getText().toString());
                    }

                }
            } else {
                if (CopyIntent.contains("snackvideo")||CopyIntent.contains("sck.io")) {
                    binding.etText.setText(CopyIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getUrlData(String str) {
        URI uri;
        try {
            uri = new URI(str);
        } catch (Exception e) {
            e.printStackTrace();
            uri = null;
        }
        String[] split = uri.getPath().split("/");
        String str2 = split[split.length - 1];
        String str3 = "android";
        String str4 = "8c46a905";
        StringBuilder sb = new StringBuilder("ANDROID_");
        sb.append(Settings.Secure.getString(getContentResolver(), "android_id"));
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList.add("mod=OnePlus(ONEPLUS A5000)");
        arrayList.add("lon=0");
        arrayList.add("country_code=in");
        StringBuilder sb2 = new StringBuilder();
        sb2.append("did=");
        sb2.append(sb);
        arrayList.add(sb2.toString());
        arrayList.add("app=1");
        arrayList.add("oc=UNKNOWN");
        arrayList.add("egid=");
        arrayList.add("ud=0");
        arrayList.add("c=GOOGLE_PLAY");
        arrayList.add("sys=KWAI_BULLDOG_ANDROID_9");
        arrayList.add("appver=2.7.1.153");
        arrayList.add("mcc=0");
        arrayList.add("language=en-in");
        arrayList.add("lat=0");
        arrayList.add("ver=2.7");
        arrayList2.addAll(arrayList);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("shortKey=");
        sb3.append(str2);
        arrayList2.add(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("os=");
        sb4.append(str3);
        arrayList2.add(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("client_key=");
        sb5.append(str4);
        arrayList2.add(sb5.toString());
        try {
            Collections.sort(arrayList2);
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        String clockKey = com.yxcorp.gifshow.util.CPU.getClockData(this, TextUtils.join("", arrayList2).getBytes(Charset.forName("UTF-8")), 0);

        StringBuilder sb6 = new StringBuilder();
        sb6.append("https://g-api.snackvideo.com/rest/bulldog/share/get?");
        sb6.append(TextUtils.join("&", arrayList));
        try {
            Utils utils = new Utils(activity);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {
                    Utils.showProgressDialog(activity);
                    commonClassForAPI.callSnackVideoData(observer,sb6.toString(),str2,str3,clockKey,str4);
                }
            } else {
                Utils.setToast(activity, getResources().getString(R.string.no_net_conn));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private DisposableObserver<JsonObject> observer = new DisposableObserver<JsonObject>() {
        @Override
        public void onNext(JsonObject jsonObject) {
            Utils.hideProgressDialog(activity);
            try {
                JsonObject photo = jsonObject.get("photo").getAsJsonObject();
                JsonArray mainArray = photo.get("main_mv_urls").getAsJsonArray();
                VideoUrl = mainArray.get(0).getAsJsonObject().get("url").getAsString();
                startDownload(VideoUrl, RootDirectorySnackVideo, activity, "snackvideo_"+System.currentTimeMillis() + ".mp4");
                VideoUrl = "";
                binding.etText.setText("");

                showInterstitialAds();

            } catch (Exception e) {
                e.printStackTrace();
                Utils.setToast(activity,getResources().getString(R.string.no_media_on_snackvideo));
            }
        }

        @Override
        public void onError(Throwable e) {
            Utils.hideProgressDialog(activity);
            e.printStackTrace();

        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog(activity);

        }
    };
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    class callGetsnackvideoData extends AsyncTask<String, Void, Document> {
        Document snackvideoDoc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... urls) {
            try {
                snackvideoDoc = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Error");
            }
            return snackvideoDoc;
        }

        protected void onPostExecute(Document result) {
            Utils.hideProgressDialog(activity);
            try {
                Elements element = result.select("script");
                String URL = "";
                for (Element script : element) {
                    String a = script.data();
                    if (a.contains("window.__INITIAL_STATE__")) {
                        a= a.substring(a.indexOf("{"),a.indexOf("};"))+"}";
                        URL = a;
                        break;
                    }
                }
                if (!URL.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(URL);
                        VideoUrl = jsonObject.getJSONObject("sharePhoto").getString("mp4Url");
                        String Url = jsonObject.getString("shortUrl");
                        getUrlData(Url);
                        VideoUrl = "";
                        binding.etText.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                showInterstitialAds();


            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }


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