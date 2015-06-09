package com.company.data;

import com.company.datatypes.KyotoSystem;
import com.company.job.GeneralJob;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.SocketHandler;

/**
 * Created by suresh on 8/6/15.
 */
public class DataNode {
    final ExecutorService threadPool = Executors.newFixedThreadPool(5);

    public void start() {
        final KyotoSystem ks=new KyotoSystem();
        try {
            final Socket receiverHandle = new Socket("127.0.0.1",2002);
            threadPool.execute(new Runnable() {
                public void run() {
                    while (true) {
                        synchronized (receiverHandle){
                            DataInputStream in = null;
                            try {
                                in = new DataInputStream(receiverHandle.getInputStream());
                                String temp[] = in.readLine().split("-");
                                if(temp[0].equals("perm")){
                                    ks.insertIntoPermTable(temp[1],temp[2]);
                                }else{
                                    ks.insertIntoTempTable(temp[1],temp[2]);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {
                                    receiverHandle.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                break;
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
