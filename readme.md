Akka Mailbox Size
=============================
This tiny code snippets pulls mailbox sizes from Akka Actors. In addition to that, you can implement an own interface `StatisticActor` in order to capture
processed message time/processed message count. This is handy in order to perform monitoring/tuning of your Akka system without the need
to install a huge console setup with MongoDB etc.


How to use
------------------
The statistic collector uses a simple, hierarchical data model.
Following values are captured:

  * id (Actor name)
  * queueSize (current messages in mailbox)
  * active (indicator whether the actor processes a message right now)
  * includeInParent (indicator whether to include the data in the parent statistics, child actor = false, routee actor = true)
  * processedMessages (number of all finished, processed messages)
  * processedMessageTime (cummulated time for message processing)
  * children (child actor metrics)

  Please note, as soon as the actors and its children are restarted, the statistics are reset.

  **Example**

    ActorRef ref = system.actorOf(Props.create(TestActor.class).withRouter(new SmallestMailboxRouter(50)), "TestActor");
    ActorMetrics stats = StatisticCollector.stats(ref);

