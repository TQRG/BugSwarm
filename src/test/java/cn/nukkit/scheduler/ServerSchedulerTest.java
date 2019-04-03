package cn.nukkit.scheduler;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 15-12-14.
 */
public class ServerSchedulerTest {

    private final ServerScheduler scheduler = new ServerScheduler();

    @Test
    public void testScheduleDelayedTask() {
        scheduler.scheduleDelayedTask(() -> System.out.println("Yes!"), 1);
        Assert.assertTrue(scheduler.getQueueSize() == 1);
        System.out.println("Heart beat 0.");
        scheduler.mainThreadHeartbeat(0);
        Assert.assertTrue(scheduler.getQueueSize() == 1);
        System.out.println("Heart beat 1.");
        scheduler.mainThreadHeartbeat(1);
        Assert.assertTrue(scheduler.getQueueSize() == 0);
        System.out.println("Heart beat 2.");
        scheduler.mainThreadHeartbeat(2);
        Assert.assertTrue(scheduler.getQueueSize() == 0);
    }

    @Test
    public void testScheduleMultiTask() {
        AtomicInteger a = new AtomicInteger();

        scheduler.scheduleDelayedTask(() -> {
            a.set(1);
        }, 1);
        scheduler.scheduleDelayedTask(() -> {
            a.set(2);
        }, 2);

        scheduler.mainThreadHeartbeat(1);
        Assert.assertTrue(a.get() == 1);

        scheduler.mainThreadHeartbeat(2);
        Assert.assertTrue(a.get() == 2);
    }

    @Test
    public void testScheduleDelayedRepeatingTaskAndCancel() {
        TaskHandler task = scheduler.scheduleDelayedRepeatingTask(() -> System.out.println("Yes!"), 1, 1);
        System.out.println("Heart beat 0.");
        scheduler.mainThreadHeartbeat(0);
        Assert.assertTrue(scheduler.getQueueSize() == 1);
        System.out.println("Heart beat 1.");
        scheduler.mainThreadHeartbeat(1);
        Assert.assertTrue(scheduler.getQueueSize() == 1);
        System.out.println("Heart beat 2.");
        scheduler.mainThreadHeartbeat(2);
        Assert.assertTrue(scheduler.getQueueSize() == 1);
        scheduler.cancelTask(task.getTaskId());
        System.out.println("Heart beat 3.");
        scheduler.mainThreadHeartbeat(3);
        Assert.assertTrue(scheduler.getQueueSize() == 0);
    }

}
