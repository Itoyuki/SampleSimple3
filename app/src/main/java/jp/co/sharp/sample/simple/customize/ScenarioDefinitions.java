package jp.co.sharp.sample.simple.customize;

/**
 * シナリオファイルで使用する定数の定義クラス.<br>
 * <p/>
 * <p>
 * controlタグのtargetにはPackage名を設定すること<br>
 * scene、memory_p(長期記憶の変数名)、resolve variable(アプリ変数解決の変数名)、accostのwordはPackage名を含むこと<br>
 * </p>
 */
public class ScenarioDefinitions {

    /**
     * sceneタグを指定する文字列
     */
    public static final String TAG_SCENE = "scene";
    /**
     * accostタグを指定する文字列
     */
    public static final String TAG_ACCOST = "accost";
    /**
     * target属性を指定する文字列
     */
    public static final String ATTR_TARGET = "target";
    /**
     * function属性を指定する文字列
     */
    public static final String ATTR_FUNCTION = "function";
    /**
     * memory_pを指定するタグ
     */
    public static final String TAG_MEMORY_PERMANENT = "memory_p:";
    /**
     * Package名.
     */
    protected static final String PACKAGE = "jp.co.sharp.sample.simple";
    /**
     * シナリオ共通: controlタグで指定するターゲット名.
     */
    public static final String TARGET = PACKAGE;
    /**
     * scene名: アプリ共通シーン
     */
    public static final String SCENE_COMMON = PACKAGE + ".scene_common";
    /**
     * function：アプリ終了を通知する.
     */
    public static final String FUNC_END_APP = "end_app";
    /**
     * function：発話内容を通知する.
     */
    public static final String FUNC_RECOG_TALK = "recog_talk";
    /**
     * function：”いつ”を通知する
     */
    public static final String FUNC_RECOG_TIME = "recog_time";
    /**
     * function：”どこで”を通知する
     */
    public static final String FUNC_RECOG_PLACE = "recog_place";
    /**
     * function：”誰が”を通知する
     */
    public static final String FUNC_RECOG_PEOPLE = "recog_people";
    /**
     * function：”何を”を通知する
     */
    public static final String FUNC_RECOG_OBJECT = "recog_object";
    /**
     * function：”どうした”を通知する
     */
    public static final String FUNC_RECOG_EVENT = "recog_event";
    /**
     * function：結果を表示
     */
    public static final String FUNC_VIEW_RESULT = "view_result";
    /**
     * function：音声UIコールバック用定義
     */
    public static final String FUNC_VIEW_MEMORIZE = "view_memorize";
    /**
     * function：”いつ”について出題する
     */
    public static final String FUNC_QUESTION_TIME = "question_time";
    /**
     * function：”どこで”について出題する
     */
    public static final String FUNC_QUESTION_PLACE = "question_place";
    /**
     * function：”だれが”について出題する
     */
    public static final String FUNC_QUESTION_PEOPLE = "question_people";
    /**
     * function：”なにを”について出題する
     */
    public static final String FUNC_QUESTION_OBJECT = "question_object";
    /**
     * function：”どうした”について出題する
     */
    public static final String FUNC_QUESTION_EVENT = "question_event";
    /**
     * accost名：accostテスト発話実行.
     */
    public static final String ACC_ACCOST =  ScenarioDefinitions.PACKAGE + ".accost.t1";
    /**
     * 暗記項目発話実行.
     */
    public static final String ACC_MEMORIZE =  ScenarioDefinitions.PACKAGE + ".accost.t2";
    /**
     * 問題発話実行
     */
    public static final String ACC_QUESTION = ScenarioDefinitions.PACKAGE + ".accost.question";
    /**
     * accost名：resolveテスト発話実行.
     */
    public static final String ACC_RESOLVE =  ScenarioDefinitions.PACKAGE + ".variable.t1";
    /**
     * accost名：get_memorypの発話実行.
     */
    public static final String ACC_GET_MEMORYP =  ScenarioDefinitions.PACKAGE + ".get_memoryp.t1";
    /**
     * accost名：アプリ終了発話実行.
     */
    public static final String ACC_END_APP = ScenarioDefinitions.PACKAGE + ".app_end.t2";
    /**
     * resolve variable：アプリで変数解決する値.
     */
    public static final String RESOLVE_JAVA_VALUE = ScenarioDefinitions.PACKAGE + ":java_side_value";
    /**
     * data key：シナリオ起動時情報1.
     */
    public static final String KEY_TEST_1 = "key_test1";
    /**
     * data key：シナリオ起動時情報2.
     */
    public static final String KEY_TEST_2 = "key_test2";
    /**
     * data key：大語彙認識文言.
     */
    public static final String KEY_LVCSR_BASIC = "Lvcsr_Basic";
    /**
     * data key：いつ
     */
    public static final String KEY_LVCSR_TIME = "Lvcsr_Time";
    /**
     * data key：どこで
     */
    public static final String KEY_LVCSR_PLACE = "Lvcsr_Place";
    /**
     * data key：だれが
     */
    public static final String KEY_LVCSR_PEOPLE = "Lvcsr_People";
    /**
     * data key：何を
     */
    public static final String KEY_LVCSR_OBJECT = "Lvcsr_Object";
    /**
     * data key：どうした
     */
    public static final String KEY_LVCSR_EVENT = "Lvcsr_Event";
    /**
     * 出題モード
     */
    public static final String MEM_P_MODE = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".mode";
    /**
     * 暗記項目データ
     */
    public static final String MEM_P_MEMORIZE = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".memorize";
    /**
     * 問題文「いつ」
     */
    public static final String MEM_P_QUESTION = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".question";
    /**
     * 解答「いつ」
     */
    public static final String MEM_P_ANSWER = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".answer";
    /**
     * 問題文「どこで」
     */
    public static final String MEM_P_QUESTION_PLACE = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".qPlace";
    /**
     * 解答「どこで」
     */
    public static final String MEM_P_ANSWER_PLACE = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".aPlace";
    /**
     * 問題文「だれが」
     */
    public static final String MEM_P_QUESTION_PEOPLE = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".qPeople";
    /**
     * 解答「だれが」
     */
    public static final String MEM_P_ANSWER_PEOPLE = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".aPeople";
    /**
     * 問題文「なにを」
     */
    public static final String MEM_P_QUESTION_OBJECT = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".qObject";
    /**
     * 解答「なにを」
     */
    public static final String MEM_P_ANSWER_OBJECT = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".aObject";
    /**
     * 問題文「どうした」
     */
    public static final String MEM_P_QUESTION_EVENT = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".qEvent";
    /**
     * 解答「どうした」
     */
    public static final String MEM_P_ANSWER_EVENT = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".aEvent";
    /**
     * memory_p：時.
     */
    public static final String MEM_P_HOUR = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".hour";
    /**
     * memory_p：分.
     * */
    public static final String MEM_P_MINUTE = ScenarioDefinitions.TAG_MEMORY_PERMANENT + ScenarioDefinitions.PACKAGE + ".minute";
    /**
     * static クラスとして使用する.
     */
    private ScenarioDefinitions() {
    }

}
