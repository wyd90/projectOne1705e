package com.bawei.draw.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class StartUserDemo1 {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(StartUserDemo1.class);
        job.setJobName("UserDrawStepOne");

        job.setMapperClass(UserDrawStepOne.UserDrawStepOneMapper.class);
        job.setReducerClass(UserDrawStepOne.UserDrawStepOneReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.addInputPath(job, new Path("D:\\yarnDate\\3.8task\\input"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\yarnDate\\3.8task\\output1"));

        if(job.waitForCompletion(true)) {
            conf.addResource("hbase-site.xml");
            Job job1 = Job.getInstance(conf);
            job1.setJarByClass(StartUserDemo1.class);
            job1.setJobName("ReadHBase");
            //初始化读取HBase表
            TableMapReduceUtil.initTableMapperJob(
                    "t_draw",new Scan()
                    , UserDrawReadHbase.UserDrawReadHBaseMapper.class
                    ,Text.class
                    ,NullWritable.class
                    ,job1
                    ,false);

            job1.setReducerClass(UserDrawReadHbase.UserDrawReadHBaseReducer.class);
            job1.setOutputKeyClass(Text.class);
            job1.setOutputValueClass(NullWritable.class);

            FileOutputFormat.setOutputPath(job1, new Path("D:\\yarnDate\\3.8task\\input"));

            if(job1.waitForCompletion(true)) {
                Job job2 = Job.getInstance(conf);
                job2.setJarByClass(StartUserDemo1.class);
                job2.setJobName("UserDrawStepTwo");

                job2.setMapperClass(UserDrawStepTwo.UserDrawStepTwoMapper.class);
                job2.setReducerClass(UserDrawStepTwo.UserDrawStepTwoReducer.class);

                job2.setMapOutputKeyClass(StepTwoMapBean.class);
                job2.setMapOutputValueClass(NullWritable.class);
                job2.setOutputKeyClass(Text.class);
                job2.setOutputValueClass(NullWritable.class);

                job2.setPartitionerClass(UserDrawStepTwoPartition.class);
                job2.setGroupingComparatorClass(UserDrawStepTwoGroup.class);

                FileInputFormat.addInputPath(job2, new Path("D:\\yarnDate\\3.8task\\output1"));
                FileInputFormat.addInputPath(job2, new Path("D:\\yarnDate\\3.8task\\input"));
                FileOutputFormat.setOutputPath(job2, new Path("D:\\yarnDate\\3.8task\\output2"));

                //把计算好的数据从新覆盖hbase中的数据
                if(job2.waitForCompletion(true)) {
                    Job job3 = Job.getInstance(conf);
                    job3.setJarByClass(StartUserDemo1.class);
                    job3.setJobName("PutDataToHBase");

                    job3.setMapperClass(UserDrawStepPutHbase.UserDrawPutHBaseMapper.class);
                    TableMapReduceUtil.initTableReducerJob("t_draw", UserDrawStepPutHbase.UserDrawPutHBaseReducer.class,job3);

                    job3.setMapOutputKeyClass(Text.class);
                    job3.setMapOutputValueClass(NullWritable.class);
                    job3.setOutputKeyClass(NullWritable.class);
                    job3.setOutputValueClass(Put.class);

                    FileInputFormat.addInputPath(job3, new Path("D:\\yarnDate\\3.8task\\output2"));
                    job3.waitForCompletion(true);
                }
            }

        }
    }
}
