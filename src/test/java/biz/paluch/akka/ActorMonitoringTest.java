package biz.paluch.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.SmallestMailboxRouter;
import akka.testkit.JavaTestKit;
import com.typesafe.config.ConfigFactory;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 17.03.14 10:18
 */
public class ActorMonitoringTest {
    private ActorSystem system;
    private ActorRef ref;

    @Before
    public void setup() {
        system = ActorSystem.create("test-system", ConfigFactory.load());
        ref = system.actorOf(Props.create(TestActor.class).withRouter(new SmallestMailboxRouter(50)), "TestActor");
    }

    @After
    public void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testMonitoringWhileProcessing() throws Exception {

        for (int i = 0; i < 100; i++) {
            ref.tell("some message", ActorRef.noSender());
        }

        Thread.sleep(200);

        ActorMetrics stats = StatisticCollector.stats(ref);

        assertEquals(0, stats.getQueueSize());
        assertEquals(50, stats.getChildren().size());

        ActorMetrics childMetric = stats.getChildren().get(0);

        assertThat(childMetric.getQueueSize(), new BaseMatcher<Integer>() {
            @Override
            public boolean matches(Object item) {
                Integer v = (Integer) item;

                return v > 0;
            }

            @Override
            public void describeTo(Description description) {

            }
        });
    }

    @Test
    public void testMonitoringAfterProcessing() throws Exception {

        for (int i = 0; i < 100; i++) {
            ref.tell("some message", ActorRef.noSender());
        }

        Thread.sleep(5000);

        ActorMetrics stats = StatisticCollector.stats(ref);

        assertEquals(50, stats.getChildren().size());
        assertEquals(0, stats.getQueueSize());

        assertEquals(50, stats.getChildren().size());
        ActorMetrics childMetric = stats.getChildren().get(0);
        assertEquals(0, childMetric.getQueueSize());

        long processed = 0;

        for (ActorMetrics actorMetrics : stats.getChildren()) {
            processed += actorMetrics.getProcessedMessages();
        }

        assertEquals(100, processed);
    }
}
