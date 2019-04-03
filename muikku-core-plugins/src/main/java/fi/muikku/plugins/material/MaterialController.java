package fi.muikku.plugins.material;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import fi.muikku.plugins.material.dao.MaterialDAO;
import fi.muikku.plugins.material.dao.MaterialMetaDAO;
import fi.muikku.plugins.material.dao.MaterialMetaKeyDAO;
import fi.muikku.plugins.material.model.Material;
import fi.muikku.plugins.material.model.MaterialMeta;
import fi.muikku.plugins.material.model.MaterialMetaKey;
import fi.muikku.plugins.material.operations.MaterialCloneOperation;

@Dependent
@Stateless
public class MaterialController {
  
  @Inject
  @Any
  private Instance<MaterialCloneOperation<?>> cloneOperations;
  
	@Inject
	private MaterialDAO materialDAO;
	
  @Inject
	private MaterialMetaDAO materialMetaDAO;
  
  @Inject
  private MaterialMetaKeyDAO materialMetaKeyDAO;
	
	public Material findMaterialById(Long id) {
		return materialDAO.findById(id);
	}
	
  public <T> T cloneMaterial(T material) {
    Iterator<MaterialCloneOperation<?>> operations = cloneOperations.iterator();

    while (operations.hasNext()) {
      @SuppressWarnings("unchecked")
      MaterialCloneOperation<T> operation = (MaterialCloneOperation<T>) operations.next();
      @SuppressWarnings("rawtypes")
      Class<? extends MaterialCloneOperation> operationClass = operation.getClass();

      for (Type genericInterface : operationClass.getGenericInterfaces()) {
        if (genericInterface instanceof ParameterizedType) {
          ParameterizedType parameterizedGenericInterface = (ParameterizedType) genericInterface;
      
          Type type = parameterizedGenericInterface.getActualTypeArguments()[0];
          Class<?> operationMaterialType = (Class<?>) type;

          if (material.getClass().isAssignableFrom(operationMaterialType)) {
            return operation.clone(material);
          }
        }
      }
    }
    
    return null;
	}

  public List<MaterialMetaKey> listMaterialMetaKeys() {
    return materialMetaKeyDAO.listAll();
  }
  
  public MaterialMeta createMaterialMeta(Material material, MaterialMetaKey key, String value) {
    return materialMetaDAO.create(material, key, value);
  }
  
  public MaterialMeta findMaterialMeta(Material material, MaterialMetaKey key) {
    return materialMetaDAO.findByMaterialAndKey(material, key);
  }

  public List<MaterialMeta> listMaterialMetas(Material material) {
    return materialMetaDAO.listByMaterial(material);
  }

  public MaterialMeta updateMaterialMeta(MaterialMeta materialMeta, String value) {
    return materialMetaDAO.updateValue(materialMeta, value);
  }
  
  public MaterialMetaKey findMaterialMetaKey(String name) {
    return materialMetaKeyDAO.findByName(name);
  }

}
