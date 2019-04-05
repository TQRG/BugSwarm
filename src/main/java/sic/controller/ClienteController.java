package sic.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sic.modelo.BusquedaClienteCriteria;
import sic.modelo.Cliente;
import sic.service.IClienteService;
import sic.service.IEmpresaService;
import sic.service.ILocalidadService;
import sic.service.IPaisService;
import sic.service.IProvinciaService;

@RestController
@RequestMapping("/api/v1")
public class ClienteController {
    
    private final IClienteService clienteService;
    private final IEmpresaService empresaService;
    private final IPaisService paisService;
    private final IProvinciaService provinciaService;
    private final ILocalidadService localidadService;
    
    @Autowired
    public ClienteController(IClienteService clienteService, IEmpresaService empresaService,
            IPaisService paisService, IProvinciaService provinciaService,
            ILocalidadService localidadService) {
        this.clienteService = clienteService;
        this.empresaService = empresaService;
        this.paisService = paisService;
        this.provinciaService = provinciaService;
        this.localidadService = localidadService;
    }
  
    @GetMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Cliente getCliente(@PathVariable("id") long id) {
        return clienteService.getClientePorId(id);
    }
    
    @GetMapping("/clientes/criteria") 
    @ResponseStatus(HttpStatus.OK)
    public List<Cliente> buscarConCriteria(@RequestParam(value = "razonSocial", required = false) String razonSocial,
                                           @RequestParam(value = "nombreFantasia", required = false) String nombreFantasia,
                                           @RequestParam(value = "idFiscal", required = false) String idFiscal,
                                           @RequestParam(value = "idPais", required = false) Long idPais,
                                           @RequestParam(value = "idProvincia", required = false) Long idProvincia, 
                                           @RequestParam(value = "idLocalidad", required = false) Long idLocalidad, 
                                           @RequestParam(value = "idEmpresa") Long idEmpresa) {
        return clienteService.buscarClientes(
                new BusquedaClienteCriteria(
                (razonSocial != null), razonSocial, 
                (nombreFantasia != null), nombreFantasia, 
                (idFiscal != null), idFiscal, 
                (idPais != null), paisService.getPaisPorId(idPais), 
                (idProvincia != null), provinciaService.getProvinciaPorId(idProvincia), 
                (idLocalidad != null), localidadService.getLocalidadPorId(idLocalidad),
                empresaService.getEmpresaPorId(idEmpresa)));
    }
       
    @GetMapping("/clientes/empresa/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Cliente> getClientes(@PathVariable("id") long id) {
        return clienteService.getClientes(empresaService.getEmpresaPorId(id));
    }
    
    @GetMapping("/clientes/predeterminado/empresa/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Cliente getClientePredeterminado(@PathVariable("id") long id) {
     return clienteService.getClientePredeterminado(empresaService.getEmpresaPorId(id));
    }
    
    
    @DeleteMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") long id) {
        clienteService.eliminar(clienteService.getClientePorId(id));
    }
    
    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente guardar(@RequestBody Cliente cliente) {
        clienteService.guardar(cliente);
        return clienteService.getClientePorRazonSocial(cliente.getRazonSocial(), cliente.getEmpresa());
    }
    
    @PutMapping("/clientes")
    @ResponseStatus(HttpStatus.OK)
    public Cliente actualizar(@RequestBody Cliente cliente) {
       clienteService.actualizar(cliente);
       return clienteService.getClientePorId(cliente.getId_Cliente());
    }
    
    @PutMapping("/clientes/predeterminado")
    @ResponseStatus(HttpStatus.OK)
    public Cliente setClientePredeterminado(@RequestBody Cliente cliente) {
       clienteService.setClientePredeterminado(cliente);
       return clienteService.getClientePorId(cliente.getId_Cliente());
    }
}
