package edu.berkeley.path.next.TestDisruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * Created by mauricemanning on 10/6/14.
 */
public class LinkEventProducer {
    private final RingBuffer<LinkEvent> ringBuffer;

    public LinkEventProducer(RingBuffer<LinkEvent> ringBuffer)
    {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorOneArg<LinkEvent, ByteBuffer> TRANSLATOR =
            new EventTranslatorOneArg<LinkEvent, ByteBuffer>()
            {
                public void translateTo(LinkEvent event, long sequence, ByteBuffer bb)
                {
                    event.set(bb.array());
                }
            };

    public void onData(ByteBuffer bb)
    {
        ringBuffer.publishEvent(TRANSLATOR, bb);
    }
}
