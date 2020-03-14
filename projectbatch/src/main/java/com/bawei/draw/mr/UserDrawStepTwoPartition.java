package com.bawei.draw.mr;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;


public class UserDrawStepTwoPartition extends Partitioner<StepTwoMapBean, NullWritable> {

    @Override
    public int getPartition(StepTwoMapBean stepTwoMapBean, NullWritable nullWritable, int numPartitions) {
        return (stepTwoMapBean.getMdn().hashCode() & Integer.MAX_VALUE) % numPartitions;
    }
}
