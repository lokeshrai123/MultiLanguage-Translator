package com.ultra.translator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ultra.translator.R;
import com.ultra.translator.models.Language;
import com.ultra.translator.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP 6300 Pro on 12/12/2017.
 */

public class LanguageAdapter extends ArrayAdapter {

    private Context mContext;
    private List<Language> mList;
    private LayoutInflater mInflater;

    public LanguageAdapter(Context context, ArrayList<Language> languages) {
        super(context, 0, languages);
        this.mContext = context;
        this.mList = languages;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_language, parent, false);
        ImageView imgFlag = view.findViewById(R.id.img_flag);
        TextView txtLangName = view.findViewById(R.id.txt_lang_name);
        Glide.with(mContext).load(AppUtils.getFlag(mList.get(position).getFlag())).into(imgFlag);
        txtLangName.setText(mList.get(position).getName().toString());
        return view;
    }
}
