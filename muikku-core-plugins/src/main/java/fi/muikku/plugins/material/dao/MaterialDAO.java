package fi.muikku.plugins.material.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.muikku.plugins.CorePluginsDAO;
import fi.muikku.plugins.material.model.Material;
import fi.muikku.plugins.material.model.Material_;

public class MaterialDAO extends CorePluginsDAO<Material> {

	private static final long serialVersionUID = 148925841493479490L;

	public List<Material> listByOriginMaterial(Material material) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<Material> root = criteria.from(Material.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(Material_.originMaterial), material)
    );

    return entityManager.createQuery(criteria).getResultList();
	}
	
}