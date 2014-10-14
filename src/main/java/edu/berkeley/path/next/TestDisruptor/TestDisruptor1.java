package edu.berkeley.path.next.TestDisruptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 */

@Configuration
@ComponentScan("edu.berkeley.path.next.TestDisruptor")
public class TestDisruptor1 {

    @Autowired
    static private RunTest      runTest;

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext();
        ctx.register(TestConfiguration.class);
        ctx.refresh();

        final Logger logger = LogManager.getLogger(TestDisruptor1.class.getName());
        logger.info("start test. ");

        RunTest runTest = ctx.getBean(RunTest.class);

        //This tests uses longs to pass thru RB
        //runTest.runDisruptor();

        //This test passes Links thru the RB
        runTest.runDisruptorForLinks();

    }


}
