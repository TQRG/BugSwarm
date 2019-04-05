package sic.repository.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import sic.modelo.ConfiguracionDelSistema;
import sic.modelo.Empresa;
import sic.repository.IConfiguracionDelSistemaRepository;

@Repository
public class ConfiguracionDelSistemaRepositoryJPAImpl implements IConfiguracionDelSistemaRepository {

    @PersistenceContext
    private EntityManager em;
    
    private static final Logger log = Logger.getLogger(ConfiguracionDelSistemaRepositoryJPAImpl.class.getPackage().getName());

    @Override
    public ConfiguracionDelSistema getConfiguracionDelSistemaPorId(long id_ConfiguracionDelSistema) {
        TypedQuery<ConfiguracionDelSistema> typedQuery = em.createNamedQuery("ConfiguracionDelSistema.buscarPorId", ConfiguracionDelSistema.class);
        typedQuery.setParameter("id", id_ConfiguracionDelSistema);
        List<ConfiguracionDelSistema> configuraciones = typedQuery.getResultList();
        if (configuraciones.isEmpty()) {
            return null;
        } else {
            return configuraciones.get(0);
        }
    }

    @Override
    public ConfiguracionDelSistema getConfiguracionDelSistemaPorEmpresa(Empresa empresa) {
        TypedQuery<ConfiguracionDelSistema> typedQuery = em.createNamedQuery("ConfiguracionDelSistema.buscarPorEmpresa", ConfiguracionDelSistema.class);
        typedQuery.setParameter("empresa", empresa);
        List<ConfiguracionDelSistema> configuraciones = typedQuery.getResultList();
        if (configuraciones.isEmpty()) {
            return null;
        } else {
            return configuraciones.get(0);
        }
    }

    @Override
    public void guardar(ConfiguracionDelSistema cds) {
        em.persist(em.merge(cds));
    }

    @Override
    public void actualizar(ConfiguracionDelSistema cds) {
        em.merge(cds);
    }
}