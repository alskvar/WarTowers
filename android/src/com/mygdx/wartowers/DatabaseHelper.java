package com.mygdx.wartowers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper implements DatabaseInterface {
    private static final String DATABASE_NAME = "wartowers.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS scores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "score INTEGER," +
                "player_name TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade policy here
    }

    public void addScore(String playerName, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("player_name", playerName);
        values.put("score", score);

        db.insert("scores", null, values);
        db.close();
    }

    @SuppressLint("Range")
    @Override
    public ScoreEntry getScore(String playerName) {
        List<ScoreEntry> scoreList = new ArrayList<>();
        String selectQuery = "SELECT * FROM scores WHERE player_name=" + playerName + " ORDER BY score DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ScoreEntry score = new ScoreEntry();
                score.setId(cursor.getInt(cursor.getColumnIndex("id")));
                score.setPlayerName(cursor.getString(cursor.getColumnIndex("player_name")));
                score.setScore(cursor.getInt(cursor.getColumnIndex("score")));
                scoreList.add(score);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return scoreList.get(0);
    }

}
