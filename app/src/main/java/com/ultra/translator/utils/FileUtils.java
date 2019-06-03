package com.ultra.translator.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by HP 6300 Pro on 12/14/2017.
 */

public class FileUtils {

    private static final String APP_DIR = Environment.getExternalStorageDirectory() + "/TranSlator";
    private static final String PARENT_DATA_DIR = APP_DIR + "/data";
    private static final String DATA_DIR = PARENT_DATA_DIR + "/tessdata";

    public static boolean createaAppDir() {
        File file = new File(APP_DIR);
        return file.exists() || file.mkdir();
    }

    public static boolean createaDir(String dir) {
        File file = new File(dir);
        return file.exists() || file.mkdir();
    }

    public static boolean createTrainningDir() {
        if (createaDir(PARENT_DATA_DIR))
            return createaDir(DATA_DIR);
        else return false;
    }

    public static String getAppDir() {
        return APP_DIR;
    }

    public static String getDataDir() {
        return DATA_DIR;
    }

    public static void copyFile(File source, File des) {
        try {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(des);
            copyFile(in, out);
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static void copyFile(String sourcePath, String desPath) {
        File sourceFile = new File(sourcePath);
        File desFile = new File(desPath);
        copyFile(sourceFile, desFile);
    }

    public static String getParentDataDir() {
        return PARENT_DATA_DIR;
    }

    public static String getTempFile() {
        return APP_DIR + "/tmp.png";
    }
}
