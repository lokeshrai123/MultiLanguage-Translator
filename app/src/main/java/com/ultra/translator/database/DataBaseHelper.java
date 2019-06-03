package com.ultra.translator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ultra.translator.models.Translation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 3/6/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "db.sql";
    private static final String DB_TABLE = "Translation";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LANG_S = "lang_s";
    private static final String COLUMN_LANG_D = "lang_d";
    private static final String COLUMN_WORD_S = "word_s";
    private static final String COLUMN_WORD_D = "word_d";
    private static final String COLUMN_MARK = "mark";

    SQLiteDatabase db;

    public final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " ( " +
            COLUMN_ID + " INTEGER PRIMARY KEY," +
            COLUMN_LANG_S + " INTEGER," +
            COLUMN_LANG_D + " INTEGER," +
            COLUMN_MARK + " INTEGER," +
            COLUMN_WORD_S + " TEXT," +
            COLUMN_WORD_D + " TEXT)";

    public final String DELETE_TABLE = "DROP TABLE IF EXISTS " + DB_TABLE;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE);
        onCreate(db);
    }

    public boolean addTranslatation(Translation translation) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LANG_S, translation.getLangSource());
        values.put(COLUMN_LANG_D, translation.getLangDes());
        values.put(COLUMN_WORD_S, translation.getWordSource());
        values.put(COLUMN_WORD_D, translation.getWordDes());
        values.put(COLUMN_MARK, 0);
        long i = db.insert(DB_TABLE, null, values);
        db.close();
        return i > -1;
    }


    public ArrayList<Translation> getAllTranslate() {
        db = this.getReadableDatabase();
        ArrayList<Translation> arr = new ArrayList();
        String sql = "SELECT * FROM " + DB_TABLE + " ORDER BY " + COLUMN_ID + " DESC";

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Translation translation = new Translation();

            translation.setId((int) cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            translation.setLangSource((int) cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LANG_S)));
            translation.setLangDes((int) cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LANG_D)));
            translation.setWordSource(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORD_S)));
            translation.setWordDes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORD_D)));
            translation.setMark(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MARK)) == 1l);
            arr.add(translation);
        }
        db.close();
        return arr;
    }


    public void markTranslation(Translation translation) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MARK, translation.isMark());
        String whereClause = COLUMN_ID + " =?";
        String[] whereArgs = {translation.getId() + ""};
        db.update(DB_TABLE, values, whereClause, whereArgs);
        db.close();
    }

    public void deleteTranslation(Translation translation) {
        db = this.getWritableDatabase();
        String whereClause = COLUMN_ID + " =?";
        String[] arg = {translation.getId() + ""};
        db.delete(DB_TABLE, whereClause, arg);
        db.close();
    }

    public void deleteAll() {
        db = this.getWritableDatabase();
        db.delete(DB_TABLE, null, null);
        db.close();
    }


    public List<Translation> getFavoriteTranslate() {
        db = this.getReadableDatabase();
        ArrayList<Translation> arr = new ArrayList();
        String[] args = {"1"};
        Cursor cursor = db.query(DB_TABLE, null, COLUMN_MARK + " =?", args, null, null, COLUMN_ID + " DESC");
        while (cursor.moveToNext()) {
            Translation translation = new Translation();
            translation.setId((int) cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            translation.setLangSource((int) cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LANG_S)));
            translation.setLangDes((int) cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LANG_D)));
            translation.setWordSource(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORD_S)));
            translation.setWordDes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORD_D)));
            translation.setMark(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MARK)) == 1l);
            arr.add(translation);
        }
        db.close();
        return arr;
    }
}
