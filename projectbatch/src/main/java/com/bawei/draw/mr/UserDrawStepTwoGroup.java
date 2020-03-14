package com.bawei.draw.mr;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;


public class UserDrawStepTwoGroup extends WritableComparator {
    public UserDrawStepTwoGroup() {
        super(StepTwoMapBean.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        StepTwoMapBean o1 = (StepTwoMapBean)a;
        StepTwoMapBean o2 = (StepTwoMapBean)b;
        return o1.getMdn().compareTo(o2.getMdn());
    }
}
