package com.company.job;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.*;

public class Master {
    private String name;
    private final int portChannel1;
    private final int portChannel2;

    public Master(String name,int portChannel1, int portChannel2) {
        this.name=name;
        this.portChannel1 = portChannel1;
        this.portChannel2 = portChannel2;
    }
    public GeneralResult getResult(){
        if (resultQueue.size()>0){
            return (GeneralResult)resultQueue.remove();
        }
        else{
            return null;
        }
    }
    final Queue jobQueue = new LinkedList<GeneralJob>();
    final Queue resultQueue = new LinkedList<GeneralResult>();
    final ExecutorService threadPool = Executors.newFixedThreadPool(5);
    final ArrayList<Socket> resultSendingSockets = new ArrayList<Socket>();
    final ArrayList<Socket> objectReceivingSockets = new ArrayList<Socket>();
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public void submitJob(GeneralJob gj){
        jobQueue.add(gj);
    }
    public void start(){

        try {
            final ServerSocket sendingServerHandler = new ServerSocket(2000,100);
            final ServerSocket receivingServerHandler = new ServerSocket(2001,100);


            //waits for object receiving machines to join network
            threadPool.execute(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            final Socket client = sendingServerHandler.accept();
                            synchronized (objectReceivingSockets) {
                                objectReceivingSockets.add(client);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });


            //waits for result sending sockets
            threadPool.execute(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            final Socket client = receivingServerHandler.accept();
                            resultSendingSockets.add(client);
                            threadPool.execute(new Runnable() {

                                public void run() {
                                    while(true){
                                        synchronized (client){
                                        int resultSocketsSize;
                                        resultSocketsSize = resultSendingSockets.size();
                                        if(resultSocketsSize>0){
                                            try {
                                                ObjectInputStream ois;Object result;
                                                ois = new ObjectInputStream(client.getInputStream());
                                                result = ois.readObject();

                                                resultQueue.add((GeneralResult)result);
                                                System.out.println((Integer) ((GeneralResult) resultQueue.remove()).getValue());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                break;
                                            }
                                        }
                                        }
                                    }
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

            //takes from job queue and send it to object receiving handlers
            threadPool.execute(new Runnable() {
                public void run() {
                    while(true){

                        if(objectReceivingSockets.size()>0 && jobQueue.size()>0){
                            try {
                                ObjectOutputStream oos = new ObjectOutputStream(objectReceivingSockets.get(randInt(0, objectReceivingSockets.size()-1)).getOutputStream());
                                synchronized (jobQueue){
                                    oos.writeObject(jobQueue.remove());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

            //receives results from slaves - resultSendingSockets


        }
        catch (IOException e) {
            System.out.println("unable to make connection on port 2000");

        }
    }
}
