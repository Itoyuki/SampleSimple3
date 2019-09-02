package jp.co.sharp.sample.simple;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TestOpenHelper extends SQLiteOpenHelper{
    //データベースのバージョン
    private static final int DATABASE_VERSION =1;

    //データベース情報を変数に格納
    private static final String DATABASE_NAME = "TestDB.db";
    private static final String TABLE_NAME = "testdb";
    private static final String _ID = "_id";

    public TestOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //db.execSQL("create table talk (" + _ID + " integer primary key autoincrement,talktext text not null)");
        db.execSQL("create table " + TABLE_NAME + " ( " + _ID + " integer primary key autoincrement,EVENTNAME text, TIME text, PLACE text, PEOPLE text, OBJECT text, EVENT text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
}
