package fi.muikku.dao.plugins;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.muikku.dao.CoreDAO;
import fi.muikku.model.plugins.PluginSettingKey;
import fi.muikku.model.plugins.PluginSettingKey_;

public class PluginSettingKeyDAO extends CoreDAO<PluginSettingKey> {

	private static final long serialVersionUID = -5118161931799359500L;

	public PluginSettingKey create(String plugin, String name) {
		PluginSettingKey pluginSettingKey = new PluginSettingKey();
		pluginSettingKey.setName(name);
		pluginSettingKey.setPlugin(plugin);
		
		getEntityManager().persist(pluginSettingKey);
		
		return pluginSettingKey;
	}
	
	public PluginSettingKey findByPluginAndName(String plugin, String name) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PluginSettingKey> criteria = criteriaBuilder.createQuery(PluginSettingKey.class);
    Root<PluginSettingKey> root = criteria.from(PluginSettingKey.class);
    criteria.select(root);
    criteria.where(
    	criteriaBuilder.and(
      		criteriaBuilder.equal(root.get(PluginSettingKey_.name), name),
      		criteriaBuilder.equal(root.get(PluginSettingKey_.plugin), plugin)
    	)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
	
}
