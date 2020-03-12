package com.my.sparkOne

object IPUtil {
  def ipToLong(ip:String):Long = {
    val arr = ip.split("[.]")
    var ipNum = 0L;
    arr.foreach(x => {
      ipNum = x.toLong | ipNum << 8L
    })
    ipNum
  }

  def searchIp(ipRules:Array[(Long,Long,String)],ip:Long): String = {
    val province:String = "未知"
    var low = 0
    var high = ipRules.length - 1
    while (low <= high){
      val middle = (low + high) / 2
      if (ip >= ipRules(middle)._1 && ip <= ipRules(middle)._2){
        return ipRules(middle)._3
      }
      if (ip < ipRules(middle)._1){
        high = middle - 1
      } else {
        low = middle + 1
      }
    }
    province
  }
  def main(args: Array[String]): Unit = {
    val ip = "5"
    val longip = ip.toLong
    println(longip.toBinaryString)
    val newlongip = longip << 8L
    println(newlongip.toBinaryString)
  }
}
