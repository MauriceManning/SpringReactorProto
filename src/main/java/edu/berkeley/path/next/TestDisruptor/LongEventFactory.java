package edu.berkeley.path.next.TestDisruptor;

/**
 *
 */


import com.lmax.disruptor.EventFactory;


public class LongEventFactory implements EventFactory<LongEvent>
{
    public LongEvent newInstance()
    {
        return new LongEvent();
    }
}