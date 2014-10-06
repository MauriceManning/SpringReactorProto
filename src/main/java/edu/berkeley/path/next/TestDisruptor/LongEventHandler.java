package edu.berkeley.path.next.TestDisruptor;


import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 */

public class LongEventHandler implements EventHandler<LongEvent>
{
    protected Logger logger;

    public LongEventHandler()
    {
        logger = LogManager.getLogger(TestOne.class.getName());
        logger.info("LongEventHandler start. ");
    }

    public void onEvent(LongEvent event, long sequence, boolean endOfBatch)
    {
        if (endOfBatch)
            logger.info(event.toString());

    }
}