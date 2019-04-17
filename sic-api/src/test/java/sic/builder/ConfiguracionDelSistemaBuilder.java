package sic.builder;

import sic.modelo.ConfiguracionDelSistema;
import sic.modelo.Empresa;

public class ConfiguracionDelSistemaBuilder {

    private long id_ConfiguracionDelSistema = 0L;
    private boolean usarFacturaVentaPreImpresa = true;
    private int cantidadMaximaDeRenglonesEnFactura = 28;
    private Empresa empresa = new EmpresaBuilder().build();

    public ConfiguracionDelSistema build() {
        return new ConfiguracionDelSistema(id_ConfiguracionDelSistema, usarFacturaVentaPreImpresa,
                cantidadMaximaDeRenglonesEnFactura, empresa);
    }

    public ConfiguracionDelSistemaBuilder withIdConfiguracionDelSistema(long idCds) {
        this.id_ConfiguracionDelSistema = idCds;
        return this;
    }

    public ConfiguracionDelSistemaBuilder withUsarFacturaVentaPreImpresa(boolean usarFacturaPreImpresa) {
        this.usarFacturaVentaPreImpresa = usarFacturaPreImpresa;
        return this;
    }

    public ConfiguracionDelSistemaBuilder withCantidadMaximaDeRenglonesEnFactura(int cantidadMaximaDeRenglonesEnFactura) {
        this.cantidadMaximaDeRenglonesEnFactura = cantidadMaximaDeRenglonesEnFactura;
        return this;
    }

    public ConfiguracionDelSistemaBuilder withEmpresa(Empresa empresa) {
        this.empresa = empresa;
        return this;
    }
}
