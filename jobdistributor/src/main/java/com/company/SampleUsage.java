package com.company;

import com.company.job.GeneralResult;
import com.company.job.Job;
import com.company.job.JobManager;
import com.company.job.Master;

/**
 * Created by suresh on 6/7/2015.
 */
public class SampleUsage {
    public static void main(String[] args) {

        Master m = new Master("firstMaster","/home/suresh/Desktop/masterdata/",2000,2001,2002);
        m.start();
        JobManager jm = new JobManager(m);
        jm.start();
        Job job = new Job(4, 5);
        Job job1 = new Job(5, 5);
        jm.submitJob(job);
        jm.submitJob(job1);
        System.out.println((Integer) ((GeneralResult) jm.getResult(job)).getValue());
        System.out.println((Integer) ((GeneralResult) jm.getResult(job1)).getValue());

    }
}
