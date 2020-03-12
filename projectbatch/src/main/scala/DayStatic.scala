import org.apache.spark.sql.{DataFrame, SparkSession}

object DayStatic {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("starticGoods")
      .master("local[*]")
      .enableHiveSupport() //启动hive 支持
      .getOrCreate()


    val res: DataFrame = spark.sql("select uid,price from `xiangmu`.`goods` group by uid order by price")
    res.show()


    spark.stop;

  }
}
