package com.bawei.datatransmission.logdata.userclient;

import com.alibaba.fastjson.JSONObject;
import com.bawei.datatransmission.logdata.bean.AppBaseLog;

import java.io.*;
import java.util.Random;

public class LogData {

   //创建随机对象
    private static Random random = new Random();

//    log属性值
    private static String[] user ={"u001","u002","u003","u004","u005","u006","u007","u008","u009","u0010",};

    private static String[] shoptype={"手机","家具","化妆品","食品","图书","服装","家电"};

    private static String[] shopname={"iPhone11","婴儿床","迪奥香水","奶粉","Hadoop权威指南","布莱奥你西服"};

    private static Integer[] price={5000,4449,2999,2000,599,139};

    private static Integer[] count ={2,1,1,2,1,2};

    private static String[] time={"2020-02-10","2020-02-10",",2020-02-11","2020-02-11","2020-02-12","2020-02-13"};

    private static String[] ip ={"16777472","16777472","16785408","16842752","16843264","16908288"};




    public static void main(String[] args) throws InterruptedException, IOException {

        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("C:\\Users\\lenovo\\projectOne1705e\\datatransmission\\docs\\log.txt"));
        for (int i = 0; i < 100; i ++) {
            AppBaseLog abl = new AppBaseLog();

            abl.setUser(user[random.nextInt(user.length)]);
            abl.setShoptype(shoptype[random.nextInt(shoptype.length)]);
            abl.setShopname(shopname[random.nextInt(shopname.length)]);
            abl.setPrice(price[random.nextInt(price.length)]);
            abl.setCount(count[random.nextInt(count.length)]);
            abl.setTime(time[random.nextInt(time.length)]);
            abl.setIp(ip[random.nextInt(ip.length)]);

            String userJson = JSONObject.toJSONString(abl);

            Thread.sleep(1000);

            out.write(userJson+"\n");

            out.flush();

        }
        out.close();
    }



}
