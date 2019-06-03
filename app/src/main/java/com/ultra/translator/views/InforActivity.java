package com.ultra.translator.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ultra.translator.AppContrains;
import com.ultra.translator.R;
import com.ultra.translator.utils.AppUtils;

import ads.MyBaseActivityWithAds;

public class InforActivity extends MyBaseActivityWithAds {
    private TextView mTxt;
    private Button mBtnShare, mBtnRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mBtnShare = findViewById(R.id.btn_share_app);
        mBtnRate = findViewById(R.id.btn_rate_app);

        mBtnShare.setOnClickListener(clickListener);
        mBtnRate.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.btn_rate_app:
                    AppUtils.goToStore(InforActivity.this, getPackageName());
                    break;
                case R.id.btn_share_app:
                    AppUtils.shareText(InforActivity.this,
                            AppContrains.PLAY_STORE_LINK + getPackageName(),
                            getString(R.string.title_share_app));
                    break;
            }
        }
    };
}
