package jp.co.sharp.sample.simple;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

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

    /**
     * 暗記内容発話用文字列.
     */
    private String mSpeachText = "";
    /**
     *解答用文字列
     */
    private String mAnswerText = "";

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

        //発話を実行
        /*if (!mSpeachText.equals("")){
            VoiceUIVariableUtil.VoiceUIVariableListHelper helper = new VoiceUIVariableUtil.VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_MEMORIZE);
            VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
        }*/
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
        switch (command) {
            case ScenarioDefinitions.FUNC_END_APP:
                finish();
                break;
            case ScenarioDefinitions.FUNC_RECOG_TALK:
                final String eventname = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_BASIC);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            SQLiteDatabase sdb = dbhelper.getWritableDatabase();
                            ContentValues values = new ContentValues();

                            values.put("EVENTNAME",eventname);
                            sdb.insert("talk", null, values);
                        }
                    }
                });
                break;
            case ScenarioDefinitions.FUNC_RECOG_TIME:
                final String time = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_TIME);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            SQLiteDatabase sdbr = dbhelper.getReadableDatabase();
                            long recodeCount = DatabaseUtils.queryNumEntries(sdbr, "talk");
                            Log.d(TAG, "recodeCount: " + recodeCount);
                            String id = String.valueOf(recodeCount);

                            SQLiteDatabase sdb = dbhelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("TIME",time);
                            sdb.update("talk", values, "_id = ?", new String[]{id});
                        }
                    }
                });
                break;
            case ScenarioDefinitions.FUNC_RECOG_PLACE:
                final String place = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_PLACE);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            SQLiteDatabase sdbr = dbhelper.getReadableDatabase();
                            long recodeCount = DatabaseUtils.queryNumEntries(sdbr, "talk");
                            Log.d(TAG, "recodeCount: " + recodeCount);
                            String id = String.valueOf(recodeCount);

                            SQLiteDatabase sdb = dbhelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("PLACE",place);
                            sdb.update("talk", values, "_id = ?", new String[]{id});
                        }
                    }
                });
                break;
            case ScenarioDefinitions.FUNC_RECOG_PEOPLE:
                final String people = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_PEOPLE);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            SQLiteDatabase sdbr = dbhelper.getReadableDatabase();
                            long recodeCount = DatabaseUtils.queryNumEntries(sdbr, "talk");
                            Log.d(TAG, "recodeCount: " + recodeCount);
                            String id = String.valueOf(recodeCount);

                            SQLiteDatabase sdb = dbhelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("PEOPLE",people);
                            sdb.update("talk", values, "_id = ?", new String[]{id});
                        }
                    }
                });
                break;
            case ScenarioDefinitions.FUNC_RECOG_OBJECT:
                final String object = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_OBJECT);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            SQLiteDatabase sdbr = dbhelper.getReadableDatabase();
                            long recodeCount = DatabaseUtils.queryNumEntries(sdbr, "talk");
                            Log.d(TAG, "recodeCount: " + recodeCount);
                            String id = String.valueOf(recodeCount);

                            SQLiteDatabase sdb = dbhelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("OBJECT",object);
                            sdb.update("talk", values, "_id = ?", new String[]{id});
                        }
                    }
                });
                break;
            case ScenarioDefinitions.FUNC_RECOG_EVENT:
                final String event = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LVCSR_EVENT);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            SQLiteDatabase sdbr = dbhelper.getReadableDatabase();
                            long recodeCount = DatabaseUtils.queryNumEntries(sdbr, "talk");
                            Log.d(TAG, "recodeCount: " + recodeCount);
                            String id = String.valueOf(recodeCount);

                            SQLiteDatabase sdb = dbhelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("EVENT",event);
                            sdb.update("talk", values, "_id = ?", new String[]{id});
                        }
                    }
                });
                break;
            case ScenarioDefinitions.FUNC_VIEW_RESULT:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFinishing()) {
                            SQLiteDatabase sdbr = dbhelper.getReadableDatabase();
                            Cursor c = sdbr.query("talk",new String[]{"TIME","PLACE","PEOPLE","OBJECT","EVENT"},null,null,null,null,null);
                            c.moveToFirst();
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < c.getCount(); i++){
                                stringBuilder.append(c.getString(c.getColumnIndex("TIME")));
                                stringBuilder.append("に");
                                stringBuilder.append(c.getString(c.getColumnIndex("PLACE")));
                                stringBuilder.append("で");
                                stringBuilder.append(c.getString(c.getColumnIndex("PEOPLE")));
                                stringBuilder.append("が");
                                stringBuilder.append(c.getString(c.getColumnIndex("OBJECT")));
                                stringBuilder.append("を");
                                stringBuilder.append(c.getString(c.getColumnIndex("EVENT")));
                                stringBuilder.append("。\n");
                                c.moveToNext();
                            }
                            c.close();
                            ((TextView) findViewById(R.id.recog_eventname_text)).setText("Lvcsr:"+ stringBuilder.toString());
                            //((TextView) findViewById(R.id.recog_time_text)).setText("問題：江戸で徳川家康が江戸幕府を開いたのは、いつですか？");
                            //((TextView) findViewById(R.id.recog_time_text)).setText("問題：1603年に江戸で江戸幕府を開いたのは、だれですか？");
                        }
                    }
                });
                break;
            case ScenarioDefinitions.FUNC_VIEW_MEMORIZE:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()){
                            SQLiteDatabase sdbr = dbhelper.getReadableDatabase();
                            long recodeCount = DatabaseUtils.queryNumEntries(sdbr, "talk");
                            Log.d(TAG, "recodeCount: " + recodeCount);
                            Cursor cursor = sdbr.query("talk", new String[]{"TIME","PLACE","PEOPLE","OBJECT","EVENT"}, "_id = ?", new String[]{"" + recodeCount}, null, null, null);
                            cursor.moveToFirst();
                            StringBuilder sBuilder = new StringBuilder();
                            for (int i = 0; i < cursor.getCount(); i++){
                                sBuilder.append(cursor.getString(cursor.getColumnIndex("TIME")));
                                sBuilder.append("に");
                                sBuilder.append(cursor.getString(cursor.getColumnIndex("PLACE")));
                                sBuilder.append("で");
                                sBuilder.append(cursor.getString(cursor.getColumnIndex("PEOPLE")));
                                sBuilder.append("が");
                                sBuilder.append(cursor.getString(cursor.getColumnIndex("OBJECT")));
                                sBuilder.append("を");
                                sBuilder.append(cursor.getString(cursor.getColumnIndex("EVENT")));
                                cursor.moveToNext();
                            }
                            cursor.close();
                            mSpeachText = sBuilder.toString();
                            Log.d(TAG, mSpeachText);

                            VoiceUIVariableUtil.setVariableData(mVoiceUIManager, ScenarioDefinitions.MEM_P_MEMORIZE, mSpeachText);
                            //if (mVoiceUIManager != null && !mSpeachText.equals("")){
                            if (mVoiceUIManager != null){
                                //VoiceUIVariableUtil.VoiceUIVariableListHelper helper = new VoiceUIVariableUtil.VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_MEMORIZE);
                                //VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
                                VoiceUIVariableListHelper helper = new VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_MEMORIZE);
                                VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
                            }

                            Log.d(TAG, "発話実行：" + mSpeachText);
                            //発話後はリセット
                            mSpeachText = "";
                            /*for (VoiceUIVariable variable : variables){
                                String key = variable.getName();
                                Log.d(TAG, "onVoiceUIViewMemorize: " + key + ":" + variable.getStringValue());
                                variable.setStringValue(mSpeachText);
                                if (!mSpeachText.equals("")){
                                    VoiceUIVariableUtil.VoiceUIVariableListHelper helper = new VoiceUIVariableUtil.VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_MEMORIZE);
                                    VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
                                }
                                Log.d(TAG, "if抜け");
                                //発話後はリセット
                                mSpeachText = "";
                            }*/
                        }
                    }
                });

                break;
            case ScenarioDefinitions.FUNC_QUESTION_TIME:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()){
                            SQLiteDatabase sdbr = dbhelper.getReadableDatabase();
                            long recodeCount = DatabaseUtils.queryNumEntries(sdbr, "talk");
                            Log.d(TAG, "recodeCount: " + recodeCount);
                            Random random = new Random();
                            int randValue = random.nextInt((int)recodeCount);
                            Cursor cursor = sdbr.query("talk", new String[]{"PLACE","PEOPLE","OBJECT","EVENT"}, "_id = ?", new String[]{"" + randValue}, null, null, null);
                            cursor.moveToFirst();
                            StringBuilder sBuilder = new StringBuilder();
                            for (int i = 0; i < cursor.getCount(); i++){
                                sBuilder.append(cursor.getString(cursor.getColumnIndex("PLACE")));
                                sBuilder.append("で、");
                                sBuilder.append(cursor.getString(cursor.getColumnIndex("PEOPLE")));
                                sBuilder.append("が");
                                sBuilder.append(cursor.getString(cursor.getColumnIndex("OBJECT")));
                                sBuilder.append("を");
                                sBuilder.append(cursor.getString(cursor.getColumnIndex("EVENT")));
                                sBuilder.append("のは、いつですか？");
                                cursor.moveToNext();
                            }
                            cursor.close();
                            mSpeachText = sBuilder.toString();
                            Log.d(TAG, mSpeachText);
                            Cursor cAnswer = sdbr.query("talk", new String[]{"TIME"}, "_id = ?", new String[]{"" + randValue}, null, null, null);
                            cAnswer.moveToFirst();
                            StringBuilder sAnswer = new StringBuilder();
                            for (int i = 0; i < cAnswer.getCount(); i++){
                                sAnswer.append(cAnswer.getString(cAnswer.getColumnIndex("TIME")));
                                cAnswer.moveToNext();
                            }
                            cAnswer.close();
                            mAnswerText = sAnswer.toString();
                            Log.d(TAG, mAnswerText);
                            VoiceUIVariableUtil.setVariableData(mVoiceUIManager, ScenarioDefinitions.DATA_ANSWER_TIME, mAnswerText);

                            VoiceUIVariableUtil.setVariableData(mVoiceUIManager, ScenarioDefinitions.DATA_QUESTION_TIME, mSpeachText);
                            if (mVoiceUIManager != null && !mSpeachText.equals("")){
                                VoiceUIVariableUtil.VoiceUIVariableListHelper helper = new VoiceUIVariableUtil.VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_QUESTION);
                                VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
                            }
                            //発話後はリセット
                            mSpeachText = "";
                            mAnswerText = "";
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    public String getmSpeachText(){
        return mSpeachText;
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
