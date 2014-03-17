package biz.paluch.akka;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 14.03.14 07:36
 */
public interface StatisticActor {
    long getProcessedMessages();

    long getMessageTime();
}
