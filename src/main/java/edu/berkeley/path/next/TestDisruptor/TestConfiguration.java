package edu.berkeley.path.next.TestDisruptor;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.util.DaemonThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.annotation.Bean;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.processor.spec.ProcessorSpec;
import reactor.core.spec.Reactors;
import reactor.event.Event;
import reactor.function.Consumer;
import reactor.function.Supplier;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;


public class TestConfiguration {

    private int NUMBER_OF_LINKS = 10000000;



    @Bean public RunTest runTest() {
        RunTest rt =  new RunTest();
        rt.NUMBER_OF_LINKS = NUMBER_OF_LINKS;
        rt.linkMgr = new LinkManager();
        // Executor that will be used to construct new threads for consumers
        //rt.executor = Executors.newSingleThreadExecutor(DaemonThreadFactory.INSTANCE);
        rt.executor = Executors.newCachedThreadPool();    //   newFixedThreadPool(4);  //.newCachedThreadPool();
        return rt;
    }

    @Bean
    public Publisher publisher(){
        Publisher pub = new Publisher();
        pub.linkMgr = new LinkManager();

        return pub;
    }

    @Bean
    public MiniPublisher minipublisher(){
        MiniPublisher pub = new MiniPublisher();

        return pub;
    }

    @Bean
    public Receiver receiver(){
        Receiver recv = new Receiver();
        recv.logger = LogManager.getLogger(TestOne.class.getName());
        return recv;
    }

    @Bean
    public MiniReceiver minireceiver(){
        MiniReceiver recv = new MiniReceiver();
        recv.logger = LogManager.getLogger(TestOne.class.getName());
        return recv;
    }

    @Bean
    public LinkEventHandler linkEventHandler(){
        LinkEventHandler recv = new LinkEventHandler();
        recv.logger = LogManager.getLogger(LinkEventHandler.class.getName());
        recv.counter = 0;
        recv.bucket = new ArrayList<byte[]>();
//        recv.jedis = new Jedis("localhost");
//        recv.pipe = recv.jedis.pipelined();

        recv.ordinal = 0;
        recv.numberOfConsumers = 4;

        return recv;

    }

    @Bean
    public LinkEventHandler2 linkEventHandler2(){
        LinkEventHandler2 recv = new LinkEventHandler2();
        recv.logger = LogManager.getLogger(LinkEventHandler.class.getName());
        recv.counter = 0;
        recv.bucket = new ArrayList<byte[]>();
        recv.handlrNum = 2;

        recv.ordinal = 1;
        recv.numberOfConsumers = 4;

        return recv;

    }

    @Bean
    public LinkEventHandler2 linkEventHandler3(){
        LinkEventHandler2 recv = new LinkEventHandler2();
        recv.logger = LogManager.getLogger(LinkEventHandler.class.getName());
        recv.counter = 0;
        recv.bucket = new ArrayList<byte[]>();
        recv.handlrNum = 3;

        recv.ordinal = 2;
        recv.numberOfConsumers = 4;

        return recv;

    }

    @Bean
    public LinkEventHandler2 linkEventHandler4(){
        LinkEventHandler2 recv = new LinkEventHandler2();
        recv.logger = LogManager.getLogger(LinkEventHandler.class.getName());
        recv.counter = 0;
        recv.bucket = new ArrayList<byte[]>();
        recv.handlrNum = 4;

        recv.ordinal = 3;
        recv.numberOfConsumers = 4;

        return recv;

    }


    @Bean
    public CountDownLatch latch(){
        return new CountDownLatch(NUMBER_OF_LINKS);
    }

    @Bean
    public CountDownLatch minilatch(){
        return new CountDownLatch(NUMBER_OF_LINKS);
    }

    @Bean
    public CountDownLatch disruptorLinksLatch(){
        return new CountDownLatch(NUMBER_OF_LINKS);
    }

    @Bean
    Environment env() {
        return new Environment();
    }

    @Bean
    Reactor createReactor(Environment env) {
        return Reactors.reactor()
                .env(env)
                .dispatcher(Environment.THREAD_POOL)
                .get();
    }

}
