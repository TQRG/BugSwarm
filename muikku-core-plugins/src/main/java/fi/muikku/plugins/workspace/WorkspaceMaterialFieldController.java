package fi.muikku.plugins.workspace;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import fi.muikku.plugins.material.MaterialField;
import fi.muikku.plugins.material.model.QueryField;
import fi.muikku.plugins.workspace.dao.WorkspaceMaterialFieldDAO;
import fi.muikku.plugins.workspace.events.WorkspaceMaterialFieldCreateEvent;
import fi.muikku.plugins.workspace.events.WorkspaceMaterialFieldDeleteEvent;
import fi.muikku.plugins.workspace.events.WorkspaceMaterialFieldUpdateEvent;
import fi.muikku.plugins.workspace.fieldio.WorkspaceFieldIOException;
import fi.muikku.plugins.workspace.fieldio.WorkspaceFieldIOHandler;
import fi.muikku.plugins.workspace.model.WorkspaceMaterial;
import fi.muikku.plugins.workspace.model.WorkspaceMaterialField;
import fi.muikku.plugins.workspace.model.WorkspaceMaterialReply;

@Stateless
@Dependent
public class WorkspaceMaterialFieldController {
  
  @Inject
  private Logger logger;
  
  @Inject
  private WorkspaceMaterialFieldDAO workspaceMaterialFieldDAO;

  @Inject
  private Event<WorkspaceMaterialFieldCreateEvent> workspaceMaterialFieldCreateEvent;
  
  @Inject
  @Any
  private Instance<WorkspaceFieldIOHandler> fieldIOHandlers;

  @Inject
  private Event<WorkspaceMaterialFieldUpdateEvent> workspaceMaterialFieldUpdateEvent;
  
  @Inject
  private Event<WorkspaceMaterialFieldDeleteEvent> workspaceMaterialFieldDeleteEvent;

  public WorkspaceMaterialField createWorkspaceMaterialField(WorkspaceMaterial workspaceMaterial, QueryField queryField, String embedId) {
    WorkspaceMaterialField workspaceMaterialField = workspaceMaterialFieldDAO.create(queryField, workspaceMaterial, embedId);
    workspaceMaterialFieldCreateEvent.fire(new WorkspaceMaterialFieldCreateEvent(workspaceMaterialField));
    return workspaceMaterialField;
  }
  
  public WorkspaceMaterialField findWorkspaceMaterialFieldByWorkspaceMaterialAndQueryFieldAndEmbedId(WorkspaceMaterial workspaceMaterial, QueryField queryField, String embedId) {
    return workspaceMaterialFieldDAO.findByWorkspaceMaterialAndQueryFieldAndEmbedId(workspaceMaterial, queryField, embedId); 
  }
  
  public List<WorkspaceMaterialField> listWorkspaceMaterialFieldsByWorkspaceMaterial(WorkspaceMaterial workspaceMaterial){
    return workspaceMaterialFieldDAO.listByWorkspaceMaterial(workspaceMaterial);
  }

  public List<WorkspaceMaterialField> listWorkspaceMaterialFieldsByQueryField(QueryField queryField) {
    return workspaceMaterialFieldDAO.listByQueryField(queryField);
  }
  
  public void updateWorkspaceMaterialField(WorkspaceMaterialField workspaceMaterialField, MaterialField materialField, boolean removeAnswers) {
    // In theory, fields' workspace instances remain the same even when updated (apart
    // from answers that might get removed based on WorkspaceMaterialFieldUpdateEvent)
    //
    // -> fi.muikku.plugins.workspace.WorkspaceMaterialFieldChangeListener
    workspaceMaterialFieldUpdateEvent.fire(new WorkspaceMaterialFieldUpdateEvent(workspaceMaterialField, materialField, removeAnswers));
  }

  public void deleteWorkspaceMaterialField(WorkspaceMaterialField workspaceMaterialField, boolean removeAnswers) {
    workspaceMaterialFieldDeleteEvent.fire(new WorkspaceMaterialFieldDeleteEvent(workspaceMaterialField, removeAnswers));
    workspaceMaterialFieldDAO.delete(workspaceMaterialField);
  }
  
  public String retrieveFieldValue(WorkspaceMaterialField field, WorkspaceMaterialReply reply) throws WorkspaceFieldIOException {
    WorkspaceFieldIOHandler handler = getIOHandler(field.getQueryField().getType());
    if (handler != null) {
      return handler.retrieve(field, reply);
    } else {
      logger.severe(String.format("Could not find io handler for queryfield type: %s", field.getQueryField().getType()));
    }
    
    return null;
  }
  
  public void storeFieldValue(WorkspaceMaterialField field, WorkspaceMaterialReply reply, String value) throws WorkspaceFieldIOException {
    WorkspaceFieldIOHandler handler = getIOHandler(field.getQueryField().getType());
    if (handler != null) {
      handler.store(field, reply, value);
    } else {
      logger.severe(String.format("Could not find io handler for queryfield type: %s", field.getQueryField().getType()));
    }
  }
  
  private WorkspaceFieldIOHandler getIOHandler(String type) {
    for (WorkspaceFieldIOHandler handler : fieldIOHandlers) {
      if (handler.getType().equals(type)) {
        return handler;
      }
    }
    
    return null;
  }

}
