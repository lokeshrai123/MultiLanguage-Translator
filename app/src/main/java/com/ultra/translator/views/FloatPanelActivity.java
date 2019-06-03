package com.ultra.translator.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.ultra.translator.AppContrains;
import com.ultra.translator.R;
import com.ultra.translator.adapters.LanguageAdapter;
import com.ultra.translator.database.DataBaseHelper;
import com.ultra.translator.models.Language;
import com.ultra.translator.models.Translation;
import com.ultra.translator.utils.AppUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

import static com.ultra.translator.utils.AppUtils.getAllLanguage;
import static com.ultra.translator.utils.AppUtils.getFlag;
import static com.ultra.translator.utils.AppUtils.getLanguageCode;

public class FloatPanelActivity extends FragmentActivity {

    private EditText mTxtInput;
    private TextView mTxtOutput;
    private Spinner mSpSource, mSpDes;
    private ImageButton mBtnSwapLang, mBtnGo, mBtnClearText;
    private Button mBtnGotoMain;
    private ImageView mImgFlag;
    private ProgressBar mProgress;
    private LanguageAdapter mAdapter;
    private DataBaseHelper dataBaseHelper;
    private Context mContext;
    private boolean flagUseApi = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.float_panel_layout);
        mContext = this;
        mBtnSwapLang = findViewById(R.id.btn_swap_lang);
        mBtnClearText = findViewById(R.id.btn_clear_text);
        mBtnGo = findViewById(R.id.btn_go);
        mSpDes = findViewById(R.id.sp_des);
        mSpSource = findViewById(R.id.sp_source);
        mTxtInput = findViewById(R.id.txt_input);
        mTxtOutput = findViewById(R.id.txt_output);
        mImgFlag = findViewById(R.id.img_input_flag);
        mProgress = findViewById(R.id.progressBar);
        mBtnGotoMain = findViewById(R.id.btn_main);
        mBtnGotoMain.setOnClickListener(listener);

        mBtnGo.setOnClickListener(listener);
        mBtnSwapLang.setOnClickListener(listener);
        mBtnClearText.setOnClickListener(listener);

        mTxtInput.setImeActionLabel(getString(R.string.action_done), EditorInfo.IME_ACTION_DONE);

        ArrayList<Language> mList = getAllLanguage(this);
        mAdapter = new LanguageAdapter(this, mList);
        mSpDes.setAdapter(mAdapter);
        mSpSource.setAdapter(mAdapter);
        mSpSource.setSelection(AppUtils.getLangSource(this));
        mSpDes.setSelection(AppUtils.getLangDes(this));
        mSpSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AppUtils.saveLangSoucre(FloatPanelActivity.this, i);
                Glide.with(FloatPanelActivity.this)
                        .load(getFlag(AppUtils.getLangSource(FloatPanelActivity.this)))
                        .into(mImgFlag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                hideKeyboard();
            }
        });
        mSpDes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AppUtils.saveLangDes(FloatPanelActivity.this, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                hideKeyboard();
            }
        });

        mTxtInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                    try {
                        translate();
                    } catch (UnsupportedEncodingException e) {
                        Log.e("translate", e.getMessage() + "");
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.btn_go:
                    try {
                        translate();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        mProgress.setVisibility(View.GONE);
                    }
                    break;
                case R.id.btn_swap_lang:
                    swapLang();
                    break;
                case R.id.btn_clear_text:
                    mTxtInput.setText("");
                    mTxtOutput.setText("");
                    break;
                case R.id.btn_main:
                    finish();
                    startActivity(new Intent(FloatPanelActivity.this, MainActivity.class));
                    break;
            }
        }
    };

    private void translate() throws UnsupportedEncodingException {
        String key = mTxtInput.getText().toString().trim();
        String langSouce = getLanguageCode(mContext, mSpSource.getSelectedItemPosition());
        String langDes = getLanguageCode(mContext, mSpDes.getSelectedItemPosition());
        boolean checkInternet = AppUtils.requestInternet(this, getString(R.string.mss_request_internet_for_translate));
        if (!checkInternet) return;
        if (TextUtils.isEmpty(key)) {
            Toast.makeText(this, mContext.getString(R.string.null_text), Toast.LENGTH_SHORT).show();
            return;
        }
        if (langDes.equals(langSouce)) {
            Toast.makeText(this, R.string.not_translate_same_lang, Toast.LENGTH_SHORT).show();
            return;
        }
        new TranslateTask(this, flagUseApi).execute(key, langSouce, langDes);
    }


    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.requestInternet(this, getString(R.string.mss_request_internet_for_app));
    }

    private void swapLang() {
        int langSourceIndex = mSpSource.getSelectedItemPosition();
        int langDesIndex = mSpDes.getSelectedItemPosition();
        mSpSource.setSelection(langDesIndex);
        mSpDes.setSelection(langSourceIndex);
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e) {
            Log.e("error", e.getMessage() + "");
        }
    }

    private void addRecentData(String text_result) {
        mTxtOutput.setText(text_result);
        if (dataBaseHelper == null) dataBaseHelper = new DataBaseHelper(mContext);
        Translation translation = new Translation();
        translation.setMark(false);
        translation.setLangSource(mSpSource.getSelectedItemPosition());
        translation.setLangDes(mSpDes.getSelectedItemPosition());
        translation.setWordSource(mTxtInput.getText().toString().trim());
        translation.setWordDes(text_result);
        dataBaseHelper.addTranslatation(translation);
    }

    static class TranslateTask extends AsyncTask<String, Void, String> {
        private FloatPanelActivity activity;
        boolean useApi;

        public TranslateTask() {
        }

        public TranslateTask(FloatPanelActivity activity, boolean useApi) {
            this.activity = activity;
            this.useApi = useApi;
        }

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(activity);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setMessage(activity.getString(R.string.translating));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (useApi) {
                TranslateOptions options = TranslateOptions.newBuilder()
                        .setApiKey(AppContrains.TRANSLATE_API_KEY)
                        .build();
                Translate translate = options.getService();
                com.google.cloud.translate.Translation translation = translate.translate(strings[0],
                        Translate.TranslateOption.sourceLanguage(strings[1]),
                        Translate.TranslateOption.targetLanguage(strings[2]));

                return translation.getTranslatedText();
            } else {
                Document document = null;
                try {
                    String key = URLEncoder.encode(strings[0], "UTF-8");
                    String url = "http://translate.google.com/m?hl=" + strings[1] +
                            "&sl=" + strings[1] + "&tl=" + strings[2] +
                            "&ie=UTF-8&prev=_m&q=" + key;
                    Log.e("url", url);
                    Random random = new Random();
                    int pos = random.nextInt(AppContrains.USER_AGENT.length);
                    document = Jsoup.connect(url).userAgent(AppContrains.USER_AGENT[pos]).get();
                    if (document != null) {
                        Elements meanElement = document.select("div.t0");
                        if (meanElement != null && meanElement.size() >= 0) {
                            for (int i = 0; i < meanElement.size(); i++) {
                                String mean = meanElement.get(i).text();
                                Log.e("mean", mean + "");
                                return mean;
                            }
                        } else {
                            Log.e("element mean", "null");
                        }
                    }
                } catch (IOException e) {
//                    if (document!= null) document.remove();
                    Log.e("error", e.getMessage() + "");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("on post", "excute");
            dialog.cancel();
            if (!TextUtils.isEmpty(s)) activity.addRecentData(s);
        }
    }
}
