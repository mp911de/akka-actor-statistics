package biz.paluch.akka;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 13.03.14 21:03
 */
public class ActorMetrics
{
    private List<ActorMetrics> children = new ArrayList<>();

    private String id;
    private int queueSize;
    private boolean active = false;
    private boolean includeInParent = false;
    private long processedMessages = 0;
    private long processedMessageTime = 0;

    public List<ActorMetrics> getChildren() {
        return children;
    }

    public void setChildren(List<ActorMetrics> children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isIncludeInParent() {
        return includeInParent;
    }

    public void setIncludeInParent(boolean includeInParent) {
        this.includeInParent = includeInParent;
    }

    public long getProcessedMessages() {
        return processedMessages;
    }

    public void setProcessedMessages(long processedMessages) {
        this.processedMessages = processedMessages;
    }

    public long getProcessedMessageTime() {
        return processedMessageTime;
    }

    public void setProcessedMessageTime(long processedMessageTime) {
        this.processedMessageTime = processedMessageTime;
    }

    public ActorMetrics createChild() {
        ActorMetrics statistic = new ActorMetrics();
        children.add(statistic);
        return statistic;
    }

    public boolean containsChild(String id) {

        for (ActorMetrics child : children) {
            if (child.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
