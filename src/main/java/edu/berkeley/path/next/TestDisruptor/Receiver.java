package edu.berkeley.path.next.TestDisruptor;

import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import reactor.event.Event;
import reactor.function.batch.BatchConsumer;
import reactor.function.Consumer;
import java.util.ArrayList;


@Configuration
@ComponentScan
//@Service
public class Receiver implements BatchConsumer<Event<LinkDataRaw>>  {

    @Autowired
    CountDownLatch latch;

    int writeBatchSize = 1000;
    ArrayList<LinkDataRaw> rlArray = new ArrayList<>(writeBatchSize);

    LinkDataRaw rl;
    protected Logger logger;

    int counter = 0;

    public void start() {

    }

    public void end() {

    }

    public void accept(Event<LinkDataRaw> ev) {
        rl = (LinkDataRaw)ev.getData();

//        counter++;
//        if ( counter < writeBatchSize ) {
//            rlArray.add(rl);
//        }
//        else {
//            logger.info(rlArray.toString());
//            rlArray.clear();
//            counter = 0;
//        }

        //System.out.println("Link " + rl.getSpeedLimit() );
        //logger.info("Link " + rl.getSpeedLimit());

        // this counts down the number of messages received so that we know that everything arrived as expected
        latch.countDown();
    }


}
