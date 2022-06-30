package com.gangjust.kingosoftapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gangjust.kingosoftapi.api.KingosoftEncoding;
import com.gangjust.kingosoftapi.api.KingosoftInfo;
import com.gangjust.kingosoftapi.error.KingosoftLoginFailureException;
import com.gangjust.kingosoftapi.utils.NetUtil;
import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

enum KingosoftApiOs {
    //ios,
    android,
}

public class KingosoftApi {
    private static final String PHONE_MI_8 = "MI 8";
    private static final String PHONE_MI_8_OS_VERSION = "10";

    private static JSONObject _loginObject;

    private KingosoftApi() {
    }

    /**
     * 支持的学校列表
     *
     * @return
     */
    public static String getSchoolList() throws IOException {
        String param = "appver=" + KingosoftInfo.appVersion + "&action=getAgent&xxmc=";
        return common(param, "00000");
    }

    /**
     * 登录
     *
     * @param schoolCode 学校代码或学校名
     * @param userName   账号
     * @param userPass   密码
     * @param reLogin    如果为 true 每次调用 login 方法都重新登录，不读取运行缓存。
     * @return
     */
    public static KingosoftApi login(String schoolCode, String userName, String userPass, boolean reLogin)
            throws KingosoftLoginFailureException, IOException {
        return login(schoolCode, userName, userPass, reLogin, PHONE_MI_8, KingosoftApiOs.android, PHONE_MI_8_OS_VERSION);
    }

    /**
     * @param schoolCode 学校代码
     * @param userName   账号
     * @param userPass   密码
     * @param phoneName  手机名称
     * @param os         手机系统
     * @param osVersion  系统版本
     * @return
     */
    public static KingosoftApi login(String schoolCode, String userName, String userPass, boolean reLogin, @NotNull String phoneName, @NotNull KingosoftApiOs os, @NotNull String osVersion)
            throws KingosoftLoginFailureException, IOException {

        if (phoneName.equals("")) {
            throw new KingosoftLoginFailureException("msg: 手机名称不能为空，你可以使用默认的 KingosoftApi.PHONE_MI_8 静态变量，也可以自定义手机名,如：HUAWEI P40，则表示 华为P40手机！");
        }

        if (osVersion.equals("")) {
            throw new KingosoftLoginFailureException("msg: 手机系统版本不能为空，你可以使用默认的 KingosoftApi.PHONE_MI_8_OS_VERSION 静态变量，也可以自定义手机的系统版本，如：10，则表示 Android 10！");
        }


        Map<String, String> map = new HashMap<>();
        map.put("loginId", userName);
        map.put("xxdm", schoolCode);
        map.put("pwd", userPass);
        map.put("action", "getLoginInfoNew");
        map.put("isky", "1");
        map.put("sjbz", "");
        map.put("sswl", "55555");
        map.put("sjxh", phoneName);
        map.put("os", "android");
        map.put("xtbb", osVersion);
        map.put("appver", KingosoftInfo.appVersion.substring(7));
        map.put("loginmode", "0");

        String loginJson = "";
        // 如果存在缓存
        if (_loginObject != null) {
            // 判断是否重新登录
            if (reLogin) loginJson = common(map, "00000");
        } else {
            loginJson = common(map, "00000");
        }

        // 如果登录失败
        if (loginJson == null || loginJson.equals("")) {
            throw new KingosoftLoginFailureException("msg: 未能成功登陆，可能是api策略已经更新！");
        }

        _loginObject = JSON.parseObject(loginJson);

        return new KingosoftApi();
    }

    /**
     * 判断是否登录成功
     *
     * @return
     */
    public boolean isLogin() {
        if (_loginObject.getString("flag").equals("0")) {
            return true;
        }
        return false;
    }

    /**
     * 返回登录的提示
     * 可以此判断登录情况，例如：验证通过！密码错误！等。
     *
     * @return
     */
    public String getLoginMsg() {
        return _loginObject.getString("msg");
    }

    /**
     * 返回个人课表 JSON
     *
     * @param jsdm 未知，根据词义应该是 教室代码
     * @param bjdm 未知，根据词义应该是 班级代码
     * @param week 欲获取第n周的课表
     * @param xnxq 学年学期,由 [年份+学期] 构成
     *             例1：20200 第一学期
     *             例2：20201 第二学期
     * @return
     * @throws IOException
     */
    public String getCourse(String jsdm, String bjdm, String week, String xnxq) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("userId", _loginObject.getString("userid"));
        map.put("usertype", _loginObject.getString("usertype"));
        map.put("action", "getKb");
        map.put("step", "kbdetail_bz");
        map.put("bjdm", bjdm);
        map.put("jsdm", jsdm);
        map.put("xnxq", xnxq);
        map.put("week", week.equals("") ? "1" : week);
        map.put("channel", "jrkb");

