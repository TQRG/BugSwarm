package fi.muikku.plugins.material;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import fi.muikku.plugins.material.dao.QueryFileFieldDAO;
import fi.muikku.plugins.material.events.QueryFieldDeleteEvent;
import fi.muikku.plugins.material.model.Material;
import fi.muikku.plugins.material.model.QueryFileField;

@Stateless
@Dependent
public class QueryFileFieldController {

  @Inject
  private QueryFileFieldDAO queryFileFieldDAO;
  
  @Inject
  private Event<QueryFieldDeleteEvent> queryFieldDeleteEvent;

  public QueryFileField createQueryFileField(Material material, String name) {
    return queryFileFieldDAO.create(material, name);
  }

  public QueryFileField findQueryFileFieldbyId(Long id) {
    return queryFileFieldDAO.findById(id);
  }

  public QueryFileField findQueryFileFieldByMaterialAndName(Material material, String name) {
    return queryFileFieldDAO.findByMaterialAndName(material, name);
  }

  public void deleteQueryFileField(QueryFileField queryField, boolean removeAnswers) {
    queryFieldDeleteEvent.fire(new QueryFieldDeleteEvent(queryField, removeAnswers));
    queryFileFieldDAO.delete(queryField);
  }

}
