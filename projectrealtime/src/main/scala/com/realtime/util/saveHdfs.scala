package com.realtime.util

import java.util.Calendar

import org.apache.spark.rdd.RDD;

object saveHdfs{

  def saveToHdfs(userLogs: RDD[(String,String,String,String,Int,String,String)]) = {
    //获取当前时间
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val hour = c.get(Calendar.HOUR_OF_DAY)
    //val millis = c.getTimeInMillis
    userLogs
      .coalesce(1)  //减少分区，以减少输出到hdfs中的文件数量
      .saveAsTextFile("hdfs://node1:8020/order1705e/"+year+"/"+(month+1)+"/"+day+"/"+hour+"/"+c.getTimeInMillis)

  }
}
