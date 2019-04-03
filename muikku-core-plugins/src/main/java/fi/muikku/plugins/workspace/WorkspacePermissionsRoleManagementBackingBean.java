package fi.muikku.plugins.workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.muikku.controller.PermissionController;
import fi.muikku.jsf.NavigationRules;
import fi.muikku.model.security.Permission;
import fi.muikku.model.users.EnvironmentRoleEntity;
import fi.muikku.model.users.RoleEntity;
import fi.muikku.model.users.SystemRoleEntity;
import fi.muikku.model.workspace.WorkspaceEntity;
import fi.muikku.model.workspace.WorkspaceRoleEntity;
import fi.muikku.schooldata.RoleController;
import fi.muikku.schooldata.WorkspaceController;
import fi.muikku.schooldata.entity.Workspace;
import fi.muikku.security.MuikkuPermissions;
import fi.muikku.security.PermissionScope;
import fi.muikku.session.SessionController;
import fi.otavanopisto.security.LoggedIn;

@Named
@Stateful
@RequestScoped
@Join(path = "/workspace/{workspaceUrlName}/rolepermissions", to = "/jsf/workspace/permissions-role.jsf")
@LoggedIn
public class WorkspacePermissionsRoleManagementBackingBean {

  @Parameter
  private String workspaceUrlName;

  @Inject
  private WorkspaceController workspaceController;
  
  @Inject
  @Named
  private WorkspaceBackingBean workspaceBackingBean;

  @Inject
  private PermissionController permissionController;

  @Inject
  private RoleController roleController;

  @Inject
  private SessionController sessionController;
  
  @RequestAction
  public String init() {
    String urlName = getWorkspaceUrlName();

    if (StringUtils.isBlank(urlName)) {
      return NavigationRules.NOT_FOUND;
    }

    WorkspaceEntity workspaceEntity = workspaceController.findWorkspaceEntityByUrlName(urlName);
    if (workspaceEntity == null) {
      return NavigationRules.NOT_FOUND;
    }

    if (!sessionController.hasCoursePermission(MuikkuPermissions.WORKSPACE_MANAGEWORKSPACESETTINGS, workspaceEntity)) {
      return NavigationRules.ACCESS_DENIED;
    }
    
    workspaceEntityId = workspaceEntity.getId();
    workspaceBackingBean.setWorkspaceUrlName(urlName);
    Workspace workspace = workspaceController.findWorkspace(workspaceEntity);
    workspaceName = workspace.getName();

    /**
     * View data
     */

    permissions = permissionController.listPermissionsByScope(PermissionScope.WORKSPACE);
    
    Collections.sort(permissions, new Comparator<Permission>() {
      @Override
      public int compare(Permission o1, Permission o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    
    roleEntities = new ArrayList<RoleEntity>();
    
    List<SystemRoleEntity> systemRoleEntities = roleController.listSystemRoleEntities();
    List<EnvironmentRoleEntity> environmentRoleEnties = roleController.listEnvironmentRoleEntities();
    List<WorkspaceRoleEntity> workspaceRoleEntities = roleController.listWorkspaceRoleEntities();
    
    Collections.sort(environmentRoleEnties, new Comparator<EnvironmentRoleEntity>() {
      @Override
      public int compare(EnvironmentRoleEntity o1, EnvironmentRoleEntity o2) {
        return o1.getArchetype().compareTo(o2.getArchetype());
      }
    });

    Collections.sort(workspaceRoleEntities, new Comparator<WorkspaceRoleEntity>() {
      @Override
      public int compare(WorkspaceRoleEntity o1, WorkspaceRoleEntity o2) {
        return o1.getArchetype().compareTo(o2.getArchetype());
      }
    });

    for (SystemRoleEntity systemRoleEntity : systemRoleEntities) {
      roleEntities.add(systemRoleEntity);
    }
    
    for (EnvironmentRoleEntity environmentRoleEntity : environmentRoleEnties) {
      roleEntities.add(environmentRoleEntity);
    }
    
    for (WorkspaceRoleEntity workspaceRoleEntity : workspaceRoleEntities) {
      roleEntities.add(workspaceRoleEntity);
    }
    
    return null;
  }

  public String getWorkspaceUrlName() {
    return workspaceUrlName;
  }

  public void setWorkspaceUrlName(String workspaceUrlName) {
    this.workspaceUrlName = workspaceUrlName;
  }

  public String getWorkspaceName() {
    return workspaceName;
  }

  public Long getWorkspaceEntityId() {
    return workspaceEntityId;
  }

  public List<Permission> getPermissions() {
    return permissions;
  }
  
  public List<RoleEntity> getRoleEntities() {
    return roleEntities;
  }
  
  public boolean hasWorkspaceRolePermission(RoleEntity role, Permission permission) {
    WorkspaceEntity workspaceEntity = workspaceController.findWorkspaceEntityById(getWorkspaceEntityId());

    return permissionController.hasWorkspacePermission(workspaceEntity, role, permission);
  }
  
  private String workspaceName;
  private Long workspaceEntityId;
  private List<RoleEntity> roleEntities;
  private List<Permission> permissions;
}