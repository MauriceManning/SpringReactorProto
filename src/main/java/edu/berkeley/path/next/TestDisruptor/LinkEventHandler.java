package edu.berkeley.path.next.TestDisruptor;

import com.lmax.disruptor.EventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;




/**
 *
 */
@Configuration
@ComponentScan
public class LinkEventHandler implements EventHandler<LinkEvent> {

    @Autowired
    CountDownLatch disruptorLinksLatch;

    protected Logger logger;
    int counter;
    Jedis jedis;
    Pipeline pipe;
    List bucket;

    long ordinal;
    long numberOfConsumers;


    public void onEvent(LinkEvent event, long sequence, boolean endOfBatch) throws IOException, ClassNotFoundException, InterruptedException
    {
        //only log info at the end of a batch receive

        //LinkDataRaw newLink = (LinkDataRaw) deserialize(event.get());


        if ((sequence % numberOfConsumers) == ordinal) {


            LinkDataRaw newLink = (LinkDataRaw) deserialize(event.get());

            disruptorLinksLatch.countDown();


            //bucket.add(event.get());

            if (endOfBatch) {
                //logger.info("LkHndlr1  count:  " + counter++);
                //this desearialize will slow things down.
                //LinkDataRaw newLink = (LinkDataRaw) deserialize(event.get());
                //logger.info("LinkEventHandler endOfBatch. obj speed limit: " + newLink.getSpeedLimit());
                //logger.info("LinkEventHandler endOfBatch. counter: " + counter);

//            ListIterator<byte[]> iter = bucket.listIterator();
//            while (iter.hasNext()){
//                logger.info("LH1" + iter.next());
//                counter++;
//                disruptorLinksLatch.countDown();
//            }
//            bucket.clear();

            }
        }

    }

    public  Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }

}
