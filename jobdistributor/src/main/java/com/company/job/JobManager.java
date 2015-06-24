package com.company.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by suresh on 24/6/15.
 */
public class JobManager {
    private final Master master;
    private final HashMap<String,GeneralResult> jidToResult= new HashMap<String,GeneralResult>();
    ExecutorService threadPool = Executors.newFixedThreadPool(3);
    public void start(){

        threadPool.execute(new Runnable() {
            public void run() {
                GeneralResult gr;
                while(true){
                    gr=null;
                    if((gr= master.getResult())!=null){
                        jidToResult.put(gr.getUJID(), gr);
                    }
                }

            }
        });
    }

    public JobManager(Master master){
        this.master=master;
    }
    public void submitJob(Job job){
        //jidToResult.put(job.getUJID(),null);
        this.master.submitJob(job);
    }
    public void submitJobs(ArrayList<GeneralJob> jobs){
        for(GeneralJob job:jobs){
          //  jidToResult.put(job.getUJID(),null);
            this.master.submitJob(job);
        }
    }
    public GeneralResult getResult(Job job){
        GeneralResult gr = null;
        while (true){
            gr = jidToResult.get(job.getUJID());
            if(gr!=null){
                jidToResult.remove(job.getUJID());
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return gr;
    }
    public GeneralResult getResult(String jobId){
        GeneralResult gr = null;
        while (true){
            if((gr = jidToResult.get(jobId))!=null){
                jidToResult.remove(jobId);
                return gr;
            }
        }
    }
    public ArrayList<GeneralResult> getResultsWithJobs(ArrayList<GeneralJob> jobs){
        GeneralResult gr = null;
        ArrayList<GeneralResult> retVal= new ArrayList<GeneralResult>();
        while (true){
            for(GeneralJob gj : jobs){
                if((gr = jidToResult.get(gj.getUJID()))!=null){
                    jidToResult.remove(gj.getUJID());
                    retVal.add(gr);
                }
            }
            if(jobs.size()==retVal.size()){
                return retVal;
            }
        }
    }
    public ArrayList<GeneralResult> getResultsWithJobIds(ArrayList<String> jobIds){
        GeneralResult gr = null;
        ArrayList<GeneralResult> retVal= new ArrayList<GeneralResult>();
        while (true){
            for(String jid : jobIds){
                if((gr = jidToResult.get(jid))!=null){
                    jidToResult.remove(jid);
                    retVal.add(gr);
                }
            }
            if(jobIds.size()==retVal.size()){
                return retVal;
            }
        }
    }
}
