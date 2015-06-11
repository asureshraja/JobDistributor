package com.company.job;

/**
 * Created by suresh on 6/7/2015.
 */
public class SampleUsage {
    public static void main(String[] args) {

        Master m = new Master("firstMaster","/home/suresh/Desktop/masterdata/",2000,2001,2002);
        m.start();

        m.submitJob(new Job(4,5));



    }
}
