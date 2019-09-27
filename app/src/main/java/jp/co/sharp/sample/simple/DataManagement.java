package jp.co.sharp.sample.simple;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

public class DataManagement {
    //データを管理するクラス
    private String eventname;
    private String time;
    private String place;
    private String people;
    private String object;
    private String event;

    /*
    public DataManagement(){
        eventname = "";
        time = "";
        place = "";
        people = "";
        object = "";
        event = "";
    }
    */

    //更新
    public void setEventname(String data){
        this.eventname = data;
    }

    public void setTime(String data){
        this.time = data;
    }

    public void setPlace(String data){
        this.place = data;
    }

    public void setPeople(String data){
        this.people = data;
    }

    public void setObject(String data){
        this.object = data;
    }

    public void setEvent(String data){
        this.event = data;
    }

    public String getEventname(){
        return this.eventname;
    }

    public String getTime(){
        return this.time;
    }

    public String getPlace(){
        return this.place;
    }

    public String getPeople(){
        return this.people;
    }

    public String getObject(){
        return this.object;
    }

    public String getEvent(){
        return this.event;
    }

    //DBへデータを挿入
    public void insertDB(SQLiteDatabase sdb){
        ContentValues values = new ContentValues();
        values.put("EVENTNAME",this.eventname);
        values.put("TIME",this.time);
        values.put("PLACE",this.place);
        values.put("PEOPLE",this.people);
        values.put("OBJECT",this.object);
        values.put("EVENT",this.event);
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
