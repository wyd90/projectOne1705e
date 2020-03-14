package com.bawei.draw.mr;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;


public class UserDrawStepOne {

    public static class UserDrawStepOneMapper extends Mapper<LongWritable, Text,Text,Text> {

        private Text k;
        private Text v;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            k = new Text();
            v = new Text();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] arr = value.toString().split("[|]");
            String uiqKey = arr[0]; //MDN（用户唯一标识）+ 用户使用的APPID


            k.set(uiqKey);
            //      mdn
            v.set(uiqKey+"|"+arr[0]);

            context.write(k,v);
        }
    }

    //统计用户单个app的使用时长和使用次数
    public static class UserDrawStepOneReducer extends Reducer<Text,Text,Text, NullWritable> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            Integer count = 0;
            String mdn = "";
            for(Text value : values) {
                String[] arr = value.toString().split("[|]");
                if(arr.length == 1) {
                    count += 1;
                    mdn = arr[1];
                }
            }
            String uiqKey = key.toString();
            //                     uiqKey      使用了几次
            Text k = new Text(uiqKey + "|" + mdn  + "|" + count);
            context.write(k,NullWritable.get());
        }
    }


}
