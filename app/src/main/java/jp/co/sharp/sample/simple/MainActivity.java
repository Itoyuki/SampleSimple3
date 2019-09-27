package jp.co.sharp.sample.simple;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import jp.co.sharp.android.voiceui.VoiceUIManager;
import jp.co.sharp.android.voiceui.VoiceUIVariable;
import jp.co.sharp.sample.simple.customize.ScenarioDefinitions;
import jp.co.sharp.sample.simple.util.VoiceUIManagerUtil;
import jp.co.sharp.sample.simple.util.VoiceUIVariableUtil;
import jp.co.sharp.sample.simple.util.VoiceUIVariableUtil.VoiceUIVariableListHelper;


/**
 * 音声UIを利用した基本的な機能だけ実装したActivity.
 */
public class MainActivity extends Activity implements MainActivityVoiceUIListener.MainActivityScenarioCallback {
    public static final String TAG = MainActivity.class.getSimpleName();

    /**
     * 音声UI制御.
     */
    private VoiceUIManager mVoiceUIManager = null;
    /**
     * 音声UIイベントリスナー.
     */
    private MainActivityVoiceUIListener mMainActivityVoiceUIListener = null;
    /**
     * 音声UIの再起動イベント検知.
     */
    private VoiceUIStartReceiver mVoiceUIStartReceiver = null;
    /**
     * ホームボタンイベント検知.
     */
    private HomeEventReceiver mHomeEventReceiver;
    /**
     * UIスレッド処理用.
     */
    private Handler mHandler = new Handler();
    /**
     * データベース用
     */
    private TestOpenHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {
            //ホームシナリオから"mode"の値を受け取る.
            String modeVal = intent.getStringExtra("mode");
            if (modeVal != null) {
                ((TextView)findViewById(R.id.mode_value)).setText(modeVal);
            }
            //ホームシナリオから任意の値を受け取る.
            List<VoiceUIVariable> variables = intent.getParcelableArrayListExtra("VoiceUIVariable");
            if (variables != null) {
                String test1 = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_TEST_1);
                String test2 = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_TEST_2);
                ((TextView)findViewById(R.id.test_value)).setText(test1 + ", " + test2);
            }else{
                Log.d(TAG, "VoiceUIVariable is null");
            }
        }

        // accostボタン
        Button voiceAccostButton = (Button)findViewById(R.id.voice_accost_button);
        voiceAccostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVoiceUIManager != null) {
                    VoiceUIVariableListHelper helper = new VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_ACCOST);
                    VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
                }
            }
        });

        // resolve variableボタン
        Button resolveButton = (Button)findViewById(R.id.resolve_variable_button);
        resolveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVoiceUIManager != null) {
                    VoiceUIVariableListHelper helper = new VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_RESOLVE);
                    VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
                }
            }
        });

        // set memory_pボタン
        Button getMemoryPButton = (Button)findViewById(R.id.set_memoryP);
        getMemoryPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();

                final String hour = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
                final String minute = String.valueOf(now.get(Calendar.MINUTE));
                int ret = VoiceUIVariableUtil.setVariableData(mVoiceUIManager, ScenarioDefinitions.MEM_P_HOUR, hour);
                if(ret == VoiceUIManager.VOICEUI_ERROR){
                    Log.d(TAG, "setVariableData:VARIABLE_REGISTER_FAILED");
                }
                ret = VoiceUIVariableUtil.setVariableData(mVoiceUIManager, ScenarioDefinitions.MEM_P_MINUTE, minute);
                if(ret == VoiceUIManager.VOICEUI_ERROR){
                    Log.d(TAG, "setVariableData:VARIABLE_REGISTER_FAILED");
                }
                String text = "Set " + hour + ":" + minute;
                TextView textSetting = (TextView)findViewById(R.id.ViewTime);
                textSetting.setText(text);
            }
        });

        // get memory_pボタン
        Button setMemoryPButton = (Button)findViewById(R.id.get_memoryP);
        setMemoryPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVoiceUIManager != null) {
                    VoiceUIVariableListHelper helper = new VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_GET_MEMORYP);
                    VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
                }
            }
        });

        // finish app：アプリ終了ボタン
        Button finishAppButton = (Button)findViewById(R.id.finish_app_button);
        finishAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVoiceUIManager != null) {
                    VoiceUIVariableListHelper helper = new VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_END_APP);
                    VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
                }
            }
        });

        //ホームボタンの検知登録.
        mHomeEventReceiver = new HomeEventReceiver();
        IntentFilter filterHome = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeEventReceiver, filterHome);

        //VoiceUI再起動の検知登録.
        mVoiceUIStartReceiver = new VoiceUIStartReceiver();
        IntentFilter filter = new IntentFilter(VoiceUIManager.ACTION_VOICEUI_SERVICE_STARTED);
        registerReceiver(mVoiceUIStartReceiver, filter);

        //DB生成
        dbhelper = new TestOpenHelper(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");

        //VoiceUIManagerのインスタンス取得.
        if (mVoiceUIManager == null) {
            mVoiceUIManager = VoiceUIManager.getService(getApplicationContext());
        }
        //MainActivityVoiceUIListener生成.
        if (mMainActivityVoiceUIListener == null) {
            mMainActivityVoiceUIListener = new MainActivityVoiceUIListener(this);
        }
        //VoiceUIListenerの登録.
        VoiceUIManagerUtil.registerVoiceUIListener(mVoiceUIManager, mMainActivityVoiceUIListener);

        //Scene有効化.
        VoiceUIManagerUtil.enableScene(mVoiceUIManager, ScenarioDefinitions.SCENE_COMMON);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");

        //バックに回ったら発話を中止する.
        VoiceUIManagerUtil.stopSpeech();

        //VoiceUIListenerの解除.
        VoiceUIManagerUtil.unregisterVoiceUIListener(mVoiceUIManager, mMainActivityVoiceUIListener);

        //Scene無効化.
        VoiceUIManagerUtil.disableScene(mVoiceUIManager, ScenarioDefinitions.SCENE_COMMON);

        //単一Activityの場合はonPauseでアプリを終了する.
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");

        //ホームボタンの検知破棄.
        this.unregisterReceiver(mHomeEventReceiver);

        //VoiceUI再起動の検知破棄.
        this.unregisterReceiver(mVoiceUIStartReceiver);

        //インスタンスのごみ掃除.
        mVoiceUIManager = null;
        mMainActivityVoiceUIListener = null;
    }

    /**
     * VoiceUIListenerクラスからのコールバックを実装する.
     */
    @Override
    public void onExecCommand(String command, List<VoiceUIVariable> variables) {
        Log.v(TAG, "onExecCommand() : " + command);

        final SQLiteDatabase sdb = dbhelper.getWritableDatabase();
        final SQLiteDatabase sdbr = dbhelper.getReadableDatabase();
        final ContentValues values = new ContentValues();

        final String eventname = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_BASIC);
        final String time = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_TIME);
        final String place = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_PLACE);
        final String people = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_PEOPLE);
        final String object = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_OBJECT);
        final String event = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_EVENT);

        //final DataManagement management = new DataManagement();

        switch (command) {
            case ScenarioDefinitions.FUNC_END_APP:
                finish();
                break;
            case ScenarioDefinitions.FUNC_RECOG_TALK:
                //final String lvcsr = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_BASIC);
                //final String eventname = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_BASIC);
                //final DataManagement management = new DataManagement();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            //final ContentValues values = new ContentValues();
                            //values.put("EVENTNAME",eventname);
                            //sdb.insert("talk", null, values);
                            //final DataManagement management = new DataManagement();
                            //management.setEventname(eventname);
                            /*
                            final String[] columns = new String[]{"EVENTNAME"};
                            Cursor c = sdbr.query("talk",columns,null,null,null,null,null);
                            while (c.moveToNext()) {
                                String moji1 = c.getString(c.getColumnIndex("EVENTNAME"));
                                ((TextView) findViewById(R.id.recog_eventname_text)).setText("Lvcsr:"+ moji1);
                            }
                            */
                            //((TextView) findViewById(R.id.recog_text)).setText("Lvcsr:"+lvcsr);
                            ((TextView) findViewById(R.id.recog_time_text)).setText("あああああ:" + ScenarioDefinitions.KEY_LVCSR_BASIC);
                        }
                    }
                });
                break;
            case ScenarioDefinitions.FUNC_RECOG_TIME:
                //final String lvcsr = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_BASIC);
                //final String time = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_TIME);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            //final ContentValues values = new ContentValues();
                            //values.put("TIME",time);
                            //sdb.insert("talk", null, values);
                            //final DataManagement management = new DataManagement();
                            //management.setTime(time);
                            /*
                            final String[] columns = new String[]{"TIME"};
                            Cursor c = sdbr.query("talk",columns,null,null,null,null,null);
                            while (c.moveToNext()) {
                                String moji2 = c.getString(c.getColumnIndex("TIME"));
                                ((TextView) findViewById(R.id.recog_time_text)).setText("Lvcsr:"+ moji2);
                            }
                            */
                            //((TextView) findViewById(R.id.recog_text)).setText("Lvcsr:"+lvcsr);
                            ((TextView) findViewById(R.id.recog_place_text)).setText("いいいいい:");
                        }
                    }
                });
                break;
            case ScenarioDefinitions.FUNC_RECOG_PLACE:
                //final String lvcsr = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_BASIC);
                //final String place = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_PLACE);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            //final ContentValues values = new ContentValues();
                            //values.put("PLACE",place);
                            //sdb.insert("talk", null, values);
                            //final DataManagement management = new DataManagement();
                            //management.setPlace(place);
                            /*
                            final String[] columns = new String[]{"PLACE"};
                            Cursor c = sdbr.query("talk",columns,null,null,null,null,null);
                            while (c.moveToNext()) {
                                String moji3 = c.getString(c.getColumnIndex("PLACE"));
                                ((TextView) findViewById(R.id.recog_place_text)).setText("Lvcsr:"+ moji3);
                            }
                            */
                            //((TextView) findViewById(R.id.recog_text)).setText("Lvcsr:"+lvcsr);
                            ((TextView) findViewById(R.id.recog_people_text)).setText("ううううう:");
                        }
                    }
                });
                break;
            case ScenarioDefinitions.FUNC_RECOG_PEOPLE:
                //final String lvcsr = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_BASIC);
                //final String people = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_PEOPLE);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            //final ContentValues values = new ContentValues();
                            //values.put("PEOPLE",people);
                            //sdb.insert("talk", null, values);
                            //final DataManagement management = new DataManagement();
                            //management.setPeople(people);
                            /*
                            final String[] columns = new String[]{"PEOPLE"};
                            Cursor c = sdbr.query("talk",columns,null,null,null,null,null);
                            while (c.moveToNext()) {
                                String moji4 = c.getString(c.getColumnIndex("PEOPLE"));
                                ((TextView) findViewById(R.id.recog_people_text)).setText("Lvcsr:"+ moji4);
                            }
                            */
                            //((TextView) findViewById(R.id.recog_text)).setText("Lvcsr:"+lvcsr);
                            ((TextView) findViewById(R.id.recog_object_text)).setText("えええええ:");
                        }
                    }
                });
                break;
            case ScenarioDefinitions.FUNC_RECOG_OBJECT:
                //final String lvcsr = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_BASIC);
                //final String object = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_OBJECT);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            //final ContentValues values = new ContentValues();
                            //values.put("OBJECT",object);
                            //sdb.insert("talk", null, values);
                            //final DataManagement management = new DataManagement();
                            //management.setObject(object);
                            /*
                            final String[] columns = new String[]{"OBJECT"};
                            Cursor c = sdbr.query("talk",columns,null,null,null,null,null);
                            while (c.moveToNext()) {
                                String moji5 = c.getString(c.getColumnIndex("OBJECT"));
                                ((TextView) findViewById(R.id.recog_object_text)).setText("Lvcsr:"+ moji5);
                            }
                            */
                            //((TextView) findViewById(R.id.recog_text)).setText("Lvcsr:"+lvcsr);
                            ((TextView) findViewById(R.id.recog_event_text)).setText("おおおおお:");
                        }
                    }
                });
                break;
            case ScenarioDefinitions.FUNC_RECOG_EVENT:
                //final String lvcsr = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_BASIC);
                //final String event = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_EVENT);
                //final String eventname = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_BASIC);
                //final String time = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_TIME);
                //final String place = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_PLACE);
                //final String people = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_PEOPLE);
                //final String object = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_OBJECT);
                //final String event = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_EVENT);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            //final ContentValues values = new ContentValues();
                            values.put("EVENTNAME",eventname);
                            values.put("TIME",time);
                            values.put("PLACE",place);
                            values.put("PEOPLE",people);
                            values.put("OBJECT",object);
                            values.put("EVENT",event);
                            sdb.insert("talk", null, values);
                            //final DataManagement management = new DataManagement();
                            //management.setEvent(event);
                            /*
                            final String[] columns = new String[]{"EVENT"};
                            Cursor c = sdbr.query("talk",columns,null,null,null,null,null);
                            while (c.moveToNext()) {
                                String moji6 = c.getString(c.getColumnIndex("EVENT"));
                                ((TextView) findViewById(R.id.recog_event_text)).setText("Lvcsr:"+ moji6);
                            }
                            */
                            //((TextView) findViewById(R.id.recog_text)).setText("Lvcsr:"+lvcsr);
                        }
                    }
                });
                break;
            case ScenarioDefinitions.FUNC_VIEW_RESULT:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            /*
                            final DataManagement management = new DataManagement();
                            //management.insertDB(sdb);
                            final ContentValues values = new ContentValues();
                            values.put("EVENTNAME",management.getEventname());
                            values.put("TIME",management.getTime());
                            values.put("PLACE",management.getPlace());
                            values.put("PEOPLE",management.getPeople());
                            values.put("OBJECT",management.getObject());
                            values.put("EVENT",management.getEvent());
                            sdb.insert("talk", null, values);
                            */
                            //management.pullDB(sdbr);
                            //String learndata = management.pullDB(sdbr);
                            //((TextView)findViewById(R.id.recog_eventname_text)).setText("Lvcsr:" + learndata);
                            final String[] columns = new String[]{"TIME","PLACE","PEOPLE","OBJECT","EVENT"};
                            String where = "EVENTNAME like ?";
                            String param = "%江戸時代%";
                            Cursor c = sdbr.query("talk",columns,null,null,null,null,null);
                            c.moveToFirst();
                            StringBuilder stringBuilder = new StringBuilder();
                            while (c.moveToNext()) {
                                stringBuilder.append(c.getString(c.getColumnIndex("TIME")));
                                stringBuilder.append("に");
                                stringBuilder.append(c.getString(c.getColumnIndex("PLACE")));
                                stringBuilder.append("で");
                                stringBuilder.append(c.getString(c.getColumnIndex("PEOPLE")));
                                stringBuilder.append("が");
                                stringBuilder.append(c.getString(c.getColumnIndex("OBJECT")));
                                stringBuilder.append("を");
                                stringBuilder.append(c.getString(c.getColumnIndex("EVENT")));
                                //String moji1 = c.getString(c.getColumnIndex("TIME"));
                                //String moji2 = c.getString(c.getColumnIndex("PLACE"));
                                //String moji3 = c.getString(c.getColumnIndex("PEOPLE"));
                                //String moji4 = c.getString(c.getColumnIndex("OBJECT"));
                                //String moji5 = c.getString(c.getColumnIndex("EVENT"));
                                //((TextView) findViewById(R.id.recog_eventname_text)).setText("Lvcsr:"+ moji1 + "に" + moji2 + "で" + moji3 + "が" + moji4 + "を" + moji5 + "。");
                            }
                            c.close();
                            ((TextView) findViewById(R.id.recog_eventname_text)).setText("Lvcsr:"+ stringBuilder.toString() + "。");
                            //((TextView) findViewById(R.id.recog_eventname_text)).setText("Lvcsr:1603年に江戸で徳川家康が江戸幕府を開いた。");
                            //((TextView) findViewById(R.id.recog_eventname_text)).setText("Lvcsr:江戸で徳川家康が江戸幕府を開いたのは何年でしょうか。");
                        }
                    }
                });
                break;
            default:
                break;
        }

    }

    /**
     * ホームボタンの押下イベントを受け取るためのBroadcastレシーバークラス.<br>
     * <p/>
     * アプリは必ずホームボタンで終了する..
     */
    private class HomeEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "Receive Home button pressed");
            // ホームボタン押下でアプリ終了する.
            finish();
        }
    }

    /**
     * 音声UI再起動イベントを受け取るためのBroadcastレシーバークラス.<br>
     * <p/>
     * 稀に音声UIのServiceが再起動することがあり、その場合アプリはVoiceUIの再取得とListenerの再登録をする.
     */
    private class VoiceUIStartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (VoiceUIManager.ACTION_VOICEUI_SERVICE_STARTED.equals(action)) {
                Log.d(TAG, "VoiceUIStartReceiver#onReceive():VOICEUI_SERVICE_STARTED");
                //VoiceUIManagerのインスタンス取得.
                mVoiceUIManager = VoiceUIManager.getService(getApplicationContext());
                if (mMainActivityVoiceUIListener == null) {
                    mMainActivityVoiceUIListener = new MainActivityVoiceUIListener(getApplicationContext());
                }
                //VoiceUIListenerの登録.
                VoiceUIManagerUtil.registerVoiceUIListener(mVoiceUIManager, mMainActivityVoiceUIListener);
            }
        }
    }
}
