package fi.muikku.plugins.search;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import fi.muikku.model.workspace.WorkspaceEntity;
import fi.muikku.schooldata.SchoolDataBridgeSessionController;
import fi.muikku.schooldata.WorkspaceController;
import fi.muikku.schooldata.WorkspaceEntityController;
import fi.muikku.schooldata.entity.Workspace;
import fi.muikku.search.SearchIndexer;

public class WorkspaceIndexer {
  
  @Inject
  private Logger logger;

  @Inject
  private SchoolDataBridgeSessionController schoolDataBridgeSessionController;

  @Inject
  private WorkspaceController workspaceController;
  
  @Inject
  private WorkspaceEntityController workspaceEntityController;

  @Inject
  private SearchIndexer indexer;

  public void indexWorkspace(String dataSource, String indentifier) {
    schoolDataBridgeSessionController.startSystemSession();
    try {
      WorkspaceEntity workspaceEntity = workspaceEntityController.findWorkspaceByDataSourceAndIdentifier(dataSource, indentifier);
      if (workspaceEntity != null) {
        Workspace workspace = workspaceController.findWorkspace(workspaceEntity);
        if (workspace != null) {
          indexWorkspace(workspace, workspaceEntity);
        }
      } else {
        logger.warning(String.format("could not index workspace because workspace entity #%s/%s could not be found", indentifier, dataSource));
      }
    } finally {
      schoolDataBridgeSessionController.endSystemSession();
    }
  }
  
  public void indexWorkspace(WorkspaceEntity workspaceEntity) {
    schoolDataBridgeSessionController.startSystemSession();
    try {
      Workspace workspace = workspaceController.findWorkspace(workspaceEntity);
      if (workspace != null) {
        indexWorkspace(workspace, workspaceEntity);
      }
    } finally {
      schoolDataBridgeSessionController.endSystemSession();
    }
  }
  
  private void indexWorkspace(Workspace workspace, WorkspaceEntity workspaceEntity) {
    try {
      Map<String, Object> extra = new HashMap<>();
      extra.put("published", workspaceEntity.getPublished());
      indexer.index(Workspace.class.getSimpleName(), workspace, extra);
    } catch (Exception e) {
      logger.warning(String.format("could not index workspace #%s/%s", workspace.getIdentifier(), workspace.getSchoolDataSource()));
    }
  }
  
}
