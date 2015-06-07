package com.company;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by suresh on 5/6/15.
 */
public class GeneralResult implements Serializable {
    private Object value;
    private Socket socketHandler;
    private String UJID=UUID.randomUUID().toString();

    public String getUJID(){
        return this.UJID;
    }

    public void setUJID(String UJID) {
        this.UJID = UJID;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Socket getSocketHandler() {
        return socketHandler;
    }

    public void setSocketHandler(Socket socketHandler) {
        this.socketHandler = socketHandler;
    }

    private ArrayList metaData=new ArrayList();
    public GeneralResult(Object value) {
        this.value = value;
    }

    public void setJobDetails(GeneralJob generalJob){
        setMetaData(generalJob.getMetaData());
        setUJID(generalJob.getUJID());
    }
    public ArrayList getMetaData() {
        return metaData;
    }

    public void setMetaData(ArrayList metaData) {
        this.metaData = metaData;
    }

    public Object getValue() {
        return value;
    }


}
