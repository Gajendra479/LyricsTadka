package com.vianet.lyricstadka.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vianet.lyricstadka.Getter_Setter;

import java.util.ArrayList;

/**
 * Created by editing2 on 23-Nov-17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "lyricsManager";

    // Contacts table name
    private static final String TABLE_LYRICS = "lyrics";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_HEAD = "head";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TEXT = "text";
    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_LYRICS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_DESCRIPTION + " TEXT, " + KEY_TEXT + " TEXT, " + KEY_HEAD + " TEXT, " + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    public void insertLyrics(String desc, String title, String subcatname) {

        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, desc);
        values.put(KEY_TEXT, title);
        values.put(KEY_HEAD, subcatname);
        db.insert(TABLE_LYRICS, null, values);
        db.close();

    }

    public void deleteDataFromLyrics(String title_id) {

        String deleteQuerry = "DELETE FROM " + TABLE_LYRICS + " WHERE " + KEY_TEXT + "= '" + title_id + "'";
        db = this.getWritableDatabase();
        db.execSQL(deleteQuerry);
        db.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String querry = "DROP TABLE IF EXIST" + TABLE_LYRICS;
        db.execSQL(querry);
        this.onCreate(db);

    }

    public ArrayList<Getter_Setter> selectData() {
        ArrayList<Getter_Setter> list = new ArrayList<>();
        try {

            db = this.getReadableDatabase();
//            Cursor curser = db.rawQuery("SELECT * FROM " + TABLE_LYRICS + " ORDER BY " + KEY_TEXT + " ASC ", null);
            Cursor curser = db.rawQuery("SELECT * FROM " + TABLE_LYRICS + " ORDER BY " + KEY_ID + " DESC ", null);
            if (curser.moveToFirst()) {
                do {

                    try {


                        Getter_Setter data = new Getter_Setter();

                        data.setId(curser.getString(0));
                        data.setDescription(curser.getString(1));
                        data.setText(curser.getString(2));
                        data.setHead(curser.getString(3));
                        list.add(data);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (curser.moveToNext());

            }
            curser.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
