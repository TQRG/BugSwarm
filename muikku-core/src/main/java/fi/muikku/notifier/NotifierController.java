package fi.muikku.notifier;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import fi.muikku.dao.notifier.NotifierActionEntityDAO;
import fi.muikku.dao.notifier.NotifierMethodEntityDAO;
import fi.muikku.dao.notifier.NotifierUserActionDAO;
import fi.muikku.dao.users.UserEntityDAO;
import fi.muikku.model.notifier.NotifierActionEntity;
import fi.muikku.model.notifier.NotifierMethodEntity;
import fi.muikku.model.notifier.NotifierUserAction;
import fi.muikku.model.notifier.NotifierUserActionAllowance;
import fi.muikku.model.users.UserEntity;
import fi.muikku.session.SessionController;
import fi.muikku.users.UserController;

@Dependent
@Stateful
public class NotifierController {
  @Inject
  private UserEntityDAO userDAO;

  @Inject
  private SessionController sessionController;

  @Inject
  private UserController userController;
  
  @Inject
  private NotifierUserActionDAO notifierUserActionDAO;
  
  @Inject
  private NotifierMethodEntityDAO notifierMethodEntityDAO;

  @Inject
  private NotifierActionEntityDAO notifierActionEntityDAO;
  
  @Inject
  @Any
  private Instance<NotifierMethod> notifierMethods;
  
  @Inject
  @Any
  private Instance<NotifierAction> notifierActions;
  
  public void sendNotification(NotifierAction action, UserEntity sender, List<UserEntity> recipients) {
    for (UserEntity recipient : recipients) {
      sendNotification(action, sender, recipient, null);
    }
  }

  public void sendNotification(NotifierAction action, UserEntity sender, List<UserEntity> recipients, Map<String, Object> params) {
    for (UserEntity recipient : recipients) {
      sendNotification(action, sender, recipient, params);
    }
  }

  public void sendNotification(NotifierAction action, UserEntity sender, UserEntity recipient, Map<String, Object> params) {
    NotifierActionEntity actionEntity = findActionEntityByName(action.getName());
    
    NotifierContext context = new NotifierContext();
    context.setSender(sender);
    context.setRecipient(recipient);
    context.setParameters(params);
    
    for (NotifierMethod strategy : notifierMethods) {
      NotifierMethodEntity method = findMethodEntityByName(strategy.getName());
      
      if (allowsMessages(recipient, actionEntity, method)) {
        strategy.sendNotification(action, context);
      }
    }
  }
  
  public void setUserActionAllowance(NotifierActionEntity action, NotifierMethodEntity method, UserEntity user, NotifierUserActionAllowance allowance) {
    NotifierUserAction notifierUserAction = notifierUserActionDAO.findByActionAndMethodAndRecipient(action, method, user);
    if (notifierUserAction != null)
      notifierUserActionDAO.updateAllowance(notifierUserAction, allowance);
    else
      notifierUserActionDAO.create(action, method, user, allowance);
  }
  
  public boolean allowsMessages(UserEntity user, NotifierAction action, NotifierMethod method) {
    NotifierActionEntity actionEntity = findActionEntityByName(action.getName());
    NotifierMethodEntity methodEntity = findMethodEntityByName(method.getName());
    
    return allowsMessages(user, actionEntity, methodEntity);
  }

  public boolean allowsMessages(UserEntity user, NotifierActionEntity action, NotifierMethodEntity method) {
    NotifierUserAction notifierUserAction = findUserAction(action, method, user);
  
    // Default is true (allowed)
    
    if (notifierUserAction != null)
      return NotifierUserActionAllowance.ALLOW.equals(notifierUserAction.getAllowance());
    else
      return true;
  }
  
  public NotifierUserAction findUserAction(NotifierActionEntity action, NotifierMethodEntity method, UserEntity user) {
    return notifierUserActionDAO.findByActionAndMethodAndRecipient(action, method, user);
  }
  
  public NotifierMethodEntity findMethodEntityByName(String name) {
    return notifierMethodEntityDAO.findByName(name);
  }

  public NotifierActionEntity findActionEntityByName(String name) {
    return notifierActionEntityDAO.findByName(name);
  }

  public void processActionsAndMethods() {
    for (NotifierAction action : notifierActions) {
      if (notifierActionEntityDAO.findByName(action.getName()) == null)
        notifierActionEntityDAO.create(action.getName());
    }
    
    for (NotifierMethod method : notifierMethods) {
      if (notifierMethodEntityDAO.findByName(method.getName()) == null)
        notifierMethodEntityDAO.create(method.getName());
    }
  }

}
