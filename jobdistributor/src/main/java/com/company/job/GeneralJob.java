package com.company.job;

import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by suresh on 5/6/15.
 */
public abstract class GeneralJob<T> {
    private ArrayList metaData = new ArrayList();
    private String UJID= UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d").randomUUID().toString();
    private Socket socketHandler;
    private Slave slave;

    public Slave getSlave() {
        return slave;
    }

    public void setSlave(Slave slave) {
        this.slave = slave;
    }

    public Socket getSocketHandler() {
        return socketHandler;
    }

    public void setSocketHandler(Socket socketHandler) {
        this.socketHandler = socketHandler;
    }
    public String getUJID(){
        return this.UJID;
    }
    public ArrayList getMetaData() {
        return metaData;
    }

    public void setMetaData(ArrayList metaData) {
        this.metaData = metaData;
    }

    public abstract T doWork();
}