        return common(map, _loginObject.getString("token"));
    }

    /**
     * 取本学年的学期代码
     *
     * @return
     * @throws IOException
     */
    public String getSchoolYear() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("userId", _loginObject.getString("userid"));
        map.put("usertype", _loginObject.getString("usertype"));
        map.put("action", "getKb");
        map.put("step", "xnxq");
        return common(map, _loginObject.getString("token"));
    }

    /**
     * 返回个人成绩 JSON
     *
     * @param flag flag可取[0,1]两个值，分别代表[原始成绩,有效成绩]
     * @param xnxq 学年学期,由 [年份+学期] 构成
     *             例1：20200 第一学期
     *             例2：20201 第二学期
     * @return
     */
    public String getScore(String flag, String xnxq) throws IOException {
        Map<String, String> map = new HashMap<>();

        map.put("action", "getStucj");
        map.put("step", "detail");
        map.put("xnxq", xnxq);
        map.put("flag", flag);
        map.put("userId", _loginObject.getString("userid"));
        map.put("usertype", _loginObject.getString("usertype"));

        return common(map, _loginObject.getString("token"));
    }

    /**
     * 返回学情警示 JSON
     *
     * @return
     */
    public String getWarningSituation() throws IOException {
        return getWebCommParams("xqjs", "list");
    }

    /**
     * 返回培养方案 JSON
     *
     * @return
     */
    public String getTrainingProgram() throws IOException {
        return getWebCommParams("pyfa", "list");
    }

    /**
     * 返回等级考试 JSON
     *
     * @return
     */
    public String getNationalLevelExamination() throws IOException {
        return getWebCommParams("djkscj", "detail");
    }

    /**
     * 返回成绩分布 JSON
     *
     * @return
     */
    public String getLearningDetails() throws IOException {
        return getWebCommParams("cjfb", "list");
    }

    /**
     * 返回重修查询 JSON
     *
     * @return
     */
    public String getRelearning() throws IOException {
        return getWebCommParams("cxcx", "detail");
    }

    /**
     * 返回作息时间 JSON
     *
     * @return
     */
    public String getTimetable() throws IOException {
        return getWebCommParams("zxsj", "detail");
    }

    /**
     * 内部 WebView 请求
     *
     * @param type 请求方式，抓包后会有对应代码
     * @param step 未知，抓包后会有对应代码
     * @return
     */
    private String getWebCommParams(String type, String step) throws IOException {
        StringBuffer buffer = new StringBuffer();
        buffer.append("user=").append(_loginObject.getString("userid")).append("&");
        buffer.append("usertype=").append(_loginObject.getString("usertype")).append("&");
        buffer.append("uuid=").append(_loginObject.getString("uuid"));

        //第一次加密
        String encode1 = KingosoftEncoding.webEncoding(buffer.toString(), _loginObject.getString("xxdm"));

        //在第一次加密的基础上添加学校代码
        encode1 = encode1 + "&xxdm=" + _loginObject.getString("xxdm");

        //第二次加密
        String encode2 = KingosoftEncoding.webEncoding(encode1, KingosoftInfo.key);

        //参数组合
        StringBuffer buffer1 = new StringBuffer();
        try {
            buffer1.append(URLEncoder.encode("head[us]", "utf-8")).append("=&");
            buffer1.append(URLEncoder.encode("head[version]", "utf-8")).append("=1.0.0&");
            buffer1.append(URLEncoder.encode("head[ct]", "utf-8")).append("=3&");

            //注意
            buffer1.append(URLEncoder.encode("head[time]", "utf-8")).append("=").append(String.valueOf(new Date().getTime()).substring(0, 10)).append("&");

            buffer1.append(URLEncoder.encode("head[sign]", "utf-8")).append("=&");


            buffer1.append("sign=").append(URLEncoder.encode(encode2, "utf-8"));
            //注意这里的 & 也要编码
            buffer1.append(URLEncoder.encode("&", "utf-8"));
            buffer1.append(URLEncoder.encode("token=", "utf-8")).append(_loginObject.getString("token"));
            //注意这里的 & 也要编码
            buffer1.append(URLEncoder.encode("&", "utf-8"));
            buffer1.append(URLEncoder.encode("appinfo=", "utf-8")).append(KingosoftInfo.appVersion).append("&");

            buffer1.append("action=").append("jw_apply&");
            buffer1.append("type=").append(type).append("&");
            buffer1.append("step=").append(step);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return webCommon(buffer1.toString());
    }

    /**
     * 封装的通用请求方法
     *
     * @param params 参数
     * @param token  请求token,默认是 00000 会在登录后返回
     * @return 返回一个String格式的html文本
     * @throws IOException
     */
    private static String common(String params, String token) throws IOException {
        String userId = "";
        String uuid = "";

        if (!token.equals("00000")) {
            userId = _loginObject.getString("userid");
            uuid = _loginObject.getString("uuid");
        }

        Map<String, String> map = KingosoftEncoding.encoding(params, userId, uuid, token);

        return NetUtil.post(KingosoftInfo.URL, map).getHTMLText();
    }

    /**
     * 封装的通用请求方法
     *
     * @param params 参数
     * @param token  请求token,默认是 00000 会在登录后返回
     * @return 返回一个String格式的html文本
     * @throws IOException
     */
    private static String common(Map<String, String> params, String token) throws IOException {
        String userId = "";
        String uuid = "";

        if (!token.equals("00000")) {
            userId = _loginObject.getString("userid");
            uuid = _loginObject.getString("uuid");
        }

        Map<String, String> map = KingosoftEncoding.encoding(params, userId, uuid, token);
        return NetUtil.post(KingosoftInfo.URL, map).getHTMLText();
    }

    /**
     * 内嵌webview通用请求方法
     *
     * @param params 参数
     * @return
     * @throws IOException
     */
    private static String webCommon(String params) throws IOException {
        return NetUtil.get(KingosoftInfo.URL_WEBVIEW, params).getHTMLText();
    }
}
