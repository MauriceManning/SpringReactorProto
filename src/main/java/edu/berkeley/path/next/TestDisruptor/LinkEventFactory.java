package edu.berkeley.path.next.TestDisruptor;

import com.lmax.disruptor.EventFactory;

/**
 *
 */

public class LinkEventFactory implements EventFactory<LinkEvent>
{
    public LinkEvent newInstance()
    {
        return new LinkEvent();
    }
}