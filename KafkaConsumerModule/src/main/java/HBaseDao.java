import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;
import java.text.DecimalFormat;

public class HBaseDao {
    //
    private Table table = null;
    // 表名
    private TableName tableName = null;
    // 是否主叫标志位
    private String flag = null;
    //分区数
    private int parttitionsNum = 0;
    public HBaseDao(){
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum",PropertiesUtil.getProperty("hbase.zookeeper.quorum"));
        try{
            Connection conn = ConnectionFactory.createConnection(conf);
            tableName = TableName.valueOf(PropertiesUtil.getProperty("hbase.tablename"));
            table = conn.getTable(tableName);
            flag = PropertiesUtil.getProperty("callerflag");
            parttitionsNum = Integer.parseInt(PropertiesUtil.getProperty("partition.num"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 讲童话计入写入Hbase，设计Rowkey

    }
    public void put(String log){
        // 切割处理日志
        String[] arr = log.split(",");
        String caller = arr[0];
        String callee = arr[1];
        String callDateFormat  = arr[2].replace("/","")
                .replace(" ","")
                .replace(":","");
        String duration = arr[3];
        // 根据主叫号码以及呼叫日期生成哈希码
        // 可以确保同一个用户的同一个月的通话记录会被放在同一个region中
        // 不同用户或者同一用户的不同月份的通话记录尽可能发散存储，避免出现热点
        String hashCode = getHashCode(callee,callDateFormat);
        String rowkey = hashCode + "," + caller +  "," + callDateFormat + "," + flag + "," + callee + "," + duration;
         // 创建put
        Put put = new Put(rowkey.getBytes());
        // 在列族添中加相应的列
        put.addColumn("f1".getBytes(),"caller".getBytes(),caller.getBytes());
        put.addColumn("f1".getBytes(),"callee".getBytes(),callee.getBytes());
        put.addColumn("f1".getBytes(),"callDate".getBytes(),callDateFormat.getBytes());
        put.addColumn("f1".getBytes(),"duration".getBytes(),duration.getBytes());

        //插入表中
        try{
            table.put(put);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 该方法用于计算出该通话记录所在的region区域号
    private String getHashCode(String caller,String callDateFormat){
        //取电话号码最后5位
        int len = caller.length();
        String caller5 = caller.substring(len-5);
        //取年份和月份
        String callDateMonth = callDateFormat.substring(0,6);
        //取哈希值
        int hashCode = (Integer.parseInt(caller5) ^ Integer.parseInt(callDateMonth)) % parttitionsNum;
        //格式化后返回
        DecimalFormat df = new DecimalFormat();
        return df.format(hashCode);

    }
}

