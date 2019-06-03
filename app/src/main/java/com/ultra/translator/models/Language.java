package com.ultra.translator.models;

/**
 * Created by HP 6300 Pro on 12/12/2017.
 */

public class Language {
    private String name;
    private String code;
    private int flag;

    public Language() {
    }

    public Language(String name, String code, int flag) {
        this.name = name;
        this.code = code;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
