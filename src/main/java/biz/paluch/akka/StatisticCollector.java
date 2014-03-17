package biz.paluch.akka;

import scala.collection.Iterator;
import akka.actor.Actor;
import akka.actor.ActorCell;
import akka.actor.ActorRef;
import akka.actor.Cell;
import akka.actor.LocalActorRef;
import akka.actor.RepointableActorRef;
import akka.routing.RoutedActorCell;
import akka.routing.RoutedActorRef;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 14.03.14 07:41
 */
public class StatisticCollector {
    public static ActorMetrics stats(ActorRef actorRef) {
        ActorMetrics result = new ActorMetrics();
        stats(actorRef, result);
        return result;
    }

    private static void stats(ActorRef actorRef, ActorMetrics statistic) {

        if (actorRef instanceof RepointableActorRef) {
            RepointableActorRef ref = (RepointableActorRef) actorRef;
            Cell cell = ref.lookup();
            statistic.setId(ref.path().toString());

            ActorCell ac = (ActorCell) cell;

            if (ac instanceof RoutedActorCell) {
                RoutedActorCell routedActorCell = (RoutedActorCell) ac;
                Iterator<ActorRef> iterator = routedActorCell.routees().iterator();
                while (iterator.hasNext()) {

                    ActorMetrics childStatistic = statistic.createChild();
                    childStatistic.setIncludeInParent(true);
                    stats(iterator.next(), childStatistic);
                }
            }

            populateStatistic(statistic, ac);

            scala.collection.immutable.Iterable<ActorRef> children = cell.childrenRefs().children();
            Iterator<ActorRef> iterator = children.iterator();

            while (iterator.hasNext()) {
                ActorRef childActorRef = iterator.next();
                if (statistic.containsChild(childActorRef.path().toString())) {
                    continue;
                }

                stats(childActorRef, statistic.createChild());
            }
            return;
        }

        if (actorRef instanceof RoutedActorRef) {
            RoutedActorRef ref = (RoutedActorRef) actorRef;
            statistic.setId(ref.path().toString());
            Cell cell = ref.lookup();
            ActorCell ac = (ActorCell) cell;

            populateStatistic(statistic, ac);
            return;
        }

        if (actorRef instanceof LocalActorRef) {
            LocalActorRef ref = (LocalActorRef) actorRef;
            statistic.setId(ref.path().toString());
            Cell cell = ref.underlying();
            ActorCell ac = (ActorCell) cell;
            populateStatistic(statistic, ac);
            return;
        }
    }

    private static void populateStatistic(ActorMetrics statistic, ActorCell ac) {
        Actor actor = ac.actor();
        if (actor instanceof StatisticActor) {
            StatisticActor statisticActor = (StatisticActor) actor;
            statistic.setProcessedMessages(statisticActor.getProcessedMessages());
            statistic.setProcessedMessageTime(statisticActor.getMessageTime());
        }

        statistic.setQueueSize(ac.mailbox().numberOfMessages());
        statistic.setActive(ac.currentMessage() != null);
    }
}
