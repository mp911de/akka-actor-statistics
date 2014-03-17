package biz.paluch.akka;

import akka.actor.UntypedActor;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 17.03.14 10:16
 */
public class TestActor extends UntypedActor implements StatisticActor {
    private long processedMessages;
    private long messageTime;

    @Override
    public long getProcessedMessages() {
        return processedMessages;
    }

    @Override
    public long getMessageTime() {
        return messageTime;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        long start = System.currentTimeMillis();
        try {
            onReceivedMessage(o);
        } finally {
            long duration = System.currentTimeMillis() - start;
            processedMessages++;
            messageTime += duration;
        }
    }

    private void onReceivedMessage(Object o) throws InterruptedException
    {
          Thread.sleep(500);
    }
}
