package com.project.callloggengenmodule;

import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class App{
    public static Map<String,String> allCallers = new HashMap();
    public static List<String> phoneNumber = new ArrayList<String>();
    static {
        //加载所有用户号码和姓名信息
        allCallers.put("15810092493", "萧邦主");
        allCallers.put("18000696806", "赵贺彪");
        allCallers.put("15151889601", "张倩");
        allCallers.put("13269361119", "王世昌");
        allCallers.put("15032293356", "张涛");
        allCallers.put("17731088562", "张阳");
        allCallers.put("15338595369", "李进全");
        allCallers.put("15733218050", "杜泽文");
        allCallers.put("15614201525", "任宗阳");
        allCallers.put("15778423030", "梁鹏");
        allCallers.put("18641241020", "郭美彤");
        allCallers.put("15732648446", "刘飞飞");
        allCallers.put("13341109505", "段光星");
        allCallers.put("13560190665", "唐会华");
        allCallers.put("18301589432", "杨力谋");
        allCallers.put("13520404983", "温海英");
        allCallers.put("18332562075", "朱尚宽");
        allCallers.put("15902136987", "张翔");
        allCallers.put("13801358247", "杨超凡");
        allCallers.put("15975500987", "何潮辉");
        allCallers.put("13013685036", "庄银泳");
        allCallers.put("15019933667", "萧金辉");
        allCallers.put("18301930136", "黄海锋");
        //加载所有用户号码到此集合
        phoneNumber.addAll(allCallers.keySet());
    }

    public static void main(String[] args) throws Exception {
        while (true){
            if (args.length != 1){
                System.err.println("You should input a path to sava the logs");
                System.exit(1);
            }
            genCallog(args[0]);
        }
//        for (int i=0;i<=100;i++){
//            genCallog("/usr/local/CallLogsSystem/caller_log.log");
//        }

    }

    private static void genCallog(String LogFile) throws Exception {
        Random r = new Random();
        //文件输出流追加写入文件
        FileWriter writer = new FileWriter(LogFile,true);

        String callerNumber = phoneNumber.get(r.nextInt(phoneNumber.size()));
        String callerName = allCallers.get(callerNumber);
        String calleeNumber;
        String calleeName;
        // 产生一个被叫
        while (true){
            calleeNumber = phoneNumber.get(r.nextInt(phoneNumber.size()));
            //主叫与被叫默认不为同一人
            if (!calleeNumber.equals(callerName)){
                calleeName = allCallers.get(calleeNumber);
                break;
            }
        }
        // 通话时间生成
        int year = r.nextInt(2) == 0 ? 2018:2019;
        int month = r.nextInt(12);
        int day = r.nextInt(getDay(month)) + 1;
        int hour = r.nextInt(24);
        int min = r.nextInt(60);
        int sec = r.nextInt(60);
        //使用日期类格式化时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        Date timeDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy/MM/dd hh:mm:ss");
        String time = sdf.format(timeDate);
        //通话时长
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("000");
        // 默认通话时间范围[0,1800)秒
        int dur = r.nextInt(60 * 30);
        String duration = df.format(dur);
        //通话记录
        String log = callerNumber + ","  + calleeNumber + "," + time + "," + duration;
        //将通话记录写入到外部存储系统
        writer.write(log + "\r\n");
        writer.flush();
        //休眠200ms
        Thread.sleep(200);


    }

    private static int getDay(int month) {
        if (month == 2){
            return 28;
        }
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
                || month == 10 || month == 12){
            return 31;
        }
        return 30;
    }
}
