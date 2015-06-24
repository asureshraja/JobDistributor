package com.company.job;

import com.company.data.MasterNode;

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
    private final int dataPortChannel;
    private final int portChannel2;
    private MasterNode mn;
    private Slave defaultSlave;
    public MasterNode getMasterDataNode() {
        return mn;
    }
    public void submitTo(Socket slave, final GeneralJob jobForSlave){
        final Socket workerSlave=slave;
        threadPool.execute(new Runnable() {
            public void run() {
                try {
                    synchronized (workerSlave){
                        ObjectOutputStream oos = new ObjectOutputStream(workerSlave.getOutputStream());
                        oos.writeObject(jobForSlave);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public Master(String name,String dataFolderName,int portChannel1, int portChannel2,int dataPortChannel) {
        this.name=name;
        this.portChannel1 = portChannel1;
        this.portChannel2 = portChannel2;
        this.dataPortChannel=dataPortChannel;
        this.mn=new MasterNode(dataFolderName,this.dataPortChannel);
        this.mn.start();
        defaultSlave = new Slave("defaultSlave",dataFolderName+"/slavedata/","127.0.0.1",portChannel1,portChannel2,dataPortChannel);
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
            final ServerSocket sendingServerHandler = new ServerSocket(this.portChannel1,100);
            final ServerSocket receivingServerHandler = new ServerSocket(this.portChannel2,100);


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
                                                resultQueue.add((GeneralResult) result);
                                               // System.out.println("received from slave jid" + ((GeneralResult) result).getUJID());
                                               // System.out.println((Integer) ((GeneralResult) resultQueue.remove()).getValue());
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
                                Socket slave = objectReceivingSockets.get(randInt(0, objectReceivingSockets.size()-1));
                                synchronized (slave){
                                    ObjectOutputStream oos = new ObjectOutputStream(slave.getOutputStream());
                                    synchronized (jobQueue){
                                        GeneralJob a = (GeneralJob)jobQueue.remove();
                                        //System.out.println("sending job id" + a.getUJID());
                                        ArrayList<Object> packedWithId=new ArrayList();
                                        packedWithId.add(a);packedWithId.add(a.getUJID());
                                        oos.writeObject(packedWithId);
                                    }
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

        defaultSlave.start();
    }
}
