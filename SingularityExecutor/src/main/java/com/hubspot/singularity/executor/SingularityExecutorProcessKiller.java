package com.hubspot.singularity.executor;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hubspot.singularity.executor.config.SingularityExecutorConfiguration;
import com.hubspot.singularity.executor.task.SingularityExecutorTaskProcessCallable;

@Singleton
public class SingularityExecutorProcessKiller {

  private final SingularityExecutorConfiguration configuration;
  private final ScheduledExecutorService scheduledExecutorService;
  private final Map<String, ScheduledFuture<?>> destroyFutures;

  @Inject
  public SingularityExecutorProcessKiller(SingularityExecutorConfiguration configuration) {
    this.configuration = configuration;

    this.destroyFutures = Maps.newConcurrentMap();
    this.scheduledExecutorService = Executors.newScheduledThreadPool(configuration.getKillThreads(), new ThreadFactoryBuilder().setNameFormat("SingularityExecutorKillThread-%d").build());
  }

  public void submitKillRequest(final SingularityExecutorTaskProcessCallable processCallable) {
    processCallable.markKilled();  // makes it so that the task can not start
    processCallable.signalTermToProcessIfActive();

    destroyFutures.put(processCallable.getTask().getTaskId(), scheduledExecutorService.schedule(new Runnable() {
      @Override
      public void run() {
        processCallable.getTask().markDestroyedAfterWaiting();
        processCallable.signalKillToProcessIfActive();
      }
    }, processCallable.getTask().getExecutorData().getSigKillProcessesAfterMillis().or(configuration.getHardKillAfterMillis()), TimeUnit.MILLISECONDS));
  }

  public void cancelDestroyFuture(String taskId) {
    ScheduledFuture<?> future = destroyFutures.remove(taskId);

    if (future != null) {
      future.cancel(false);
    }
  }

  public ExecutorService getExecutorService() {
    return scheduledExecutorService;
  }

}
