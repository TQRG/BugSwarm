package fi.muikku.plugins.material.coops;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import fi.muikku.events.ContextInitializedEvent;

public class CoOpsServletContextListener {
  
  @Inject
  private CoOpsSessionController coOpsSessionController;

  public void onContextInitialized(@Observes ContextInitializedEvent event) {
    coOpsSessionController.deleteAllSessions();
  }
  
}
