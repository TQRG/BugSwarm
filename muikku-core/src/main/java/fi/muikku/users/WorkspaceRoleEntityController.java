package fi.muikku.users;

import java.util.logging.Logger;

import javax.inject.Inject;

import fi.muikku.dao.base.SchoolDataSourceDAO;
import fi.muikku.dao.users.RoleSchoolDataIdentifierDAO;
import fi.muikku.dao.workspace.WorkspaceRoleEntityDAO;
import fi.muikku.model.base.SchoolDataSource;
import fi.muikku.model.users.RoleEntity;
import fi.muikku.model.users.RoleSchoolDataIdentifier;
import fi.muikku.model.users.UserRoleType;
import fi.muikku.model.workspace.WorkspaceRoleArchetype;
import fi.muikku.model.workspace.WorkspaceRoleEntity;

public class WorkspaceRoleEntityController {
  
  @Inject
  private Logger logger;
  
  @Inject
  private WorkspaceRoleEntityDAO workspaceRoleEntityDAO;

  @Inject
  private RoleSchoolDataIdentifierDAO roleSchoolDataIdentifierDAO;
  
  @Inject
  private SchoolDataSourceDAO schoolDataSourceDAO;
  
  public WorkspaceRoleEntity createWorkspaceRoleEntity(String dataSource, String identifier, WorkspaceRoleArchetype archetype, String name) {
    SchoolDataSource schoolDataSource = schoolDataSourceDAO.findByIdentifier(dataSource);
    if (schoolDataSource == null) {
      logger.severe("Could not find datasource " + dataSource);
      return null;
    }
    
    WorkspaceRoleEntity workspaceRoleEntity = workspaceRoleEntityDAO.create(archetype, name);
    roleSchoolDataIdentifierDAO.create(schoolDataSource, identifier, workspaceRoleEntity);
    
    return workspaceRoleEntity;
  }
  
  public WorkspaceRoleEntity findWorkspaceRoleEntityByDataSourceAndIdentifier(String dataSource, String identifier) {
    SchoolDataSource schoolDataSource = schoolDataSourceDAO.findByIdentifier(dataSource);
    if (schoolDataSource == null) {
      logger.severe("Could not find datasource " + dataSource);
      return null;
    }
    
    RoleSchoolDataIdentifier roleIdentifier = roleSchoolDataIdentifierDAO.findByDataSourceAndIdentifier(schoolDataSource, identifier);
    if (roleIdentifier != null) {
      RoleEntity roleEntity = roleIdentifier.getRoleEntity();
      if (roleEntity != null && roleEntity.getType() == UserRoleType.WORKSPACE) {
        return (WorkspaceRoleEntity) roleEntity;
      }
    }
    
    return null;
  }

}
