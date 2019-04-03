package fi.muikku.plugins.internalauth.dao;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


import fi.muikku.model.users.UserEntity;
import fi.muikku.plugins.CorePluginsDAO;
import fi.muikku.plugins.internalauth.model.InternalAuth;
import fi.muikku.plugins.internalauth.model.InternalAuth_;


public class InternalAuthDAO extends CorePluginsDAO<InternalAuth> {

  private static final long serialVersionUID = 5095222172173498678L;

  public InternalAuth create(Long userEntityId, String password) {
    InternalAuth internalAuth = new InternalAuth();
    internalAuth.setPassword(password);
    internalAuth.setUserEntityId(userEntityId);
    return persist(internalAuth);
  }

  public InternalAuth findByUserId(Long userEntityId) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<InternalAuth> criteria = criteriaBuilder.createQuery(InternalAuth.class);
    Root<InternalAuth> root = criteria.from(InternalAuth.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(InternalAuth_.userEntityId), userEntityId));

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public InternalAuth findByUserIdAndPassword(Long userEntityId, String password) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<InternalAuth> criteria = criteriaBuilder.createQuery(InternalAuth.class);
    Root<InternalAuth> root = criteria.from(InternalAuth.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.and(criteriaBuilder.equal(root.get(InternalAuth_.userEntityId), userEntityId),
        criteriaBuilder.equal(root.get(InternalAuth_.password), password)));

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public void updatePassword(UserEntity user, String passwordHash, String newPasswordHash) {
    InternalAuth internalAuth = findByUserIdAndPassword(user.getId(), passwordHash);
    internalAuth.setPassword(newPasswordHash);
    persist(internalAuth);
  }
}
