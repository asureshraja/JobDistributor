package com.company.job;
import com.company.data.DataNode;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by suresh on 5/6/15.
 */
public class Slave {
    private final  String name;
    private final String serverIP;
    private final String dataFolderName;
    private final int serverPortChannel1;
    private final int serverPortChannel2;
    private final int serverDataPort;
    private final Slave selfRef;
    private DataNode dn;

    public DataNode getDataNode() {
        return dn;
    }
    public final Slave getSlaveObject(){
        return this;
    }
    public Slave(String name,String dataFolderName, String serverIP, int serverPortChannel1, int serverPortChannel2,int serverDataPort) {
        this.dataFolderName=dataFolderName;
        this.name = name;
        this.serverIP = serverIP;
        this.serverPortChannel1 = serverPortChannel1;
        this.serverPortChannel2 = serverPortChannel2;
        this.serverDataPort=serverDataPort;
        dn = new DataNode(this.dataFolderName,serverDataPort,serverIP);
        dn.start();
        this.selfRef=this;
    }

    public void start(){
        try {
            final Socket jobReceiverHandle = new Socket(serverIP,serverPortChannel1);
            final Socket resultSendingHandle = new Socket(serverIP,serverPortChannel2);

            final Queue jobQueue = new LinkedList<GeneralJob>();
            try {
                ExecutorService threadPool = Executors.newFixedThreadPool(5);
                final ExecutorService executorPool = Executors.newFixedThreadPool(15);
                threadPool.execute(new Runnable() {
                    public void run() {
                        while (true) {
                            synchronized (jobReceiverHandle){
                                ObjectInputStream in = null;
                                GeneralJob jobToWork;
                                ArrayList packedWorkWithJid;
                                try {

                                            in = new ObjectInputStream(jobReceiverHandle.getInputStream());
                                            packedWorkWithJid = (ArrayList) in.readObject();
                                            jobToWork = (GeneralJob)packedWorkWithJid.get(0);
                                            jobToWork.setUJID((String)packedWorkWithJid.get(1));
                                            jobToWork.setSlave(selfRef);
                                        jobQueue.add(jobToWork);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    try {
                                        jobReceiverHandle.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                });

                threadPool.execute(new Runnable() {
                    public void run() {
                        while (true) {
                            if (jobQueue.size() > 0) {
                                executorPool.execute(new Runnable() {
                                    public void run() {
                                        if(jobQueue.size()>0){
                                            ObjectOutputStream out = null;
                                            try {
                                                GeneralJob job;
                                                job = (GeneralJob) jobQueue.remove();
                                                //System.out.println("received details"+job.getUJID());
                                                GeneralResult computedResult = new GeneralResult((job).doWork());
                                                computedResult.setJobDetails(job);
                                                //System.out.println("result details" + computedResult.getUJID());
                                                synchronized (resultSendingHandle) {
                                                    out = new ObjectOutputStream(resultSendingHandle.getOutputStream());
                                                    out.writeObject(computedResult);
                                              //      System.out.println("from " + name);
                                                }


                                            } catch (Exception e) {
//                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                });

                            }else{
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    jobReceiverHandle.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
