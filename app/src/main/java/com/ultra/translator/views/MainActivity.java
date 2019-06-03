package com.ultra.translator.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.ultra.translator.AppContrains;
import com.ultra.translator.PermissionUtils;
import com.ultra.translator.R;
import com.ultra.translator.adapters.LanguageAdapter;
import com.ultra.translator.adapters.RecentWordAdapter;
import com.ultra.translator.database.DataBaseHelper;
import com.ultra.translator.float_widget.FloatService;
import com.ultra.translator.models.Language;
import com.ultra.translator.models.Translation;
import com.ultra.translator.orc.OrcManager;
import com.ultra.translator.utils.AlertDialogUtils;
import com.ultra.translator.utils.AppUtils;
import com.ultra.translator.utils.FileUtils;
import com.ultra.translator.utils.Idelegate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

import ads.MyAdsController;
import ads.MyBaseMainActivity;

import static com.ultra.translator.utils.AppUtils.getAllLanguage;
import static com.ultra.translator.utils.AppUtils.getFlag;
import static com.ultra.translator.utils.AppUtils.getLanguageCode;
import static com.ultra.translator.utils.AppUtils.getSttCode;

public class MainActivity extends MyBaseMainActivity {
    // request code
    public static final int REQUEST_CODE_CROP = 1003;
    public static final int REQUEST_CODE_SPEECHTOTEXT = 1002;
    public static final int REQUEST_CODE_GALLEY = 1001;
    public static final int REQUEST_CODE_CAMERA = 1000;
    public static final String EXTRA_DATA = "data";
    private EditText mTxtInput;
    private ImageView imgInputFlag;
    private LinearLayout mBtnORC, mBtnSpeechtoText,
            mBtnShowFLoat, mBtnDeleteAll, mBtnFavor, mBtnInfo;
    private ImageButton mBtnClearText, mBtnGo, mBtnSwapLang;
    private Spinner mSpLangSource, mSpLangDes;
    private RecyclerView mRcvRecent;
    private TextView mTxtNodata;

    private ArrayList<Language> mList;
    private LanguageAdapter mLanguageAdapter;
    private RecentWordAdapter mRecentWordAdapter;
    private ArrayList<Translation> mListTranslation;
    private DataBaseHelper dataBaseHelper;
    private OrcManager mOrcManager;
    private Bitmap tmpBitmap;
    private ImageView tmpImageView;

    private static boolean flagUseApi = false;

