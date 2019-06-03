package ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.ultra.translator.R;
import com.ultra.translator.utils.Idelegate;

public class MyAdsController { //

    public static String KEY_ADS_PLATFORM = "key_ads"; //

    public static void setTypeAds(Context ctx) {


        isUseAdmob = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(KEY_ADS_PLATFORM, false);


        FB_BANNER_ID = "11";
        FB_INTERSTITIAL_ID = "11";

        AudienceNetworkAds.initialize(ctx);

    }


    private static InterstitialAd mInterstitialAd;
    private static com.facebook.ads.InterstitialAd mFbInterstitialAd;
    private static int flagAds = 1;

    public static boolean isUseAdmob = false;


    private static String FB_BANNER_ID = "";

    private static String FB_INTERSTITIAL_ID = "";

    public static void listenNetworkChangeToRequestAdsFull(Activity ac) {

        if (h == null)
            h = new Handler();

        if (rCheckNetworkLoadAdsInterstitial == null)
            rCheckNetworkLoadAdsInterstitial = new RCheckNetworkLoadAdsInterstitial(ac);

        h.post(rCheckNetworkLoadAdsInterstitial);
    }

    static RCheckNetworkLoadAdsInterstitial rCheckNetworkLoadAdsInterstitial;

    static boolean lastNetworkAvailable = false;

    static class RCheckNetworkLoadAdsInterstitial implements Runnable {
        Activity ac;

        public RCheckNetworkLoadAdsInterstitial(Activity ac) {
            this.ac = ac;
        }

        @Override
        public void run() {
            if (!lastNetworkAvailable
                    && CheckInternet.isNetworkAvailable(ac)) {
                requestNewInterstitial();
            }

            if (CheckInternet.isNetworkAvailable(ac)) {
                lastNetworkAvailable = true;
            } else {
                lastNetworkAvailable = false;
            }

            h.postDelayed(RCheckNetworkLoadAdsInterstitial.this, 4000);
        }
    }


    public static void handleInterstitialAds(final Activity ac) {

        if (mInterstitialAd == null)
            mInterstitialAd = new InterstitialAd(ac);

        String adUnitId = mInterstitialAd.getAdUnitId();

        if (adUnitId == null || adUnitId.equals("")) {

            if (isUseAdmob)
                mInterstitialAd.setAdUnitId(getInterstitialAdsId(ac));
            else
                mInterstitialAd.setAdUnitId("11");


        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });


        //facebook ads
        mFbInterstitialAd = new com.facebook.ads.InterstitialAd(ac, FB_INTERSTITIAL_ID);

        requestNewInterstitial();


