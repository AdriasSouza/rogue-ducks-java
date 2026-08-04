package com.example.rogueducks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "rogueducks.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_RANKING = "ranking";
    public static final String COL_ID = "id";
    public static final String COL_NOME = "nome";
    public static final String COL_PONTUACAO = "pontuacao";
    public static final String COL_ONDA = "onda";
    public static final String COL_DATA = "data";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_RANKING + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOME + " TEXT, " +
                COL_PONTUACAO + " INTEGER, " +
                COL_ONDA + " INTEGER, " +
                COL_DATA + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RANKING);
        onCreate(db);
    }

    public void inserirPontuacao(String nome, int pontuacao, int onda) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_NOME, (nome == null || nome.trim().isEmpty()) ? "Jogador" : nome);
            values.put(COL_PONTUACAO, pontuacao);
            values.put(COL_ONDA, onda);
            db.insert(TABLE_RANKING, null, values);
        } finally {
            db.close();
        }
    }

    public List<String> getTop10() {
        List<String> ranking = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_RANKING, 
                    new String[]{COL_NOME, COL_PONTUACAO, COL_ONDA}, 
                    null, null, null, null, 
                    COL_PONTUACAO + " DESC", "10");

            if (cursor.moveToFirst()) {
                do {
                    String nome = cursor.getString(0);
                    int score = cursor.getInt(1);
                    int onda = cursor.getInt(2);
                    ranking.add(nome + " - " + score + " pts (Onda " + onda + ")");
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return ranking;
    }
}