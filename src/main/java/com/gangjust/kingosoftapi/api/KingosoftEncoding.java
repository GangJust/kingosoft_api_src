package com.gangjust.kingosoftapi.api;

import java.util.HashMap;
import java.util.Map;

public class KingosoftEncoding {

    private static Map<Integer, Character> map;

    /**
     * 加密参数的方法
     *
     * @param map   一个Map格式的参数列表
     * @param token 默认值是00000
     * @return 返回加密的值，是一个 [Map]
     */
    public static Map<String, String> encoding(Map<String, String> map, String userid, String uuid, String token) {

        String params = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey().trim();
            String value = entry.getValue() == null ? "" : entry.getValue();
            params = params + "&" + key + "=" + value;
        }

        return encoding(params, userid, uuid, token);
    }

    public static Map<String, String> encoding(String params, String userid, String uuid, String token) {
        token = token == null ? "00000" : ("".equals(token) ? "00000" : token);

        if (!token.equals("00000")) params += "&sfid=" + userid + "&uuid=" + uuid;

        if (params.indexOf("&") == 0) params = params.substring(1);

        Map<String, String> hashMap = new HashMap();

        try {
            hashMap.put("param", getParam(params, KingosoftInfo.key));
            hashMap.put("param2", getParam2(params));
            hashMap.put("token", token);
            hashMap.put("appinfo", KingosoftInfo.appVersion);
        } catch (Exception e) {
            hashMap.put("param", "error");
            hashMap.put("param2", "error");
            hashMap.put("token", token);
            hashMap.put("appinfo", KingosoftInfo.appVersion);
        }
        return hashMap;
    }

    /**
     * 该方法用作于喜鹊儿内嵌webview访问的加密
     *
     * @param str  参数一
     * @param str2 参数二
     * @return
     */
    public static String webEncoding(String str, String str2) {
        try {
            return "param=" + getParam(str, str2) + "&param2=" + getParam2(str);
        } catch (Exception e) {
            return "param=error&param2=error";
        }
    }

    /**
     * param参数加密
     *
     * @param string 未加密的param参数
     * @param s      一个key值
     * @return
     */
    public static String getParam(String string, String s) {
        if (string == null || "".equals(string) || s == null || "".equals(s)) {
            return string;
        }
        String s2 = "";
        int length = s.length();
        int length2 = string.length();
        int n = (int) Math.ceil(length2 * 1.0 / length);
        int n2 = (int) Math.ceil(length2 * 3.0 * 6.0 / 9.0 / 6.0);
        String string2 = "";
        String s3;
        for (int i = 0; i < n; ++i) {
            int n3 = 1;
            while (true) {
                s3 = string2;
                if (n3 > length) break;
                int n4 = i * length + n3;
                String string3 = "000" + (Integer.parseInt(toArray(string.substring(n4 - 1, n4))) + Integer.parseInt(toArray(s.substring(n3 - 1, n3))) + n2 * 6 % length);
                string2 += string3.substring(string3.length() - 3);
                if (n4 == length2) {
                    s3 = string2;
                    break;
                }
                n3++;
            }
            string2 = s3;
        }
        int n5 = 0;
        string = s2;
        while (true) {
            s = string;
            if (n5 >= string2.length()) break;
            int length3;
            if ((length3 = n5 + 9) >= string2.length()) {
                length3 = string2.length();
            }
            s = string2.substring(n5, length3);
            n5 += 9;
            s = "000000" + a(Long.parseLong(s));
            s = s.substring(s.length() - 6);
            string += s;
        }
        return s;
    }

    /**
     * param2参数加密
     *
     * @param str 未加密的param参数
     * @return 加密后的param
     */
    public static String getParam2(String str) {
        String[] split = MD5Util.md5(str).split("");

        //可能是java的版本原因，也可能是系统的原因 linux 与 window 符号长度的原因，在手机端aide上 split[0] 应该是一个空白字符
        if (!("".equals(split[0].trim()))) {
            String[] tmp = new String[split.length + 1];
            tmp[0] = "";
            for (int i = 0; i < split.length; i++) {
                tmp[i + 1] = split[i];
            }
            split = tmp;
        }

        String str2 = "";
        for (int i = 0; i < split.length; i++) {
            if (!(i == 3 || i == 10 || i == 17 || i == 25)) {
                str2 = str2 + split[i];
            }
        }

        return MD5Util.md5(str2);
    }

    private static void putMap() {
        int i = 0;
        if (map == null) {
            map = new HashMap<>();
        }
        for (int i2 = 0; i2 < 10; i2++) {
            map.put(Integer.valueOf(i2), Character.valueOf((char) (i2 + 48)));
        }
        while (i < 26) {
            map.put(Integer.valueOf(i + 10), Character.valueOf((char) (i + 97)));
            i++;
        }
    }

    /**
     * 该方法默认为公开的，如果有使用，请将其公开
     *
     * @param s
     * @return
     */
    private static String toArray(String s) {
        final StringBuffer sb = new StringBuffer();
        final char[] charArray = s.toCharArray();
        for (int i = 0; i < charArray.length; ++i) {
            if (i != charArray.length - 1) {
                sb.append(Integer.valueOf(charArray[i])).append(",");
            } else {
                sb.append(Integer.valueOf(charArray[i]));
            }
        }
        return sb.toString();
    }

    private static String a(long j) {
        putMap();
        String str = "";
        if (j < 0) {
            return "-" + a(Math.abs(j));
        }
        do {
            String str2 = str;
            str = (map.get(((int) (j % 36)))).toString();
            if (!("".equals(str2))) {
                str = str + str2;
            }
            j /= 36;
        } while (j > 0);
        return str;
    }

}
