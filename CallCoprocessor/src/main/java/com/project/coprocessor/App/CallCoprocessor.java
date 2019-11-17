package com.project.coprocessor.App;

import com.project.coprocessor.util.Util;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WALEdit;

import java.io.IOException;
import java.util.Optional;

/**
 * 通话记录协处理器
 * 1.在插入一条主叫通话记录的同时插入一条被叫通话记录
 * 2.在查被叫信息时通过value找出主叫
 */
public class CallCoprocessor implements RegionCoprocessor {

    @Override
    public Optional<RegionObserver> getRegionObserver() {
        return Optional.ofNullable(new CallRefinObserver());
    }

    private static class CallRefinObserver implements RegionObserver{
        @Override
        public void postPut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
            RegionObserver.super.postPut(e, put, edit, durability);
            //获取表名
            String tableName = e.getEnvironment().getRegion().getRegionInfo().getTable().getNameAsString();
            //判断是否为通话记录，不是则跳过
            if (tableName.equals("calllog")){
                return;
            }
            // 获取当前插入的RowKey
            String rowkey = Bytes.toString(put.getRow());
            // 切割
            String[] row = rowkey.split(",");
            // 判断是否是主叫插入，若不是则退出
            if (row[3].equals('0') ){
                return;
            }
            // 获取各个属性值
            String caller= row[1];
            String callDate = row[2];
            String callee = row[3];
            String callDuration = row[5];
            //设置为被叫插入
            String flag = "0";
            //取得哈希值
            String hashCode = Util.getHashCode(callee,callDate,100);

            //被叫RowKey
            String callRow = Util.getRowKey(hashCode,callee,callDate,flag,caller,callDuration);

            //创建Put对象
            Put putCallee = new Put(callRow.getBytes());
            //被叫存储
            putCallee.addColumn("f1".getBytes(),"caller".getBytes(),callee.getBytes());
            putCallee.addColumn("f1".getBytes(),"callDate".getBytes(),callDate.getBytes());
            putCallee.addColumn("f1".getBytes(),"callee".getBytes(),caller.getBytes());
            putCallee.addColumn("f1".getBytes(),"callDuration".getBytes(),callDuration.getBytes());

            //插入表中

            Table table = e.getEnvironment().getConnection().getTable(TableName.valueOf(tableName));
    }

    }


}
