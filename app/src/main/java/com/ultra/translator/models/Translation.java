package com.ultra.translator.models;

import android.util.Log;

/**
 * Created by HP 6300 Pro on 12/12/2017.
 */

public class Translation {

    private int id;
    private int langSource;
    private int langDes;
    private String wordSource;
    private String wordDes;
    private boolean isMark;

    public Translation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLangSource() {
        return langSource;
    }

    public void setLangSource(int langSource) {
        this.langSource = langSource;
    }

    public int getLangDes() {
        return langDes;
    }

    public void setLangDes(int langDes) {
        this.langDes = langDes;
    }

    public String getWordSource() {
        return wordSource;
    }

    public void setWordSource(String wordSource) {
        this.wordSource = wordSource;
    }

    public String getWordDes() {
        return wordDes;
    }

    public void setWordDes(String wordDes) {
        this.wordDes = wordDes;
    }

    public boolean isMark() {
        return isMark;
    }

    public void setMark(boolean mark) {
        isMark = mark;
    }

    public void print() {
        Log.e("translation", id + "\n" + langSource + "\n" + langDes + "\n" + wordSource + "\n" + wordDes + "\n" + isMark);
    }
}
