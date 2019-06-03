package com.ultra.translator.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ultra.translator.AppContrains;
import com.ultra.translator.R;
import com.ultra.translator.database.DataBaseHelper;
import com.ultra.translator.listener.UnMarkListener;
import com.ultra.translator.models.Translation;
import com.ultra.translator.utils.AppUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by HP 6300 Pro on 12/14/2017.
 */

public class RecentWordAdapter extends RecyclerView.Adapter<RecentWordAdapter.MyViewHolder> {

    private List<Translation> mList;
    private Context mContext;
    private TextToSpeech mTts;

    private DataBaseHelper mDataBaseHelper;
    private UnMarkListener mListener;

    public RecentWordAdapter(Context mContext, List<Translation> translations) {
        this.mList = translations;
        this.mContext = mContext;
        mTts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {

                    if (mTts != null) {
                        mTts.setLanguage(Locale.US);
                    }

                }
            }
        });

        mDataBaseHelper = new DataBaseHelper(mContext);
    }

    public void setUnMarkListener(UnMarkListener listener) {
        this.mListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recent, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Translation translation = mList.get(position);
        holder.mTxtTitleS.setText(AppUtils.getLanguage(mContext, translation.getLangSource()));
        holder.mTxtTitleD.setText(AppUtils.getLanguage(mContext, translation.getLangDes()));
        holder.mTxtWordS.setText(translation.getWordSource());
        holder.mTxtWordD.setText(translation.getWordDes());
        Glide.with(mContext).load(AppUtils.getFlag(mList.get(position).getLangSource()))
                .into(holder.mImgFlagS);
        Glide.with(mContext).load(AppUtils.getFlag(mList.get(position).getLangDes()))
                .into(holder.mImgFlagD);
        if (translation.isMark())
            holder.mBtnMark.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_star_fill));
        else
            holder.mBtnMark.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_star));

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                switch (id) {
                    case R.id.btn_copy_s:
                        copy(translation.getWordSource());
                        break;
                    case R.id.btn_copy_d:
                        copy(translation.getWordDes());
                        break;
                    case R.id.btn_share_s:
                        share(translation.getWordSource());
                        break;
                    case R.id.btn_share_d:
                        share(translation.getWordDes());
                        break;
                    case R.id.btn_speak_source:
                        speak(translation.getWordSource(), translation.getLangSource());
                        break;
                    case R.id.btn_speak_des:
                        speak(translation.getWordDes(), translation.getLangDes());
                        break;
                    case R.id.btn_close:
                        delele(position);
                        break;
                    case R.id.btn_mark:
                        mark(view, position);
                        break;
                    default:
                        break;
                }
            }
        };

        holder.mBtnCopyS.setOnClickListener(onClickListener);
        holder.mBtnCopyD.setOnClickListener(onClickListener);
        holder.mBtnShareS.setOnClickListener(onClickListener);
        holder.mBtnShareD.setOnClickListener(onClickListener);
        holder.mBtnSpeakS.setOnClickListener(onClickListener);
        holder.mBtnSpeakD.setOnClickListener(onClickListener);
        holder.mBtnClose.setOnClickListener(onClickListener);
        holder.mBtnMark.setOnClickListener(onClickListener);
    }

    private void speak(String text, int nation_code) {
        String s = AppUtils.getTtsCode(mContext, nation_code);
        if (!TextUtils.isEmpty(s) && s.contains("_")) {
            if (!s.contains("en")) {
                if (!checkAppTtsInstalled()) showDialogInstallTTS();
            }
            Locale loc = new Locale(s);

//            checkLangSupport(loc);

            mTts.setLanguage(loc);
            mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else
            Toast.makeText(mContext, R.string.not_support_tts_language, Toast.LENGTH_SHORT).show();
    }

    //check language support by texttospeech
    private void checkLangSupport(Locale loc) {
        mTts.isLanguageAvailable(loc);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Set<Locale> kqs = mTts.getAvailableLanguages();

            Log.e("abc", kqs.toString());
        }
    }

    private boolean checkAppTtsInstalled() {
        PackageManager manager = mContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(AppContrains.GOOGLE_TEXT_TO_SPEECH, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showDialogInstallTTS() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle(R.string.title_missing_packet)
                .setMessage(R.string.mss_install_tts)
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
                        AppUtils.goToStore(mContext, AppContrains.GOOGLE_TEXT_TO_SPEECH);
                    }
                });
        builder.show();
    }

    private void copy(String text) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(mContext, "copy", Toast.LENGTH_SHORT).show();
    }

    private void share(final String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Share")
                .setMessage("Share translate")
                .setPositiveButton("Share Text", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AppUtils.shareText(mContext, text, mContext.getString(R.string.share_translate_text));
                    }
                })
                .setNegativeButton("Share Voice", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        rendTexttoAudio(text);
                    }
                });
        builder.show();
    }

    private void delele(final int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle(R.string.title_confirm_delete)
                .setMessage(R.string.mss_delete)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        mDataBaseHelper.deleteTranslation(mList.get(position));
                        mList.remove(position);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.show();
    }

    private void mark(View view, int position) {
        Log.e("mark pós", position + "");
        Translation translation = mList.get(position);
        translation.setMark(!translation.isMark());
        mDataBaseHelper.markTranslation(translation);
        if (translation.isMark()) {
            ((ImageButton) view).setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_star_fill));
        } else {
            ((ImageButton) view).setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_star));
        }
        if (mListener != null) mListener.onUnMark(position);
    }

    public void rendTexttoAudio(String text) {

        if (!TextUtils.isEmpty(text) && text.contains("_")) {
            if (!text.contains("en")) {
                if (!checkAppTtsInstalled()) showDialogInstallTTS();
            }
            Locale loc = new Locale(text);

//            checkLangSupport(loc);

            mTts.setLanguage(loc);
//            if (!mTts.isSpeaking()) mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            Toast.makeText(mContext, R.string.not_support_tts_language, Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> hashMap = new HashMap();
        String destFileName = Environment.getExternalStorageDirectory() + "/Download/" + text + ".wav";
        hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);
        mTts.synthesizeToFile(text, hashMap, destFileName);
        AppUtils.shareVoice(mContext, destFileName);
    }

    public void releaseTts() {
        if (mTts == null) return;
        mTts.stop();
        mTts.shutdown();
        mTts = null;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTxtTitleS, mTxtTitleD, mTxtWordS, mTxtWordD;
        private ImageButton mBtnCopyS, mBtnCopyD, mBtnShareS, mBtnShareD, mBtnSpeakS, mBtnSpeakD, mBtnClose, mBtnMark;
        private ImageView mImgFlagS, mImgFlagD;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxtTitleS = itemView.findViewById(R.id.txt_title_souce);
            mTxtTitleD = itemView.findViewById(R.id.txt_title_des);
            mTxtWordS = itemView.findViewById(R.id.txt_source_word);
            mTxtWordD = itemView.findViewById(R.id.txt_des_word);
            mBtnCopyS = itemView.findViewById(R.id.btn_copy_s);
            mBtnCopyD = itemView.findViewById(R.id.btn_copy_d);
            mBtnShareS = itemView.findViewById(R.id.btn_share_s);
            mBtnShareD = itemView.findViewById(R.id.btn_share_d);
            mBtnSpeakS = itemView.findViewById(R.id.btn_speak_source);
            mBtnSpeakD = itemView.findViewById(R.id.btn_speak_des);
            mBtnClose = itemView.findViewById(R.id.btn_close);
            mBtnMark = itemView.findViewById(R.id.btn_mark);
            mImgFlagS = itemView.findViewById(R.id.img_flag_source);
            mImgFlagD = itemView.findViewById(R.id.img_flag_des);
        }
    }
}
