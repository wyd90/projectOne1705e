import org.apache.spark.sql.SparkSession

object Staticday {
  def main(args: Array[String]): Unit = {
    //伪装成hdfs用户
    System.setProperty("HADOOP_USER_NAME","root")
    val spark = SparkSession.builder().appName("staticDayAct")
      .master("local[*]")
      .enableHiveSupport()
      .getOrCreate()

    //    //用户名 商品类别 商品名称 单价 购买数量 购买时间
    //    spark.sql("create table xiangmu.goods(uid string,type string,name string,price float,num float,time string)" +
    //      " partitioned by(day string)" +
    //      " row format delimited fields terminated by ','");

    //从本地导入数据
    //    spark.sql("load data local inpath '/root/webgoods/2020-02-10.log' into table xiangmu.goods partition(day='2-10')")
    //    spark.sql("load data local inpath '/root/webgoods/2020-02-11.log' into table xiangmu.goods partition(day='2-11')")
    //    spark.sql("load data local inpath '/root/webgoods/2020-02-12.log' into table xiangmu.goods partition(day='2-12')")
    //    spark.sql("load data local inpath '/root/webgoods/2020-02-13.log' into table xiangmu.goods partition(day='2-13')")
    //    spark.sql("load data local inpath '/root/webgoods/2020-02-15.log' into table xiangmu.goods partition(day='2-15')")
    //从hdfs导
    //    spark.sql("load data inpath '/webgoods/2020-02-10.log' into table xiangmu.goods partition(day='2-10')")
    //    spark.sql("load data inpath '/webgoods/2020-02-11.log' into table xiangmu.goods partition(day='2-11')")
    //    spark.sql("load data inpath '/webgoods/2020-02-12.log' into table xiangmu.goods partition(day='2-12')")
    //    spark.sql("load data inpath '/webgoods/2020-02-13.log' into table xiangmu.goods partition(day='2-13')")
    //    spark.sql("load data inpath '/webgoods/2020-02-15.log' into table xiangmu.goods partition(day='2-15')")

    //    //创建日活表
    //    spark.sql("create table xiangmu.goods_active_day(uid string,type string,name string,price float,num float,time string)" +
    //      " partitioned by(day string)" +
    //      " row format delimited fields terminated by ','");
    //统计02-10活跃用户
    //    val active210: DataFrame = spark.sql("select uid,type,name,price,num,time,row_number() over(partition by uid order by time desc) rk from xiangmu.goods where day='2020-02-10'")
    //    active210.createTempView("activeUser")
    //    val active2100 = spark.sql("select uid,type,name,price,num,time where rk=1")
    //    active2100.createTempView("activeUser1")
    //    spark.sql("insert into xiangmu.goods_active_day partition(day='2020-02-10') select * from activeUser1")
    //    active2100.show()
    spark.sql("insert into xiangmu.goods_active_day partition(day='2020-02-10') select * from (select uid,type,name,price,num,time from (select uid,type,name,price,num,time,row_number() over(partition by uid order by time desc) rk from xiangmu.goods where day='2020-02-10') tmp where rk = 1) tmp2")


    //    //创建日新用户表
    //    spark.sql("create table xiangmu.goods_new_day like xiangmu.goods_active_day")
    //    //创建历史用户表
    //    spark.sql("create table xiangmu.goods_history(uid string)")
    //
    //    //统计02-10日新增用户
    //    spark.sql("insert into xiangmu.goods_add_active_day partition(day='2020-02-10') " +
    //      "select tuad.uid uid,tuad.type type,tuad.name name,tuad.price price,tuad.num num,tuad.time time from xiangmu.goods_active_day tuad left join xiangmu.goods_history tuh on tuad.uid = tuh.uid where tuad.day = '2020-02-10' and tuh.uid is NULL")
    //    //更新02-10日历史用户的数据
    //    spark.sql("insert into xiangmu.goods_user_history select uid from xiangmu.goods_add_active_day where day='2020-02-10'")

    spark.stop()

    //    bin/sqoop export --connect "jdbc:mysql://localhost:3306/xiangmu?characterEncoding=UTF-8" --username root --password 123456 --table goods --fields-terminated-by ',' --export-dir '/user/hive/warehouse/xiangmu.db/goods/day=2-15/2020-02-15.log';

  }
}
