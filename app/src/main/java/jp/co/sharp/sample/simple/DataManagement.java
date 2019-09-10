package jp.co.sharp.sample.simple;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

public class DataManagement {
    //データを管理するクラス
    String eventname;
    String time;
    String place;
    String people;
    String object;
    String event;

    public DataManagement(){
        eventname = "江戸時代";
        time = "1603年";
        place = "江戸";
        people = "徳川家康";
        object = "江戸幕府";
        event = "開いた";
    }

    //更新
    public void setEventname(String data){
        eventname = data;
    }

    public void setTime(String data){
        time = data;
    }

    public void setPlace(String data){
        place = data;
    }

    public void setPeople(String data){
        people = data;
    }

    public void setObject(String data){
        object = data;
    }

    public void setEvent(String data){
        event = data;
    }

    //DBへデータを挿入
    public void insertDB(SQLiteDatabase sdb){
        ContentValues values = new ContentValues();
        values.put("EVENTNAME",eventname);
        values.put("TIME",time);
        values.put("PLACE",place);
        values.put("PEOPLE",people);
        values.put("OBJECT",object);
        values.put("EVENT",event);
        sdb.insert("talk", null, values);
    }

    //DBからデータを取り出す
    public void pullDB(SQLiteDatabase sdbr){
        final String[] columns = new String[]{"TIME","PLACE","PEOPLE","OBJECT","EVENT"};
        Cursor c = sdbr.query("talk",columns,null,null,null,null,null);
        //String moji = "";
        //String moji1 = "";
        //String moji2 = "";
        //String moji3 = "";
        //String moji4 = "";
        //String moji5 = "";
        while (c.moveToNext()) {
            String moji1 = c.getString(c.getColumnIndex("TIME"));
            String moji2 = c.getString(c.getColumnIndex("PLACE"));
            String moji3 = c.getString(c.getColumnIndex("PEOPLE"));
            String moji4 = c.getString(c.getColumnIndex("OBJECT"));
            String moji5 = c.getString(c.getColumnIndex("EVENT"));
            //((TextView)findViewById(R.id.recog_eventname_text)).setText("Lvcsr:" + moji1 + "に" + moji2 + "で" + moji3 + "が" + moji4 + "を" + moji5 + "。");
            //moji = moji1 + "に" + moji2 + "で" + moji3 + "が" + moji4 + "を" + moji5 + "。";
        }
    }
}
