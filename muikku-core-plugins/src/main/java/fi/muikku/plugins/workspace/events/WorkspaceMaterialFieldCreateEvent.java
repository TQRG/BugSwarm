package fi.muikku.plugins.workspace.events;

import fi.muikku.plugins.workspace.model.WorkspaceMaterialField;

public class WorkspaceMaterialFieldCreateEvent extends WorkspaceMaterialFieldEvent {

  public WorkspaceMaterialFieldCreateEvent(WorkspaceMaterialField workspaceMaterialField) {
    super(workspaceMaterialField);
  }

}
