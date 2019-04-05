package sic.service.impl;

import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.ConfiguracionDelSistema;
import sic.modelo.Empresa;
import sic.service.IConfiguracionDelSistemaService;
import sic.repository.ConfiguracionDelSistemaRepository;

@Service
public class ConfiguracionDelSistemaServiceImpl implements IConfiguracionDelSistemaService {

    private final ConfiguracionDelSistemaRepository configuracionRepository; 
    private static final Logger LOGGER = Logger.getLogger(ConfiguracionDelSistemaServiceImpl.class.getPackage().getName());

    @Autowired
    public ConfiguracionDelSistemaServiceImpl(ConfiguracionDelSistemaRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;           
    }

    @Override
    public ConfiguracionDelSistema getConfiguracionDelSistemaPorId(long id_ConfiguracionDelSistema) {
        ConfiguracionDelSistema cds = configuracionRepository.findOne(id_ConfiguracionDelSistema); 
        if (cds == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cds_no_existente"));
        }
        return cds;        
    }

    @Override
    public ConfiguracionDelSistema getConfiguracionDelSistemaPorEmpresa(Empresa empresa) {        
        return configuracionRepository.findByEmpresa(empresa);        
    }

    @Override
    @Transactional
    public ConfiguracionDelSistema guardar(ConfiguracionDelSistema cds) {        
        cds = configuracionRepository.save(cds);        
        LOGGER.warn("La Configuracion del Sistema " + cds + " se guardó correctamente." );
        return cds;
    }

    @Override
    @Transactional
    public void actualizar(ConfiguracionDelSistema cds) {        
        configuracionRepository.save(cds);        
    }
}
