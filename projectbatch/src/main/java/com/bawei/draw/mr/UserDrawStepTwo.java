package com.bawei.draw.mr;


import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import yn.task.bean.UserDraw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class UserDrawStepTwo {

    public static class UserDrawStepTwoMapper extends Mapper<LongWritable, Text,StepTwoMapBean,NullWritable> {

        private StepTwoMapBean stepTwoMapBean;
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            FileSplit fileSplit = (FileSplit) context.getInputSplit();
            String path = fileSplit.getPath().toString();
            if(path.contains("fromhbase")) {
                //读的是已有画像数据
                stepTwoMapBean = new StepTwoMapBean("fromhbase");
            } else {
                //读的是第一步的输出
                stepTwoMapBean = new StepTwoMapBean("fromoutput1");
            }
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] arr = value.toString().split("[|]");

            if("fromhbase".equals(stepTwoMapBean.getStatus())) {
                //读的是已有画像数据
                String mdn = arr[0];
                String male = arr[1];
                String female = arr[2];
                String age1 = arr[3];
                String age2 = arr[4];
                String age3 = arr[5];
                String age4 = arr[6];
                String age5 = arr[7];

                stepTwoMapBean.set(mdn,male,female,age1,age2,age3,age4,age5);

            } else {
                //读的是第一步的输出
                String mdn = arr[1];

                stepTwoMapBean.set (mdn, "NULL", "NULL", "NULL", "NULL", "NULL", "NULL", "NULL");
            }
            context.write(stepTwoMapBean,NullWritable.get());
        }
    }

    //同一个用户，所有app都要聚合到这个reduce中
    public static class UserDrawStepTwoReducer extends Reducer<StepTwoMapBean,NullWritable,Text,NullWritable>{

        private Map<String,String[]> uidMap = null;

        //读取标签库
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            uidMap = new HashMap<String, String[]>();
            Configuration conf = context.getConfiguration();
            FileSystem fs = FileSystem.get(URI.create("file:///"), conf);
            FSDataInputStream in = fs.open(new Path("file:///D:\\yarnDate\\用户画像标签库.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split("[|]");
                String uid = arr[0];
                uidMap.put(String.valueOf(uid),arr);
            }
        }

        @Override
        protected void reduce(StepTwoMapBean key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {

//            if("zzvmMPZpABPYVo0b30uzXQ==".equals(key.getMdn())){
//                System.out.println("zzvmMPZpABPYVo0b30uzXQ==");
//            }
            UserDraw userDraw = null;
            boolean init = true;
            for(NullWritable value : values) {
                StepTwoMapBean stepTwoMapBean = new StepTwoMapBean();
                //把key复制过来
                stepTwoMapBean.set(key.getMdn(),key.getMale(),key.getFemale(),key.getAge1(),key.getAge2(),key.getAge3(),key.getAge4(),key.getAge5());
                stepTwoMapBean.setStatus(key.getStatus());
                //第一次要把从HBase中的数据迭代出来
                if(init) {
                    //初始化userDraw(用户画像)对象
                    if("fromhbase".equals(stepTwoMapBean.getStatus())) {
                        userDraw = new UserDraw(stepTwoMapBean.getMdn(),Double.valueOf(stepTwoMapBean.getMale()),Double.valueOf(stepTwoMapBean.getFemale()),Double.valueOf(stepTwoMapBean.getAge1()),Double.valueOf(stepTwoMapBean.getAge2()),Double.valueOf(stepTwoMapBean.getAge3()),Double.valueOf(stepTwoMapBean.getAge4()),Double.valueOf(stepTwoMapBean.getAge5()));
                    } else {
                        //说明hbase中从来也没有过这条数据，该日志很可能是新增用户产生的
                        userDraw = new UserDraw(key.getMdn(),0.5d,0.5d,0.2d,0.2d,0.2d,0.2d,0.2d);
                        //由于第一条数据就是日志数据，所以第一次就要计算一下
                        calculateData(uidMap,userDraw,stepTwoMapBean);
                    }
                    init =false;
                } else {
                    //不是第一次迭代，后面的都是log数据，直接计算用户画像
                    calculateData(uidMap,userDraw,stepTwoMapBean);
                }
            }
            context.write(new Text(userDraw.toString()),NullWritable.get());
        }

        public void calculateData(Map<String,String[]> uidMap, UserDraw userDraw,StepTwoMapBean stepTwoMapBean) {
            Integer uid = 0;//用户使用的appid
            if(!StringUtils.isEmpty(stepTwoMapBean.getMdn())) {
                uid = Integer.valueOf(stepTwoMapBean.getMdn());
            }



            String[] tabs = uidMap.get(uidMap);
            if(tabs != null) {
                Double male =   Double.valueOf(tabs[2]); //男概率
                Double female = Double.valueOf(tabs[3]); //女概率
                Double age1 = Double.valueOf(tabs[4]);  //年龄段1概率
                Double age2 = Double.valueOf(tabs[5]);
                Double age3 = Double.valueOf(tabs[6]);
                Double age4 = Double.valueOf(tabs[7]);
                Double age5 = Double.valueOf(tabs[8]);

                userDraw.protraitSex(male,female);  //累加计算性别概率
                userDraw.protraitAge(age1,age2,age3,age4,age5); //累加计算年龄概率
            }
        }
    }
}
