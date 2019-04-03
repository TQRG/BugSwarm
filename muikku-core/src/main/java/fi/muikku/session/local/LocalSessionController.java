package fi.muikku.session.local;

import fi.muikku.model.users.UserEntity;
import fi.muikku.session.SessionController;

public interface LocalSessionController extends SessionController {

  public void representUser(Long userId);

  public void endRepresentation();

  public boolean isRepresenting();

  public UserEntity getRepresentedUser();
}
