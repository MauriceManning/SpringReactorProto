package edu.berkeley.path.next.TestDisruptor;

/**
 * Created by mauricemanning on 10/6/14.
 */


import com.lmax.disruptor.EventFactory;


public class LongEventFactory implements EventFactory<LongEvent>
{
    public LongEvent newInstance()
    {
        return new LongEvent();
    }
}