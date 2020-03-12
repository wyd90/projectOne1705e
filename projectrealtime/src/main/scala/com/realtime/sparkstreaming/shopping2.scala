package com.realtime.sparkstreaming

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}

object shopping2 {
  def main(args: Array[String]): Unit = {
    val spark = new SparkConf().setMaster("local[*]").setAppName("shopping")
    val ssc = new StreamingContext(spark, Seconds(20))

    //持久化到本地
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
        //用户名 商品类别 商品名称 单价 购买数量 购买时间
        val resRedis = lines.map(line => {
          val arr = line.split(",")
          if (arr.length==6){
            ((arr(0)+":"+arr(2)), arr(4))
          }else{
            "数据格式错误"
          }

        })

        //用户名 商品类别 商品名称 单价 购买数量 购买时间
        val resHdfs = lines.map(line => {
          val arr = line.split(",")
          if (arr.length == 6) {
            (arr(0),arr(1), arr(2), arr(3),arr(4),arr(5),arr(3).toInt*arr(4).toInt )
          } else {
            "数据格式错误"
          }
        })
        println(resHdfs.collect().toBuffer)
        val tuples = resRedis.collect()

        println(tuples.toBuffer)

        println("================kafka偏移量打印=======================")
        offsetRanges.foreach(x => {
          println(s"kafkapartition=${x.partition}  kafkapartitionoffsets=${x.fromOffset}")
        })
        println("======================================================")

        //手动更新kafka偏移量
        stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
      }
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
