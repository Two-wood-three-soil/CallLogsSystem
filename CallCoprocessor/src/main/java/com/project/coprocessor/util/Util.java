package com.project.coprocessor.util;

import java.text.DecimalFormat;

public class Util {
    public static String getHashCode(String callee,String callDateFormat,int partitionsNum){
        // 创建格式化类
        DecimalFormat df = new DecimalFormat();
        int len = callee.length();
        String callee5 = callee.substring(len-5);
        String callDatemonth = callDateFormat.substring(0,6);
        //取hash值
        int code = (Integer.parseInt(callee5) ^ Integer.parseInt(callDatemonth)) % partitionsNum;
        // 格式化后返回
        df.applyPattern("00");
        return df.format(code);
    }
    // 拼接RowKey
    public static String getRowKey(String hashCode,String caller,String callDate,String flag,String callee,String callDuration){
        return hashCode + "," + caller + "," + callDate + "," + flag  + "," + callee + "," + callDuration;
    }
}
