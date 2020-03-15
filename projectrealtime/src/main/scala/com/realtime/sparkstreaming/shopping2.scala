package com.realtime.sparkstreaming

import java.io

import com.realtime.util.{IpUtil, JedisClusterConnectionPoolUtil, saveHdfs}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}

object shopping2 {
  def main(args: Array[String]): Unit = {

    val ssc = StreamingContext.getOrCreate("D:\\shopping",userdataEtlconsumer _)

    ssc.start()
    ssc.awaitTermination()
  }

  def userdataEtlconsumer:StreamingContext = {
    val spark = new SparkConf().setMaster("local[*]").setAppName("shopping")
    val ssc = new StreamingContext(spark, Seconds(20))
    //持久化到本地

    val ipRules = ssc.sparkContext.textFile("C:\\Users\\弓长润泽Z\\Desktop\\input\\ip\\rule\\ip.txt")
    val rules = ipRules.map(line => {
      val arr = line.split("[|]")
      (arr(2).toLong, arr(3).toLong, arr(6))
    })

    val broadcastRule: Broadcast[RDD[(Long, Long, String)]] = ssc.sparkContext.broadcast(rules)

    ssc.checkpoint("D:\\shopping")
    //日志级别
    ssc.sparkContext.setLogLevel("error")
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "node4:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "earliest", //latest
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = Array("shopping")
    //创建kafka连接
    val stream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String,String](topics, kafkaParams)
    )
    //用户名 商品类别 商品名称 单价 购买数量 购买时间
    stream.foreachRDD(kafkaRdd => {

      if(!kafkaRdd.isEmpty()) {

        //取出偏移量
        val offsetRanges = kafkaRdd.asInstanceOf[HasOffsetRanges].offsetRanges
        //业务
        val lines = kafkaRdd.map(_.value())

        val filterLines: RDD[String] = lines.filter(line => {
          val fields = line.split(",")
          if (fields.length == 6) {
            true
          } else {
            false
          }
        })

        //用户名 商品类别 商品名称 单价 购买数量 购买时间 ip地址
        //return：购买时间:城市:用户名:商品名称 订单金额
        val resRedis: RDD[(String, Int)] = filterLines.map(line => {
          val arr = line.split(",")
          val city = IpUtil.searchIp(broadcastRule.value.collect(), arr(6).toLong)
          ((arr(5) + ":" + city + ":" + arr(0) + ":" + arr(2)), arr(4).toInt * arr(5).toInt)
        })

        //存入redis
        val conn = JedisClusterConnectionPoolUtil.getConnection
        resRedis.foreach(x => {
          conn.incrBy(x._1,x._2)
        })
        //存入redis2 用作网页展示
        resRedis.foreach(x => {
          val exist = JedisClusterConnectionPoolUtil.hexist("user",x._1)
          if (exist){
            val value = JedisClusterConnectionPoolUtil.hget("user",x._1)
            JedisClusterConnectionPoolUtil.hset("user",x._1,(x._2+value).toString())
          }
          JedisClusterConnectionPoolUtil.hset("user",x._1,x._2.toString)
        })

        //用户名 商品类别 商品名称 单价 购买数量 购买时间 ip地址
        //return:用户名 商品名称 单价 购买数量 订单金额 购买时间 城市
        val resHdfs: RDD[(String,String,String,String,Int,String,String)] = filterLines.map(line => {
          val arr = line.split(",")
          val city = IpUtil.searchIp(broadcastRule.value.collect(),arr(6).toLong)
          (arr(0),arr(2),arr(3),arr(4),arr(3).toInt * arr(4).toInt,arr(5),city)
        })

        saveHdfs.saveToHdfs(resHdfs)

        //        println(resHdfs.collect().toBuffer)
        //        val tuples = resRedis.collect()

        //        println(tuples.toBuffer)

        println("================kafka偏移量打印=======================")
        offsetRanges.foreach(x => {
          println(s"kafkapartition=${x.partition}  kafkapartitionoffsets=${x.fromOffset}")
        })
        println("======================================================")

        //手动更新kafka偏移量
        stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
      }
    })
    ssc
  }
}
