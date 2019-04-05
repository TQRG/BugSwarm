package io.nflow.config.guice;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ThreadFactory;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import io.nflow.config.guice.DemoWorkflow.State;
import io.nflow.engine.internal.config.NFlow;
import io.nflow.engine.internal.config.WorkflowLifecycle;
import io.nflow.engine.internal.executor.WorkflowInstanceExecutor;
import io.nflow.engine.service.WorkflowDefinitionService;
import io.nflow.engine.service.WorkflowInstanceService;
import io.nflow.engine.workflow.instance.WorkflowInstance;

public class EngineModuleTest {

  @Test
  public void testH2WithGuiceConfiguration() {
    Properties props = getEngineConfigurationProperties();
    Injector injector = Guice.createInjector(new EngineModule(props, null));

    WorkflowInstanceExecutor executor = injector.getInstance(WorkflowInstanceExecutor.class);
    assertThat(executor.getQueueRemainingCapacity(), is(200));

    AbstractResource nonSpringWorkflowsListing = injector.getInstance(Key.get(AbstractResource.class, NFlow.class));
    assertThat(nonSpringWorkflowsListing, nullValue());

    ThreadFactory factory = injector.getInstance(Key.get(ThreadFactory.class, NFlow.class));
    assertThat(factory, instanceOf(CustomizableThreadFactory.class));
    assertThat(((CustomizableThreadFactory) factory).getThreadNamePrefix(), is("nflow-executor-"));
    assertThat(((CustomizableThreadFactory) factory).getThreadGroup().getName(), is("nflow"));

    ObjectMapper mapper = injector.getInstance(Key.get(ObjectMapper.class, NFlow.class));
    assertThat(mapper.canSerialize(DateTime.class), is(true));
    assertThat(mapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion(),
        is(JsonInclude.Include.NON_EMPTY));

    WorkflowLifecycle lifecycle = injector.getInstance(WorkflowLifecycle.class);
    if (lifecycle.isAutoStartup()) {
      lifecycle.start();
    }

    assertThat(lifecycle.getPhase(), is(Integer.MAX_VALUE));
    assertThat(lifecycle.isAutoStartup(), is(true));

    WorkflowDefinitionService workflowDefinitionService = injector.getInstance(WorkflowDefinitionService.class);
    workflowDefinitionService.addWorkflowDefinition(new DemoWorkflow());

    WorkflowInstanceService workflowInstanceService = injector.getInstance(WorkflowInstanceService.class);

    WorkflowInstance instance = new WorkflowInstance.Builder().setType("demo").setState("begin").setNextActivation(DateTime.now())
        .build();
    int id = workflowInstanceService.insertWorkflowInstance(instance);

    while (!instance.state.equals(State.done.name())) {
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      instance = workflowInstanceService.getWorkflowInstance(id);
    }
    Runnable callback = mock(Runnable.class);
    lifecycle.stop(callback);
    verify(callback).run();
  }

  private Properties getEngineConfigurationProperties() {
    ClassPathResource engineProperties = new ClassPathResource("nflow-engine.properties");
    Properties p = new Properties();
    try {
      p.load(engineProperties.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    p.setProperty("nflow.db.type", "h2");
    p.setProperty("nflow.executor.thread.count", "100");
    p.setProperty("nflow.dispatcher.await.termination.seconds", "60");
    p.setProperty("nflow.dispatcher.executor.thread.keepalive.seconds", "0");
    return p;
  }
}
