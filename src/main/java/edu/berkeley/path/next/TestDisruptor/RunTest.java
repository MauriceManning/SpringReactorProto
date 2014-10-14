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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

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


    @Autowired
    private LinkEventHandler linkEventHandler;
    @Autowired
    private LinkEventHandler2 linkEventHandler2;

    @Autowired
    private LinkEventHandler2 linkEventHandler3;

    @Autowired
    private LinkEventHandler2 linkEventHandler4;

    @Autowired
    CountDownLatch disruptorLinksLatch;

    // number of links to publish to the reactor
    protected int NUMBER_OF_LINKS;

    protected Environment environment;

    protected  LinkManager linkMgr;

    protected ExecutorService executor;

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

        System.out.println("runDisruptor Number of Longs  to send: " + max );

        long start = System.currentTimeMillis();

        for (long l = 0; l < max; l++)
        {
            bb.putLong(0, l);
            producer.onData(bb);
        }

        long elapsed = System.currentTimeMillis()-start;
        System.out.println("runDisruptor Elapsed time: " + elapsed + "ms");


    }


    @Bean
    public void runDisruptorForLinks() throws Exception {

        final Logger logger = LogManager.getLogger(TestOne.class.getName());
        logger.info("runDisruptorForLinks start. ");

        // create one Link and wrap it in an event so it is ready to publish to Reactor
        LinkDataRaw link = linkMgr.getLink();
        System.out.println("link size: " + sizeof(link));

        // Specify the size of the ring buffer, must be power of 2.
        //int bufferSize = 1024;
        int bufferSize = 262144;  //256 * 1024

        // The factory for the event
        LinkEventFactory factory = new LinkEventFactory();

        // Construct the Disruptor
        //Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize, executor);

        //Commit to a single producer which optimizes the disruptor
        Disruptor<LinkEvent> disruptor = new Disruptor(factory,
                bufferSize,
                executor,
                ProducerType.SINGLE, // Single producer
                new YieldingWaitStrategy()
        );


        // Connect the handler
        //disruptor.handleEventsWith(linkEventHandler);
        disruptor.handleEventsWith(linkEventHandler, linkEventHandler2, linkEventHandler3, linkEventHandler4);

        // "after" sets up a pipeline of handlers, not what we want
        //disruptor.after(linkEventHandler).handleEventsWith(linkEventHandler2);

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<LinkEvent> ringBuffer = disruptor.getRingBuffer();

        LinkEventProducer producer = new LinkEventProducer(ringBuffer);

        //This LinkDataRaw object is 574 bytes
        ByteBuffer bb = ByteBuffer.allocate(574);

        bb.put(getTheBytes(link));
        System.out.println("runDisruptorForLinks bb: " + bb.toString() );

        LinkDataRaw newLink = (LinkDataRaw) deserialize(bb.array());
        //Check that this object is valid
        //System.out.println("runDisruptorForLinks newLink: " + newLink.getSpeedLimit() );
        System.out.println("runDisruptor Number of RawLinkData objects to send: " + NUMBER_OF_LINKS );

        long start = System.currentTimeMillis();

        for (long l = 0; l < NUMBER_OF_LINKS; l++)
        {
            producer.onData(bb);
        }

        disruptorLinksLatch.await();

        long elapsed = System.currentTimeMillis()-start;
        System.out.println("runDisruptorForLinks Elapsed time: " + elapsed + "ms");


    }

    public static int sizeof(Object obj) throws IOException {

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();

        return byteOutputStream.toByteArray().length;
    }

    public static byte[] getTheBytes(Object obj) throws IOException {

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();
        System.out.println("runDisruptorForLinks objectOutputStream: " + objectOutputStream.toString() );

        return byteOutputStream.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }

}
