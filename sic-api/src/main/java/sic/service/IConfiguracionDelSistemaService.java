package sic.service;

import sic.modelo.ConfiguracionDelSistema;
import sic.modelo.Empresa;

public interface IConfiguracionDelSistemaService {

    void actualizar(ConfiguracionDelSistema cds);

    ConfiguracionDelSistema getConfiguracionDelSistemaPorEmpresa(Empresa empresa);

    ConfiguracionDelSistema getConfiguracionDelSistemaPorId(long id_ConfiguracionDelSistema);

    ConfiguracionDelSistema guardar(ConfiguracionDelSistema cds);
    
}
