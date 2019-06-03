package com.ultra.translator.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.ultra.translator.AppContrains;
import com.ultra.translator.R;
import com.ultra.translator.models.Language;
import com.ultra.translator.views.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by HP 6300 Pro on 12/14/2017.
 */

public class AppUtils {

    private static final String PRE_NAME = "values";
    private static final String KEY_LANG_SOURCE = "souce";
    private static final String KEY_LANG_DES = "des";

    public static void saveLangSoucre(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_LANG_SOURCE, value);
        editor.commit();
    }

    public static int getLangSource(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_LANG_SOURCE, getDefaultSourceLang());
    }

    private static int getDefaultSourceLang() {
        if (Locale.getDefault().getLanguage().contains("vi")) return 5;
        return 5;
    }

    public static void saveLangDes(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_LANG_DES, value);
        editor.commit();
    }

    public static int getLangDes(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_LANG_DES, getDefaultDesLang());
    }

    private static int getDefaultDesLang() {
        if (Locale.getDefault().getLanguage().contains("vi")) return 37;
        else if (Locale.getDefault().getLanguage().contains("en")) return 6;
        return 0;
    }

    //    share plantext
    public static void shareText(Context ctx, String text, String title) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
        sharingIntent.setType("text/plain");
        ctx.startActivity(Intent.createChooser(sharingIntent, title));
    }

    //    share file
    public static void shareVoice(Context ctx, String sharePath) {
        File file = new File(sharePath);
        if (file.exists()) Log.e("file", "exits");
        Uri uri = Uri.parse("file://" + sharePath);
        Log.e("uri", uri.getPath());
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("*/*");
        share.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        ctx.startActivity(Intent.createChooser(share, "Share Voice File"));
    }

    public static String getStringOnArray(Context context, int sourceID, int pos) {
        return context.getResources().getStringArray(sourceID)[pos];
    }

    public static String getLanguage(Context context, int pos) {
        return getStringOnArray(context, R.array.language, pos);
    }

    public static String getLanguageCode(Context context, int pos) {
        return getStringOnArray(context, R.array.language_code, pos);
    }

    public static int getFlag(int pos) {
        return AppContrains.flag[pos];
    }

    public static String getTtsCode(Context context, int pos) {
        return getStringOnArray(context, R.array.locale, pos);
    }

    public static String getSttCode(Context context, int pos) {
        return getStringOnArray(context, R.array.lang_code, pos);
    }

    public static ArrayList<Language> getAllLanguage(Context context) {
        ArrayList<Language> languages = new ArrayList<>();
        String[] lang = context.getResources().getStringArray(R.array.language);
        for (int i = 0; i < lang.length; i++) {
            languages.add(new Language(lang[i], getLanguageCode(context, i), i));
        }
        return languages;
    }

    public static void goToStore(Context context, String appId) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppContrains.MARKET_DETAILS_ID + appId)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppContrains.PLAY_STORE_LINK + appId)));
        }
    }

    public static void createShortcut(Context context) {
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutIntent.putExtra("duplicate", false);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher));
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(context, MainActivity.class));
        context.sendBroadcast(shortcutIntent);
    }

    public static boolean checkNetworkConnect(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null;
    }

    public static boolean checkInternetConnect(Context context) {
        if (!checkNetworkConnect(context)) return false;
//        try {
//            InetAddress inetAddress = InetAddress.getByName("google.com");
//            return !inetAddress.equals("");
//        } catch (UnknownHostException e) {
//            Log.e("checkInternet", "not connect");
//            return false;
//        }
        return true;
    }

    public static void showDialogInternetRequest(final Context context, String mss) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_no_internet)
                .setMessage(mss)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.show();
    }

    public static boolean requestInternet(Context context, String mess) {
        if (!checkInternetConnect(context)) {
            showDialogInternetRequest(context, mess);
            return false;
        }
        return true;
    }

    public static void checkcontain() {
//        lang of tts
        String[] lang = {"ar-SA", "bn_BD", "zh_CN", "da_DK", "nl_NL", "en_US", "fr_FR", "de_DE", "el_GR", "gu-IN", "he-IL", "hi_IN", "hu_HU", "in_ID", "it_IT", "ja_JP", "kn-IN", "km_KH", "ko_KR", "lo-LA", "ms-MY", "ml-IN", "mr-IN", "ne_NP", "fa-IR", "pl_PL", "pt_PT", "Punjabi", "ru_RU", "si_LK", "es_ES", "ta-LK", "te-IN", "th_TH", "tr_TR", "uk_UA", "ur-PK", "vi_VN"};
//        lang of stt
//        String[] lang = {"ar-SA", "bn-BD", "cmn-Hans-CN", "da-DK", "nl-NL", "en-US", "fr-FR", "de-DE", "el-GR", "gu-IN", "he-IL", "hi-IN", "hu-HU", "id-ID", "it-IT", "ja-JP", "kn-IN", "km-KH", "ko-KR", "lo-LA", "ms-MY", "ml-IN", "mr-IN", "ne-NP", "fa-IR", "pl-PL", "pt-PT", "Punjabi", "ru-RU", "si-LK", "es-ES", "ta-LK", "te-IN", "th-TH", "tr-TR", "uk-UA", "ur-PK", "vi-VN"};
        //        langfromserve of stt
//       String langfromServe = "[af-ZA, az-AZ, id-ID, ms-MY, jv-ID, su-ID, ca-ES, cs-CZ, da-DK, de-DE, en-AU, en-CA, en-001, en-GH, en-IN, en-IE, en-KE, en-NZ, en-NG, en-PH, en-ZA, en-TZ, en-GB, en-US, es-AR, es-BO, es-CL, es-CO, es-CR, es-EC, es-US, es-SV, es-ES, es-GT, es-HN, es-MX, es-NI, es-PA, es-PY, es-PE, es-PR, es-DO, es-UY, es-VE, eu-ES, fil-PH, fr-FR, fr-CA, gl-ES, hr-HR, zu-ZA, is-IS, it-IT, sw, sw-TZ, lv-LV, lt-LT, hu-HU, nl-NL, nb-NO, pl-PL, pt-BR, pt-PT, ro-RO, sl-SI, sk-SK, fi-FI, sv-SE, vi-VN, tr-TR, el-GR, bg-BG, ru-RU, sr-RS, uk-UA, ka-GE, hy-AM, he-IL, ar-IL, ar-JO, ar-AE, ar-BH, ar-DZ, ar-SA, ar-KW, ar-MA, ar-TN, ar-OM, ar-PS, ar-QA, ar-LB, ar-EG, fa-IR, ur-PK, ur-IN, am-ET, hi-IN, ta-IN, ta-LK, ta-SG, ta-MY, bn-BD, bn-IN, km-KH, kn-IN, mr-IN, gu-IN, si-LK, te-IN, ml-IN, ne-NP, lo-LA, th-TH, ko-KR, cmn-Hans-CN, cmn-Hans-HK, cmn-Hant-TW, yue-Hant-HK, ja-JP]";
        //        langfromserve of stt
        String langfromServe = "[ku, pt_BR, ta, ja_JP, tr_TR, ru_RU, ko_KR, hi_IN, fil_PH, fi_FI, ca, hr, es_ES, sk, zh_TW, sw, el_GR, fr_BE, en_GB, nl_NL, la, pt_PT, fr_FR, bs, km_KH, vi_VN, en_AU, pl_PL, da_DK, sq, cy, en_US, bn_IN, si_LK, in_ID, yue_HK, uk_UA, bn_BD, sr, en_IN, cs_CZ, it_IT, ne_NP, de_DE, es_US, zh_CN, th_TH, sv_SE, nb_NO, hu_HU]";
        for (int i = 0; i < lang.length; i++) {
            if (langfromServe.contains(lang[i])) Log.e("contain", lang[i]);
            else Log.e("!contain", lang[i]);
        }
    }
}
