package com.ultra.translator;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.ads.AudienceNetworkAds;
import com.ultra.translator.utils.FileUtils;

/**
 * Created by HP 6300 Pro on 12/11/2017.
 */

public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        FileUtils.createaAppDir();
        FileUtils.createTrainningDir();
        MultiDex.install(this);

        AudienceNetworkAds.initialize(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);


    }

}
