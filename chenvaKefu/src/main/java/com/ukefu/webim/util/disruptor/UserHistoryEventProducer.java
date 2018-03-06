package com.ukefu.webim.util.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.ukefu.util.event.UserEvent;
import com.ukefu.util.event.UserHistoryEvent;

public class UserHistoryEventProducer {
	private final RingBuffer<UserHistoryEvent> ringBuffer;

    public UserHistoryEventProducer(RingBuffer<UserHistoryEvent> ringBuffer)
    {
        this.ringBuffer = ringBuffer;
    }

    public void onData(UserEvent userEvent)
    {
        long id = ringBuffer.next();  // Grab the next sequence
        try{
        	UserHistoryEvent event = ringBuffer.get(id);
        	event.setEvent(userEvent);
        }finally{
            ringBuffer.publish(id);
        }
    }
}
