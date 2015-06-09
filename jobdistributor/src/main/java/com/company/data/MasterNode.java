package com.company.data;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by suresh on 8/6/15.
 */
public class MasterNode {
    final ExecutorService threadPool = Executors.newFixedThreadPool(5);
    final ArrayList<Socket> dataReceivingSockets = new ArrayList<Socket>();
    public void sendTo(final Socket receiverHandle, final boolean permanent, final String tablename, final String commaSeparatedRecord){
        threadPool.execute(new Runnable() {
            public void run() {
                if(permanent){
                    String data="perm-"+tablename+"-"+commaSeparatedRecord;
                    synchronized (receiverHandle){
                        try {
                            DataOutputStream dos = new DataOutputStream(receiverHandle.getOutputStream());
                            dos.writeUTF(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }else{
                    String data="perm-"+tablename+"-"+commaSeparatedRecord;
                    synchronized (receiverHandle){
                        try {
                            DataOutputStream dos = new DataOutputStream(receiverHandle.getOutputStream());
                            dos.writeUTF(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    public void sendTo(final Socket receiverHandle, final boolean permanent, final String tablename, final ArrayList<String> commaSeparatedRecords){
        threadPool.execute(new Runnable() {
            public void run() {
                if(permanent){
                    synchronized (receiverHandle){
                        try {
                            DataOutputStream dos = new DataOutputStream(receiverHandle.getOutputStream());
                            for(String data:commaSeparatedRecords){
                                dos.writeUTF("perm-"+tablename+"-"+data);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    synchronized (receiverHandle){
                        try {
                            DataOutputStream dos = new DataOutputStream(receiverHandle.getOutputStream());
                            for(String data:commaSeparatedRecords){
                                dos.writeUTF("temp-"+tablename+"-"+data);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void sendAll(final boolean permanent, final String tablename, final ArrayList<String> commaSeparatedRecords){
        for(Socket s: dataReceivingSockets){
            sendTo(s,permanent,tablename,commaSeparatedRecords);
        }
    }
    public void sendAll(final boolean permanent, final String tablename, final String commaSeparatedRecord){
        for(Socket s: dataReceivingSockets){
            sendTo(s,permanent,tablename,commaSeparatedRecord);
        }
    }
    public ArrayList<Socket> getClients(){
        return dataReceivingSockets;
    }

    public void start() {
        try {
            final ServerSocket sendingServerHandler = new ServerSocket(2002,100);
            threadPool.execute(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            final Socket client = sendingServerHandler.accept();
                            synchronized (dataReceivingSockets) {
                                dataReceivingSockets.add(client);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
