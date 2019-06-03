package com.ultra.translator.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ultra.translator.R;
import com.ultra.translator.adapters.RecentWordAdapter;
import com.ultra.translator.database.DataBaseHelper;
import com.ultra.translator.listener.UnMarkListener;
import com.ultra.translator.models.Translation;

import java.util.ArrayList;
import java.util.List;

import ads.MyBaseActivityWithAds;

public class FavoriteActivity extends MyBaseActivityWithAds {

    private RecyclerView mRcvFavor;
    private RecentWordAdapter mAdapter;
    private List<Translation> mList;
    private DataBaseHelper mDataBaseHelper;
    private TextView mTxtNodata;
    private Button mBtnUnmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        mTxtNodata = findViewById(R.id.txt_nodata);
        mRcvFavor = findViewById(R.id.rcv_favor);
        mBtnUnmark = findViewById(R.id.btn_clear_mark);
        mBtnUnmark.setOnClickListener(clickListener);
        mRcvFavor.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mList = new ArrayList<>();
        mAdapter = new RecentWordAdapter(this, mList);
        mAdapter.setUnMarkListener(listener);
        mRcvFavor.setAdapter(mAdapter);
    }

    private UnMarkListener listener = new UnMarkListener() {
        @Override
        public void onUnMark(int position) {
            mList.remove(position);
            if (mList.size() == 0) mTxtNodata.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (Translation translation : mList) {
                if (mDataBaseHelper == null)
                    mDataBaseHelper = new DataBaseHelper(FavoriteActivity.this);
                translation.setMark(!translation.isMark());
                mDataBaseHelper.markTranslation(translation);
            }
            getData();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    public void getData() {
        mList.clear();
        if (mDataBaseHelper == null) mDataBaseHelper = new DataBaseHelper(this);
        mList.addAll(mDataBaseHelper.getFavoriteTranslate());
        if (mList.size() == 0) mTxtNodata.setVisibility(View.VISIBLE);
        else mTxtNodata.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) mAdapter.releaseTts();
    }
}
