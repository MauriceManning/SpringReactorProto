package edu.berkeley.path.next.TestDisruptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.CORBA.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.Reactor;
import reactor.event.Event;
import reactor.core.processor.spec.ProcessorSpec;
import reactor.function.Supplier;
import static reactor.event.selector.Selectors.$;


import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import java.util.concurrent.ExecutorService;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@Configuration
@ComponentScan("edu.berkeley.path.next.TestDisruptor")

public class RunTest  {

    @Autowired
    private Reactor rbReactor;
    @Autowired
    private Publisher publisher;
    @Autowired
    private Receiver receiver;
    @Autowired
    private MiniPublisher minipublisher;
    @Autowired
    private MiniReceiver minireceiver;


    // number of links to publish to the reactor
    protected int NUMBER_OF_LINKS;

    protected Environment environment;

    @Bean
    public void run() throws Exception {
        final Logger logger = LogManager.getLogger(RunTest.class.getName());
        logger.info("start runTest. ");

        // Subscribe to topic
        // set the receiver class as the callback when an event or message arrives.
        rbReactor.on($("topic"), receiver);

        // Call the publisher to push the links to the reactor
        publisher.publishLinks(NUMBER_OF_LINKS);



        //THIS SECTION used to compare time with basic ints
        // Subscribe to topic
        // set the receiver class as the callback when an event or message arrives.
        //rbReactor.on($("minitopic"), minireceiver);

        // Call the publisher to push the links to the reactor
        //minipublisher.publishNumbers(NUMBER_OF_LINKS);

    }

    @Bean
    public void runDisruptor() throws Exception {

        final Logger logger = LogManager.getLogger(TestOne.class.getName());
        logger.info("runDisruptor start. ");

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        // Executor that will be used to construct new threads for consumers
        ExecutorService executor = Executors.newSingleThreadExecutor(DaemonThreadFactory.INSTANCE);


        // The factory for the event
        LongEventFactory factory = new LongEventFactory();


        // Construct the Disruptor

        //Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize, executor);

        Disruptor<LongEvent> disruptor = new Disruptor(factory,
                bufferSize,
                executor,
                ProducerType.SINGLE, // Single producer
                new YieldingWaitStrategy()
                );


        // Connect the handler
        disruptor.handleEventsWith(new LongEventHandler());

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        LongEventProducer producer = new LongEventProducer(ringBuffer);

        ByteBuffer bb = ByteBuffer.allocate(8);
        long max = 10000000L;

        long start = System.currentTimeMillis();

        for (long l = 0; l < max; l++)
        {
            bb.putLong(0, l);
            producer.onData(bb);
        }

        long elapsed = System.currentTimeMillis()-start;
        System.out.println("runDisruptor Elapsed time: " + elapsed + "ms");


    }

}
