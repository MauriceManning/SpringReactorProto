package edu.berkeley.path.next.TestDisruptor;

import com.lmax.disruptor.EventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 *
 */
public class LinkEventHandler implements EventHandler<LinkEvent> {

    protected Logger logger;

    public LinkEventHandler()
    {
        logger = LogManager.getLogger(TestOne.class.getName());
        logger.info("LinkEventHandler start. ");
    }

    public void onEvent(LinkEvent event, long sequence, boolean endOfBatch) throws IOException, ClassNotFoundException
    {
        //only log info at the end of a batch receive
        if (endOfBatch) {
            //this desearialize will slow things down.
            //LinkDataRaw newLink = (LinkDataRaw) deserialize(event.get());
            //logger.info("LinkEventHandler endOfBatch. obj speed limit: " + newLink.getSpeedLimit());
            logger.info("LinkEventHandler endOfBatch.");
        }

    }

    public  Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }

}
