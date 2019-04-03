package fi.muikku.dao.users;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.muikku.dao.CoreDAO;
import fi.muikku.model.users.RoleEntity;
import fi.muikku.model.users.RoleEntity_;

public class RoleEntityDAO extends CoreDAO<RoleEntity> {

	private static final long serialVersionUID = -4915561488370208448L;

  public RoleEntity findByName(String roleName) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RoleEntity> criteria = criteriaBuilder.createQuery(RoleEntity.class);
    Root<RoleEntity> root = criteria.from(RoleEntity.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(RoleEntity_.name), roleName)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

}