    private static final String KEY_USE_API = "is_use_api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (PermissionUtils.get(this).checkIsGrantAllPermissions(this)) {
            handleAfterGrantedPermissions();
        } else {
            PermissionUtils.get(this).requestAllPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.get(this).onRequestPermissionResult(permissions, grantResults);

        if (PermissionUtils.get(this).checkIsGrantAllPermissions(this)) {
            handleAfterGrantedPermissions();
        } else {
            finish();
        }
    }

    private void handleAfterGrantedPermissions () {
        flagUseApi = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(KEY_USE_API, false);

        mTxtNodata = findViewById(R.id.txt_nodata);
        mBtnSwapLang = findViewById(R.id.btn_swap_lang);
        mBtnSwapLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapLang();
            }
        });

        mRcvRecent = findViewById(R.id.rcv_recent);
        mRcvRecent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mTxtInput = findViewById(R.id.txt_input);
        mTxtInput.setImeActionLabel(getString(R.string.action_done), EditorInfo.IME_ACTION_DONE);

        mBtnORC = findViewById(R.id.btn_open_orc_option);
        mBtnORC.setOnClickListener(onBottomBarClickListioner);

        mBtnSpeechtoText = findViewById(R.id.btn_mic);
        mBtnSpeechtoText.setOnClickListener(onBottomBarClickListioner);

        mBtnShowFLoat = findViewById(R.id.btn_float_button);
        mBtnShowFLoat.setOnClickListener(onBottomBarClickListioner);

        mBtnDeleteAll = findViewById(R.id.btn_delete_all);
        mBtnDeleteAll.setOnClickListener(onBottomBarClickListioner);

        mBtnInfo = findViewById(R.id.btn_info);
        mBtnInfo.setOnClickListener(onBottomBarClickListioner);

        mBtnFavor = findViewById(R.id.btn_favor);
        mBtnFavor.setOnClickListener(onBottomBarClickListioner);

        mSpLangSource = findViewById(R.id.sp_source);
        mSpLangDes = findViewById(R.id.sp_des);
        mList = getAllLanguage(this);
        mLanguageAdapter = new LanguageAdapter(this, mList);
        mSpLangDes.setAdapter(mLanguageAdapter);
        mSpLangSource.setAdapter(mLanguageAdapter);
        updateLanguageSelection();
        mSpLangSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AppUtils.saveLangSoucre(MainActivity.this, i);
                Glide.with(MainActivity.this)
                        .load(getFlag(AppUtils.getLangSource(MainActivity.this)))
                        .into(imgInputFlag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                hideKeyboard();
            }
        });
        mSpLangDes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AppUtils.saveLangDes(MainActivity.this, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                hideKeyboard();
            }
        });

        mListTranslation = new ArrayList<>();
        mRecentWordAdapter = new RecentWordAdapter(this, mListTranslation);
        mRcvRecent.setAdapter(mRecentWordAdapter);

        imgInputFlag = findViewById(R.id.img_input_flag);
        Glide.with(this)
                .load(getFlag(AppUtils.getLangSource(this)))
                .into(imgInputFlag);

        mBtnClearText = findViewById(R.id.btn_clear_text);

        mBtnClearText.setOnClickListener(onInputItemBarClick);

        mBtnGo = findViewById(R.id.btn_go);
        mBtnGo.setOnClickListener(onInputItemBarClick);

        mTxtInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_NEXT) {
                    hideKeyboard();
                    try {
                        translate(mTxtInput.getText().toString().trim(),
                                getLanguageCode(MainActivity.this, mSpLangSource.getSelectedItemPosition()),
                                getLanguageCode(MainActivity.this, mSpLangDes.getSelectedItemPosition()));
                    } catch (UnsupportedEncodingException e) {
                        Log.e("translate", e.getMessage() + "");
                    }
                    return true;
                }
                return false;
            }
        });

    }

    private void updateLanguageSelection() {
        mSpLangSource.setSelection(AppUtils.getLangSource(this));
        mSpLangDes.setSelection(AppUtils.getLangDes(this));
    }

    @Override
    protected void onResume() {

        if (PermissionUtils.get(this).checkIsGrantAllPermissions(this)) {
            Log.e("onResume: ", "onresume");
            AppUtils.requestInternet(this, getString(R.string.mss_request_internet_for_app));
            super.onResume();
            getData();
            updateLanguageSelection();
        } else {
            super.onResume();
        }

    }

    private View.OnClickListener onInputItemBarClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboard();
            int id = view.getId();
            switch (id) {
                case R.id.btn_clear_text:
                    mTxtInput.setText("");
                    break;
                case R.id.btn_go:
                    try {
                        translate(mTxtInput.getText().toString().trim(),
                                getLanguageCode(MainActivity.this, mSpLangSource.getSelectedItemPosition()),
                                getLanguageCode(MainActivity.this, mSpLangDes.getSelectedItemPosition())
                        );
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    View.OnClickListener onBottomBarClickListioner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboard();
            int id = view.getId();
            switch (id) {
                case R.id.btn_mic:
                    startSpeechtotext(mSpLangSource.getSelectedItemPosition());
                    break;
                case R.id.btn_float_button:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {

                        AlertDialogUtils.showAlertDialog(MainActivity.this, getString(R.string.dialog_capquyen_title), getString(R.string.dialog_capquyen_mess), "Ok", "", false, new Idelegate() {
                            @Override
                            public void callBack(Object value, int where) {
                                //If the draw over permission is not available open the settings screen
                                //to grant the permission.
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        });

                    } else {
                        Intent service = new Intent(getBaseContext(), FloatService.class);
                        //ContextCompat.startForegroundService(getBaseContext(), service);

                        startService(service);
                    }


                    break;
                case R.id.btn_delete_all:
                    if (mListTranslation.size() == 0)
                        Toast.makeText(MainActivity.this, R.string.no_history, Toast.LENGTH_SHORT).show();
                    else showDialogConfirmDelete();
                    break;
                case R.id.btn_info:
                    MyAdsController.showAdsFullBeforeDoAction(new Idelegate() {
                        @Override
                        public void callBack(Object value, int where) {
                            startActivity(new Intent(MainActivity.this, InforActivity.class));
                        }
                    });

                    break;
                case R.id.btn_favor:

                    MyAdsController.showAdsFullBeforeDoAction(new Idelegate() {
                        @Override
                        public void callBack(Object value, int where) {
                            startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
                        }
                    });

                    break;
                case R.id.btn_open_orc_option:
                    openOrcOption();
                    break;
                default:
                    break;
            }
        }
    };

    private void openOrcOption() {
        PopupMenu popupMenu = new PopupMenu(this, mBtnORC);
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.orc_option, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.btn_camera:
                        openCamera();
                        return true;
                    case R.id.btn_galley:
                        openGalley();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void showDialogConfirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_confirm_delete)
                .setMessage(R.string.mss_delete_all)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        dataBaseHelper.deleteAll();
                        getData();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.show();
    }

    private void startSpeechtotext(int code) {
        String speechCode = getSttCode(this, code);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
//        get support lang
//        checkSupportLanguage();

        if (!TextUtils.isEmpty(speechCode) && speechCode.contains("-")) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, speechCode);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, speechCode);
            intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, speechCode);

        } else {
            Toast.makeText(this, R.string.toast_stt_not_support_language, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECHTOTEXT);
        } catch (Exception a) {
            Toast.makeText(this, R.string.toast_not_support_stt, Toast.LENGTH_SHORT).show();
            showDialogInstallSTT();
        }
    }

    private void checkSupportLanguage() {
        Intent detaltIntent = new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
        sendOrderedBroadcast(detaltIntent, null, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle results = getResultExtras(true);
                if (results.containsKey(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE)) {
                    String languagePreference = results.getString(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE);
                    Log.e("lang pref: ", languagePreference);
                }
                if (results.containsKey(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES)) {
                    ArrayList<String> supportedLanguages = results.getStringArrayList(
                            RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES);
                    Log.e("lang pref: ", supportedLanguages.toString());
                }
            }
        }, null, RESULT_OK, null, null);
    }

    private void showDialogInstallSTT() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_missing_extention)
                .setMessage(R.string.mss_install_stt)
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        AppUtils.goToStore(MainActivity.this, AppContrains.GOOGLE_SPEECH_TO_TEXT);
                    }
                });
        builder.show();
    }

    private void swapLang() {
        int langSourceIndex = mSpLangSource.getSelectedItemPosition();
        int langDesIndex = mSpLangDes.getSelectedItemPosition();
        mSpLangSource.setSelection(langDesIndex);
        mSpLangDes.setSelection(langSourceIndex);
    }

    private void translate(String key, String langSouce, String langDes) throws UnsupportedEncodingException {
        boolean checkInternet = AppUtils.requestInternet(this, getString(R.string.mss_request_internet_for_translate));
        if (!checkInternet) return;
        if (TextUtils.isEmpty(key)) {
            Toast.makeText(this, R.string.null_text, Toast.LENGTH_SHORT).show();
            return;
        }
        if (langDes.equals(langSouce)) {
            Toast.makeText(this, R.string.not_translate_same_lang, Toast.LENGTH_SHORT).show();
            return;
        }
        new TranslateTask(this, flagUseApi).execute(key, langSouce, langDes);
    }

    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }
    }

    public void openGalley() {
        Intent intentPickFGalley = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentPickFGalley, REQUEST_CODE_GALLEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                try {
                    showDialogEditBitmap(imageBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_GALLEY) {
                Uri uri = data.getData();
                if (uri != null) {
                    String[] array = {MediaStore.MediaColumns.DATA};
                    Cursor cursor = getContentResolver().query(uri, array, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                        final Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        cursor.close();
                        try {
                            showDialogEditBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (requestCode == REQUEST_CODE_SPEECHTOTEXT) {
                ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (results.size() > 1) mTxtInput.setText(results.get(0));
                else Toast.makeText(this, R.string.tts_no_result, Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_CODE_CROP) {
                Log.e("onCrop", "image");
                Bundle bundle = data.getExtras();
                if (bundle == null) return;
                Bitmap bitmap = bundle.getParcelable(EXTRA_DATA);
                if (bitmap == null) return;
                tmpBitmap = bitmap;
                if (tmpImageView != null) Glide.with(MainActivity.this)
                        .load(bitmap)
                        .into(tmpImageView);
            }
        }
    }

    @Override
    protected void onPause() {
        Log.e("activity", "pause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("activity", "destroy");
        if (mRecentWordAdapter != null) mRecentWordAdapter.releaseTts();
    }

    public void getData() {
        mListTranslation.clear();
        if (dataBaseHelper == null) dataBaseHelper = new DataBaseHelper(this);
        mListTranslation.addAll(dataBaseHelper.getAllTranslate());
        mRecentWordAdapter.notifyDataSetChanged();
        if (mListTranslation.size() == 0) mTxtNodata.setVisibility(View.VISIBLE);
        else mTxtNodata.setVisibility(View.GONE);
    }

    static class TranslateTask extends AsyncTask<String, Void, String> {
        private MainActivity activity;
        boolean useApi;

        public TranslateTask() {
        }

        public TranslateTask(MainActivity activity, boolean useApi) {
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

            try {
                dialog.show();
            } catch (Exception e) {

            }

        }

        @Override
        protected String doInBackground(String... strings) {
            if (useApi) {
                TranslateOptions options = TranslateOptions.newBuilder()
                        .setApiKey(AppContrains.TRANSLATE_API_KEY)
                        .build();
                Translate translate = options.getService();

                try {
                    com.google.cloud.translate.Translation translation = translate.translate(strings[0],
                            Translate.TranslateOption.sourceLanguage(strings[1]),
                            Translate.TranslateOption.targetLanguage(strings[2]));

                    return translation.getTranslatedText();
                } catch (Exception e) {
                    return "";
                }

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

            try {
                dialog.cancel();
            } catch (Exception e) {

            }

            if (!TextUtils.isEmpty(s)) activity.addRecentData(s);
        }
    }

    private void addRecentData(String text_result) {
        if (dataBaseHelper == null) dataBaseHelper = new DataBaseHelper(this);
        Translation translation = new Translation();
        translation.setMark(false);
        translation.setLangSource(mSpLangSource.getSelectedItemPosition());
        translation.setLangDes(mSpLangDes.getSelectedItemPosition());
        translation.setWordSource(mTxtInput.getText().toString().trim());
        translation.setWordDes(text_result);
        dataBaseHelper.addTranslatation(translation);
        mTxtInput.setText("");
        getData();
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

    private void cropImage(Uri source, Bitmap bitmap) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(source, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("scaleUpIfNeeded", true);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_CODE_CROP);
        } catch (ActivityNotFoundException ex) {
            Log.e("error cropImage", ex.getMessage() + "");
        }
    }

    private void showDialogEditBitmap(final Bitmap bitmap) throws FileNotFoundException {
        tmpBitmap = bitmap;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_bitmap, null);
        Spinner mSp = view.findViewById(R.id.sp_lang);
        mSp.setAdapter(mLanguageAdapter);
        mSp.setSelection(AppUtils.getLangSource(MainActivity.this));
        mSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AppUtils.saveLangSoucre(MainActivity.this, i);
                updateLanguageSelection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ImageButton mBtnCrop = view.findViewById(R.id.btn_crop);
        ImageButton mBtnRotate = view.findViewById(R.id.btn_rotate);
        final ImageView imgReview = view.findViewById(R.id.img_translate);
        tmpImageView = imgReview;
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        Glide.with(this).load(bitmap).into(imgReview);
        mBtnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCrop(tmpBitmap);
            }
        });
        mBtnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRotate(tmpBitmap, progressBar, imgReview);
            }
        });

        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        })
                .setNegativeButton(getString(R.string.done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
//                        runOrc(tmpBitmap);
                        new OrcText(MainActivity.this).execute(tmpBitmap);
                    }
                });

        builder.setView(view);
        builder.show();
    }

    private void onClickRotate(Bitmap bitmap, ProgressBar progressBar, ImageView imgReview) {
        progressBar.setVisibility(View.VISIBLE);
        tmpBitmap = rotateBitmap(tmpBitmap);
        Bitmap tmp = tmpBitmap;
        Glide.with(MainActivity.this)
                .load(tmp)
                .into(imgReview);
        progressBar.setVisibility(View.GONE);
    }

    private void onClickCrop(Bitmap bitmap) {
        String tempPath = FileUtils.getTempFile();
        OutputStream outFile = null;
        try {
            outFile = new FileOutputStream(tempPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outFile);
            cropImage(Uri.parse("file://" + tempPath), tmpBitmap);
        } catch (FileNotFoundException e) {
            Log.e("error compress", e.getMessage() + "");
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private String runOrc(final Bitmap bitmap) {
//        return mOrcManager.startRecognize(MainActivity.this, bitmap);
        return "";
    }

    private void showOrcResultDialog(final String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText mTxtResult = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mTxtResult.setLayoutParams(lp);
        if (!TextUtils.isEmpty(msg)) mTxtResult.setText(msg);
        builder.setView(mTxtResult);
        builder.setTitle(R.string.orc_result)
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton(R.string.translate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        String text = mTxtResult.getText().toString().trim();
                        mTxtInput.setText(text);
                        try {
                            translate(text,
                                    getLanguageCode(MainActivity.this, mSpLangSource.getSelectedItemPosition()),
                                    getLanguageCode(MainActivity.this, mSpLangDes.getSelectedItemPosition())
                            );
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
        builder.show();
    }

    static class OrcText extends AsyncTask<Bitmap, Void, String> {

        MainActivity activity;
        ProgressDialog mProgressDialog;

        public OrcText(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(activity.getString(R.string.searching_text));
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            return activity.runOrc(bitmaps[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressDialog.cancel();
            activity.showOrcResultDialog(s);
        }
    }
}
