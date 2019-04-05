package sic.builder;

import java.util.Date;
import sic.modelo.Cliente;
import sic.modelo.CondicionIVA;
import sic.modelo.Empresa;
import sic.modelo.Localidad;

public class ClienteBuilder {

    private long id_Cliente = 0L;
    private String razonSocial = "Construcciones S.A.";
    private String nombreFantasia = "Servimetal";
    private String direccion = "Perugorria 2421";
    private CondicionIVA condicionIVA = new CondicionIVABuilder().build();
    private String id_Fiscal = "23248527419";
    private String email = "servimetal@hotmail.com";
    private String telPrimario = "379 4587114";
    private String telSecundario = "379 4852498";
    private Localidad localidad = new LocalidadBuilder().build();
    private String contacto = "Facundo Pastore";
    private Date fechaAlta = new Date(1342580400000L); // 18/07/2012
    private Empresa empresa = new EmpresaBuilder().build();
    private boolean eliminado = false;
    private boolean predeterminado = false;
    
    public Cliente build() {
        return new Cliente(id_Cliente, razonSocial, nombreFantasia, direccion, condicionIVA,
                id_Fiscal, email, telPrimario, telSecundario, localidad, contacto, fechaAlta,
                empresa, eliminado, predeterminado);
    }
    
    public ClienteBuilder withId_Cliente(long id_Cliente) {
        this.id_Cliente = id_Cliente;
        return this;
    }
    
    public ClienteBuilder withRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
        return this;
    }
    
    public ClienteBuilder withNombreFantasia(String nombreFantasia) {
        this.nombreFantasia = nombreFantasia;
        return this;
    }
    
    public ClienteBuilder withDireccion(String direccion) {
        this.direccion = direccion;
        return this;
    }
    
    public ClienteBuilder withCondicionIVA(CondicionIVA condicionIVA) {
        this.condicionIVA = condicionIVA;
        return this;
    }
    
    public ClienteBuilder withId_Fiscal(String id_Fiscal) {
        this.id_Fiscal = id_Fiscal;
        return this;
    }
    
    public ClienteBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public ClienteBuilder withTelPrimario(String telPrimario) {
        this.telPrimario = telPrimario;
        return this;
    }
    
    public ClienteBuilder withTelSecundario(String telSecundario) {
        this.telSecundario = telSecundario;
        return this;
    }
    
    public ClienteBuilder withLocalidad(Localidad localidad) {
        this.localidad = localidad;
        return this;
    }
    
    public ClienteBuilder withContacto(String contacto) {
        this.contacto = contacto;
        return this;
    }
    
    public ClienteBuilder withFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
        return this;
    }
    
    public ClienteBuilder withEmpresa(Empresa empresa) {
        this.empresa = empresa;
        return this;
    }
    
    public ClienteBuilder withEliminado(boolean eliminado) {
        this.eliminado = eliminado;
        return this;
    }
    
    public ClienteBuilder withPredeterminado(boolean predeterminado) {
        this.predeterminado = predeterminado;
        return this;
    }
}
