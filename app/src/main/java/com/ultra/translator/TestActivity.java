package com.ultra.translator;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

//import com.telpoo.frame.utils.Mlog;

import java.util.ArrayList;
import java.util.List;

//import edu.sfsu.cs.orange.ocr.CaptureActivity;
//import edu.sfsu.cs.orange.ocr.ChooseImageActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //startCaptureInput();

//        startPhotoInput();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 100)
                && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String kq = bundle.getString("result_input");

                Log.e("abc", kq);
            }
        }
    }


//    public void startCaptureInput() {
//        Intent in = new Intent(getApplicationContext(), CaptureActivity.class);
//        in.putExtra("lang", "vie");
//        startActivityForResult(in, 100);
//
//    }
//
//    public void startPhotoInput() {
//        Intent in = new Intent(getApplicationContext(), ChooseImageActivity.class);
//        in.putExtra("lang", "vie");
//        startActivityForResult(in, 100);
//    }


    public boolean checkHasEngineRecognizerSpeechToText(Activity ins) {
        PackageManager pm = ins.getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (activities.size() != 0) {
            return true;
        } else {
            return false;
        }
    }

//    public void startActionRecognizerSpeechToText(boolean language) {
//
//        // check available
//
//        if (!checkHasEngineRecognizerSpeechToText(this)) {
//            Mlog.E("Recognizer not present");
//
//            //ToastUtils.showToast("Recognizer not present", ins);
//            return;
//        }
//
//        String sLanguageShort = "", sLanguageFull = "";
//        if (language) {
//            sLanguageShort = GlobalResources.CODE_LAN_FOREIGN;
//            sLanguageFull = GlobalResources.CODE_LAN_FOREIGN_NAME;
//        } else {
//            sLanguageShort = GlobalResources.CODE_LAN_VI;
//            sLanguageFull = GlobalResources.CODE_LAN_VI_NAME;
//        }
//
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Language: " + sLanguageFull);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, sLanguageShort);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, sLanguageShort);
//        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,
//                sLanguageShort);
//
//        startActivityForResult(intent, GlobalResources.REQUEST_CODE.RECOGNIZER_VOICE_INPUT);
//    }
}
