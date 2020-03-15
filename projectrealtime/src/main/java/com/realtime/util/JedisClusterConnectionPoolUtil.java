package com.realtime.util;


import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.rdd.RDD;
import redis.clients.jedis.*;
import scala.Tuple2;

import java.util.*;

public class JedisClusterConnectionPoolUtil{
    //配置文件
    private static JedisPoolConfig config = null;
    //集群所有节点信息
    private static Set<HostAndPort> nodes = null;

    private static JedisCluster jedisCluster = null;


    static {
        config = new JedisPoolConfig();
        //设置最大连接数10
        config.setMaxTotal(10);
        //设置最大空闲连接数5
        config.setMaxIdle(5);
        //有效性检查
        config.setTestOnBorrow(true);

        nodes = new HashSet<>();
        nodes.add(new HostAndPort("192.168.56.101", 6379));
        nodes.add(new HostAndPort("192.168.56.101", 7000));
        nodes.add(new HostAndPort("192.168.56.102", 6379));
        nodes.add(new HostAndPort("192.168.56.102", 7000));
        nodes.add(new HostAndPort("192.168.56.103", 6379));
        nodes.add(new HostAndPort("192.168.56.103", 7000));

    }

    public static JedisCluster getConnection() {
        if (jedisCluster == null) {
            //创建redis集群连接
            jedisCluster = new JedisCluster(nodes, config);
        }
        return jedisCluster;
    }


    //查询所有Hash
    public static  Map<String, String> hgetAll(String key) {
        JedisCluster conn = JedisClusterConnectionPoolUtil.getConnection();
        Map<String, JedisPool> clusterNodes = conn.getClusterNodes();
        Set<Map.Entry<String, JedisPool>> entries = clusterNodes.entrySet();
        Map<String, String> value = null;
        for (Map.Entry<String, JedisPool> node : entries) {
            JedisPool jedisPool = node.getValue();
            Jedis jedis = jedisPool.getResource();

            value = jedis.hgetAll(key);
            jedis.close();


        }
        return value;
    }
//存入hm类型的
    public static String hmset(String key, String item) {
        JedisCluster conn = JedisClusterConnectionPoolUtil.getConnection();
        Map<String, JedisPool> clusterNodes = conn.getClusterNodes();
        Set<Map.Entry<String, JedisPool>> entries = clusterNodes.entrySet();
        String result = null;
        for (Map.Entry<String, JedisPool> node : entries) {
            JedisPool jedisPool = node.getValue();
            Jedis jedis = jedisPool.getResource();
//            String hmset = jedis.hmset(item);
        }
        return result;
    }


}


//    ////获取keyAndValue
//    public static void main(String[] args) {
//        JedisCluster conn = JedisClusterConnectionPool.getConnection();
//
//        Map<String, JedisPool> clusterNodes = conn.getClusterNodes();
//        Set<Map.Entry<String, JedisPool>> entries = clusterNodes.entrySet();
//        for (Map.Entry<String, JedisPool> node : entries) {
//            JedisPool jedisPool = node.getValue();
//            Jedis jedis = jedisPool.getResource();
//
//            String nodeName = node.getKey();
//            System.out.println("================" + nodeName + "======================");
//            Set<String> keySet = jedis.keys("*");
//            byte[][] keys = keySet.toArray(new byte[keySet.size()][]);
//            // 获取所有value
//            byte[][] values = jedis.mget(keys).toArray(new byte[keySet.size()][]);
//
//            // 打印key-value对
//            for (int i = 0; i < keySet.size(); ++i) {
//                System.out.println(byte2hex(keys[i]) + " --- " + byte2hex(values[i]));
//            }
//
//            long end = System.currentTimeMillis();
//
//            System.out.println("====================================================");
//        }
//
//        conn.close();
//    }
//
//    private static String byte2hex(byte[] buffer) {
//        String h = "0x";
//
//        for (byte aBuffer : buffer) {
//            String temp = Integer.toHexString(aBuffer & 0xFF);
//            if (temp.length() == 1) {
//                temp = "0" + temp;
//            }
//            h = h + " " + temp;
//        }
//
//        return h;
//
//    }

//    public static void main(String[] args) {
//        JedisCluster conn = JedisClusterConnectionPool.getConnection();
////        String scoreStr = conn.get("zhangchaobing");
////        Integer score = Integer.valueOf(scoreStr);
////        System.out.println(score);
//
//        Map<String, JedisPool> clusterNodes = conn.getClusterNodes();
//        Set<Map.Entry<String, JedisPool>> entries = clusterNodes.entrySet();
//        for(Map.Entry<String,JedisPool> node : entries){
//            JedisPool jedisPool = node.getValue();
//            Jedis jedis = jedisPool.getResource();
//            String nodeName = node.getKey();
//            System.out.println("================"+nodeName+"======================");
//            Set<String> keys = jedis.keys("*");
//            for(String key : keys) {
//                System.out.println(key);
//            }
//            System.out.println("====================================================");
//        }
//
//        conn.close();
//    }





