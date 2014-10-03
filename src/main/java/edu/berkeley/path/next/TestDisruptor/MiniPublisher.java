package edu.berkeley.path.next.TestDisruptor;

import java.util.concurrent.CountDownLatch;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.Reactor;
import reactor.event.Event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */

public class MiniPublisher {

    @Autowired
    Reactor reactor;

    @Autowired
    CountDownLatch latch;

    public void publishNumbers(int numberOfNumbers) throws InterruptedException {
        long start = System.currentTimeMillis();

        AtomicInteger counter = new AtomicInteger(1);

        for (int i=0; i < numberOfNumbers; i++) {
            reactor.notify("minitopic", Event.wrap(counter.getAndIncrement()));
        }

        latch.await();

        long elapsed = System.currentTimeMillis()-start;

        System.out.println("MiniPublisher Elapsed time: " + elapsed + "ms");
        System.out.println("Average time per number: " + elapsed/ numberOfNumbers + "ms");
    }

}