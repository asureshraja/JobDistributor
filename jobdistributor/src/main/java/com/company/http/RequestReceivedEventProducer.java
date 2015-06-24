package com.company.http;

/**
 * Created by suresh on 12/6/15.
 */
import com.lmax.disruptor.RingBuffer;

public class RequestReceivedEventProducer
{
    private final RingBuffer<RequestReceivedEvent> ringBuffer;

    public RequestReceivedEventProducer(RingBuffer<RequestReceivedEvent> ringBuffer)
    {
        this.ringBuffer = ringBuffer;
    }

    public void onData(SocketAndStreams ss)
    {
        long sequence = ringBuffer.next();  // Grab the next sequence
        try
        {
            
            RequestReceivedEvent event = ringBuffer.get(sequence); // Get the entry in the Disruptor
            // for the sequence
            event.set(ss);  // Fill with data
        }
        finally
        {
            ringBuffer.publish(sequence);
        }
    }
}