        h.post(rAdsInterstitial);
    }


    public static void requestNewInterstitial() {

        if (isUseAdmob) {
            AdRequest adRequest = MyAdsController.getAdRequest();

            if (mInterstitialAd != null && !mInterstitialAd.isLoaded())
                mInterstitialAd.loadAd(adRequest);
        } else {
            try {
                if (mFbInterstitialAd != null && !mFbInterstitialAd.isAdLoaded())
                    mFbInterstitialAd.loadAd();
            } catch (Exception e) {

            }
        }


    }


    public static Handler h = new Handler();

    public static Runnable rAdsInterstitial = new Runnable() {

        @Override
        public void run() {

            flagAds = 1;

            h.postDelayed(rAdsInterstitial, TIME_DELAY_ADS_INTERSTITIAL);
        }
    };


    protected static AdRequest getAdRequest() {

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        return adRequest;
    }


    static final int TIME_DELAY_ADS_INTERSTITIAL = 60 * 1000 * 3;

    public static void showAdsFullBeforeDoAction(final Idelegate callback) {

        if (mInterstitialAd == null && mFbInterstitialAd == null) {
            callback.callBack(0, 0);
            return;
        }

        if (flagAds == 1) {

            if (isUseAdmob) {
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {

                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {

                            requestNewInterstitial();

                            flagAds = 0;

                            h.removeCallbacks(MyAdsController.rAdsInterstitial);

                            h.postDelayed(MyAdsController.rAdsInterstitial, TIME_DELAY_ADS_INTERSTITIAL);

                            callback.callBack(0, 0);
                        }
                    });

                    mInterstitialAd.show();


                } else {
                    callback.callBack(0, 0);

                    requestNewInterstitial();
                }
            } else {
                if (mFbInterstitialAd.isAdLoaded()) {
                    mFbInterstitialAd.setAdListener(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                            callback.callBack(0, 0);

                            requestNewInterstitial();

                            flagAds = 0;

                            h.removeCallbacks(MyAdsController.rAdsInterstitial);

                            h.postDelayed(MyAdsController.rAdsInterstitial, TIME_DELAY_ADS_INTERSTITIAL);

                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {

                        }

                        @Override
                        public void onAdLoaded(Ad ad) {

                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    });

                    try {
                        mFbInterstitialAd.show();
                    } catch (Exception e) {
                        callback.callBack(0, 0);
                    }

                } else {
                    callback.callBack(0, 0);

                    requestNewInterstitial();
                }
            }

        } else {

            callback.callBack(0, 0);

            requestNewInterstitial();
        }
    }

    public static void showBannerAds (final Activity ctx) {

        final AdView mAdViewBanner = new AdView(ctx);

//        if (MyIAPUtils.localCheckIsPurchase(ctx)) {
//            mAdViewBanner.setVisibility(View.GONE);
//
//            return;
//        }


        String adUnitId = mAdViewBanner.getAdUnitId();

        if (adUnitId == null || adUnitId.equals("")) {
            String adsId = getBannerAdsId(ctx);

            mAdViewBanner.setAdSize(com.google.android.gms.ads.AdSize.BANNER);

            mAdViewBanner.setAdUnitId(adsId);
        }


        final RelativeLayout adViewContainer = (RelativeLayout) ctx.findViewById(R.id.adView_container);

        if (isUseAdmob) {

            adViewContainer.removeAllViews();

            try {
                adViewContainer.addView(mAdViewBanner);
            } catch (Exception e) {

            }


            AdRequest adRequest = getAdRequest();
            mAdViewBanner.loadAd(adRequest);
            mAdViewBanner.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);

                    adViewContainer.removeAllViews();

                    com.facebook.ads.AdView adViewFB = new com.facebook.ads.AdView(ctx, FB_BANNER_ID, AdSize.BANNER_HEIGHT_50);
                    adViewContainer.addView(adViewFB);
                    adViewFB.loadAd();
                }
            });
        } else {

            adViewContainer.removeAllViews();

            final com.facebook.ads.AdView adViewFB = new com.facebook.ads.AdView(ctx, FB_BANNER_ID, AdSize.BANNER_HEIGHT_50);

            try {
                adViewContainer.addView(adViewFB);
            } catch (Exception e) {

            }

            adViewFB.setAdListener(new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {

                    adViewFB.setVisibility(View.GONE);

                    adViewContainer.removeAllViews();

                    adViewContainer.addView(mAdViewBanner);

                    AdRequest adRequest = getAdRequest();
                    mAdViewBanner.loadAd(adRequest);
                }

                @Override
                public void onAdLoaded(Ad ad) {

                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            });

            adViewFB.loadAd();
        }
    }


    public static String getApplicationAdsId(Context ctx) {

        return "ca-app-pub-3940256099942544~3347511713";
    }


    private static String getBannerAdsId(Context ctx) {

        return "ca-app-pub-3940256099942544/6300978111";
    }

    private static String getInterstitialAdsId(Context ctx) {

        return "ca-app-pub-3940256099942544/1033173712";
    }


    public static void releaseAds_Callbacks() {
        if (h != null)
            h.removeCallbacksAndMessages(null);

        try {
            if (mFbInterstitialAd != null) {
                mFbInterstitialAd.destroy();
            }
        } catch (Exception e) {

        }

        try {
            if (mInterstitialAd != null) {
                mInterstitialAd = null;
            }
        } catch (Exception e) {

        }


        flagAds = 1;
    }
}
