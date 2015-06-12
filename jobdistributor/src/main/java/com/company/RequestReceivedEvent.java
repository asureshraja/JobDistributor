package com.company;

/**
 * Created by suresh on 12/6/15.
 */

public class RequestReceivedEvent
{
    private SocketAndStreams value;
    public void set(SocketAndStreams value)
    {
        this.value = value;
    }
    public SocketAndStreams get(){
        return value;
    }
}

