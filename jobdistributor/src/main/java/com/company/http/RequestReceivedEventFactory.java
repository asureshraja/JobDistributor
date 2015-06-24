package com.company.http;
import com.lmax.disruptor.EventFactory;

/**
 * Created by suresh on 12/6/15.
 */

public class RequestReceivedEventFactory implements EventFactory<RequestReceivedEvent>
{
    public RequestReceivedEvent newInstance()
    {
        return new RequestReceivedEvent();
    }
}
