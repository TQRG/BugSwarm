package fi.muikku.plugins.workspace.events;

import fi.muikku.plugins.workspace.model.WorkspaceRootFolder;

public class WorkspaceRootFolderUpdateEvent extends WorkspaceRootFolderEvent {

  public WorkspaceRootFolderUpdateEvent(WorkspaceRootFolder workspaceRootFolder) {
    super(workspaceRootFolder);
  }
  
}
