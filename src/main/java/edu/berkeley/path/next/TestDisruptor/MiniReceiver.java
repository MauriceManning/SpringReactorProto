package edu.berkeley.path.next.TestDisruptor;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import reactor.event.Event;
import reactor.function.Consumer;



/**
 *
 */

@Configuration
@ComponentScan
class MiniReceiver implements Consumer<Event<Integer>> {

    @Autowired
    CountDownLatch latch;

    protected Logger logger;

    public void accept(Event<Integer> ev) {

        logger.info("MiniReceiver " + ev.getData() );
        latch.countDown();
    }

}