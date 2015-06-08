package com.company.job;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by suresh on 5/6/15.
 */
public class Slave {
    private final  String name;
    private final  int threads;
    private final String serverIP;
    private final int serverPortChannel1;
    private final int serverPortChannel2;

    public Slave(String name, int threads, String serverIP, int serverPortChannel1, int serverPortChannel2) {
        this.name = name;
        this.threads = threads;
        this.serverIP = serverIP;
        this.serverPortChannel1 = serverPortChannel1;
        this.serverPortChannel2 = serverPortChannel2;
    }

    public void start(){
        try {
            final Socket jobReceiverHandle = new Socket(serverIP,serverPortChannel1);
            final Socket resultSendingHandle = new Socket(serverIP,serverPortChannel2);
            final Queue jobQueue = new LinkedList<GeneralJob>();
            try {
                ExecutorService threadPool = Executors.newFixedThreadPool(5);
                final ExecutorService executorPool = Executors.newFixedThreadPool(threads);
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            synchronized (jobReceiverHandle){
                                ObjectInputStream in = null;
                                GeneralJob jobToWork;
                                try {

                                            in = new ObjectInputStream(jobReceiverHandle.getInputStream());
                                            jobToWork = (GeneralJob) in.readObject();

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
                    @Override
                    public void run() {
                        while (true) {
                            if (jobQueue.size() > 0) {
                                executorPool.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(jobQueue.size()>0){
                                            ObjectOutputStream out = null;
                                            try {
                                                GeneralJob job;
                                                job = (GeneralJob) jobQueue.remove();

                                                GeneralResult computedResult = new GeneralResult((job).doWork());
                                                computedResult.setJobDetails(job);
                                                synchronized (resultSendingHandle) {
                                                    out = new ObjectOutputStream(resultSendingHandle.getOutputStream());
                                                    out.writeObject(computedResult);
                                                    System.out.println("from " + name);
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
