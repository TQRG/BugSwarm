package cn.nukkit.scheduler;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ServerScheduler {
    public static int WORKERS = 4;

    private final AsyncPool asyncPool;

    private final PriorityQueue<TaskHandler> queue;
    private final Map<Integer, TaskHandler> tasks;

    private int currentTaskId;
    private int currentTick;

    public ServerScheduler() {
        this.queue = new PriorityQueue<>(11, (left, right) -> left.getNextRun() - right.getNextRun());
        this.tasks = new HashMap<>();
        this.asyncPool = new AsyncPool(Server.getInstance(), WORKERS);
    }

    public TaskHandler scheduleTask(Task task) {
        return this.addTask(task, -1, -1);
    }

    public TaskHandler scheduleTask(Runnable task) {
        return this.addTask(Task.of(task), -1, -1);
    }

    public void scheduleAsyncTask(AsyncTask task) {
        task.setTaskId(this.nextId());
        this.asyncPool.submitTask(task);
    }

    public void scheduleAsyncTaskToWorker(AsyncTask task, int worker) {
        task.setTaskId(this.nextId());
        this.asyncPool.submitTaskToWorker(task, worker);
    }

    public int getAsyncTaskPoolSize() {
        return this.asyncPool.getSize();
    }

    public void increaseAsyncTaskPoolSize(int newSize) {
        this.asyncPool.increaseSize(newSize);
    }

    public TaskHandler scheduleDelayedTask(Task task, int delay) {
        return this.addTask(task, delay, -1);
    }

    public TaskHandler scheduleDelayedTask(Runnable task, int delay) {
        return this.addTask(Task.of(task), delay, -1);
    }

    public TaskHandler scheduleRepeatingTask(Runnable task, int period) {
        return this.addTask(Task.of(task), -1, period);
    }

    public TaskHandler scheduleRepeatingTask(Task task, int period) {
        return this.addTask(task, -1, period);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Task task, int delay, int period) {
        return this.addTask(task, delay, period);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Runnable task, int delay, int period) {
        return this.addTask(Task.of(task), delay, period);
    }

    public void cancelTask(int taskId) {
        if (this.tasks.containsKey(taskId)) {
            this.tasks.get(taskId).cancel();
            this.tasks.remove(taskId);
        }
    }

    public void cancelTask(Plugin plugin) {
        for (Map.Entry<Integer, TaskHandler> entry : this.tasks.entrySet()) {
            int taskId = entry.getKey();
            TaskHandler task = entry.getValue();
            Task ptask = task.getTask();
            if (ptask instanceof PluginTask && ((PluginTask) ptask).getOwner().equals(plugin)) {
                task.cancel();
                this.tasks.remove(taskId);
            }
        }
    }

    public void cancelAllTasks() {
        for (Map.Entry<Integer, TaskHandler> entry : this.tasks.entrySet()) {
            entry.getValue().cancel();
        }
        this.tasks.clear();
        this.queue.clear();
        this.currentTaskId = 0;
    }

    public boolean isQueued(int taskId) {
        return this.tasks.containsKey(taskId);
    }

    private TaskHandler addTask(Task task, int delay, int period) {
        if (task instanceof PluginTask) {
            if (((PluginTask) task).getOwner() == null) {
                throw new PluginException("Invalid owner of PluginTask " + task.getClass().getName());
            } else if (!((PluginTask) task).getOwner().isEnabled()) {
                throw new PluginException("Plugin '" + ((PluginTask) task).getOwner().getName() + "' attempted to register a task while disabled");
            }
        }
        //todo deprecate CallBackTask??
        if (delay <= 0) {
            delay = -1;
        }

        if (period <= -1) {
            period = -1;
        } else if (period < 1) {
            period = 1;
        }

        return this.handle(new TaskHandler(
                task.getClass().getName(), task, this.nextId(), delay, period
        ));
    }

    private TaskHandler handle(TaskHandler handler) {
        int nextRun;
        if (handler.isDelayed()) {
            nextRun = this.currentTick + handler.getDelay();
        } else {
            nextRun = this.currentTick;
        }
        handler.setNextRun(nextRun);
        this.tasks.put(handler.getTaskId(), handler);
        this.queue.add(handler);
        return handler;
    }

    public void mainThreadHeartbeat(int currentTick) {
        this.currentTick = currentTick;
        while (this.isReady(this.currentTick)) {
            TaskHandler task = this.queue.poll();
            if (task.isCancelled()) {
                this.tasks.remove(task.getTaskId());
                continue;
            } else {
                try {
                    task.run(this.currentTick);
                } catch (Exception e) {
                    Server.getInstance().getLogger().critical("Could not execute task " + task.getTaskName() + ": " + e.getMessage());
                    Server.getInstance().getLogger().logException(e);
                }
            }
            if (task.isRepeating()) {
                task.setNextRun(this.currentTick + task.getPeriod());
                this.queue.add(task);
            } else {
                task.remove();
                this.tasks.remove(task.getTaskId());
            }
        }
        this.asyncPool.collectTasks();
    }

    public int getQueueSize() {
        return queue.size();
    }

    private boolean isReady(int currentTick) {
        return this.queue.peek() != null && this.queue.peek().getNextRun() <= currentTick;
    }

    private int nextId() {
        return ++currentTaskId;
    }

}
