package com.ultra.translator.float_widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.ultra.translator.views.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.ultra.translator.utils.AppUtils.getAllLanguage;
import static com.ultra.translator.utils.AppUtils.getFlag;
import static com.ultra.translator.utils.AppUtils.getLanguageCode;

/**
 * Created by HP 6300 Pro on 12/21/2017.
 */

public class FloatDialog extends Dialog {
    private EditText mTxtInput;
    private TextView mTxtOutput;
    private Spinner mSpSource, mSpDes;
    private ImageButton mBtnSwapLang, mBtnGo, mBtnClearText;
    private Button mBtnHome;
    private ImageView mImgFlag;
    private ProgressBar mProgress;
    private LanguageAdapter mAdapter;
    private DataBaseHelper dataBaseHelper;
    private Context mContext;
    private boolean flagUseApi = false;

    public FloatDialog(Context context) {
        super(context, R.style.MyPanel);
        intit(context);
    }

    private void intit(Context context) {
        this.mContext = context;
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.float_panel_layout);
        getWindow().setFormat(Window.PROGRESS_VISIBILITY_OFF);
        getWindow().setBackgroundDrawable(null);
        getWindow().setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= 26) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        //getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);

        mBtnSwapLang = findViewById(R.id.btn_swap_lang);
        mBtnClearText = findViewById(R.id.btn_clear_text);
        mBtnGo = findViewById(R.id.btn_go);
        mSpDes = findViewById(R.id.sp_des);
        mSpSource = findViewById(R.id.sp_source);
        mTxtInput = findViewById(R.id.txt_input);
        mTxtOutput = findViewById(R.id.txt_output);
        mImgFlag = findViewById(R.id.img_input_flag);
        mProgress = findViewById(R.id.progressBar);
        mBtnHome = findViewById(R.id.btn_main);

        mTxtInput.setImeActionLabel(this.mContext.getString(R.string.action_done), EditorInfo.IME_ACTION_DONE);

        mBtnHome.setOnClickListener(listener);
        mBtnGo.setOnClickListener(listener);
        mBtnSwapLang.setOnClickListener(listener);
        mBtnClearText.setOnClickListener(listener);

        ArrayList<Language> mList = getAllLanguage(mContext);
        mAdapter = new LanguageAdapter(mContext, mList);
        mSpDes.setAdapter(mAdapter);
        mSpSource.setAdapter(mAdapter);
        mSpSource.setSelection(AppUtils.getLangSource(mContext));
        mSpDes.setSelection(AppUtils.getLangDes(mContext));
        mSpSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AppUtils.saveLangSoucre(mContext, i);
                Glide.with(mContext)
                        .load(getFlag(AppUtils.getLangSource(mContext)))
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
                AppUtils.saveLangDes(mContext, i);
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
                        mProgress.setVisibility(GONE);
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
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    dismiss();
                    break;
            }
        }
    };

    private void translate() throws UnsupportedEncodingException {
        String key = mTxtInput.getText().toString().trim();
        String langSouce = getLanguageCode(mContext, mSpSource.getSelectedItemPosition());
        String langDes = getLanguageCode(mContext, mSpDes.getSelectedItemPosition());
        boolean checkInternet = AppUtils.requestInternet(mContext, mContext.getString(R.string.mss_request_internet_for_translate));
        if (!checkInternet) return;
        if (TextUtils.isEmpty(key)) {
            Toast.makeText(mContext, mContext.getString(R.string.null_text), Toast.LENGTH_SHORT).show();
            return;
        }
        if (langDes.equals(langSouce)) {
            Toast.makeText(mContext, R.string.not_translate_same_lang, Toast.LENGTH_SHORT).show();
            return;
        }
        new TranslateTask(this, flagUseApi).execute(key, langSouce, langDes);
    }

    private void swapLang() {
        int langSourceIndex = mSpSource.getSelectedItemPosition();
        int langDesIndex = mSpDes.getSelectedItemPosition();
        mSpSource.setSelection(langDesIndex);
        mSpDes.setSelection(langSourceIndex);
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
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
        hideKeyboard();
    }

    static class TranslateTask extends AsyncTask<String, Void, String> {
        private FloatDialog floatPanle;
        private boolean useApi;

        public TranslateTask() {
        }

        public TranslateTask(FloatDialog s, boolean useApi) {
            this.floatPanle = s;
            this.useApi = useApi;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            floatPanle.mProgress.setVisibility(VISIBLE);
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
            floatPanle.mProgress.setVisibility(GONE);
            floatPanle.addRecentData(s);
        }
    }
}
