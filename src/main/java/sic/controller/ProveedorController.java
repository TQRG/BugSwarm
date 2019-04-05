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
import sic.modelo.BusquedaProveedorCriteria;
import sic.modelo.Proveedor;
import sic.service.IEmpresaService;
import sic.service.ILocalidadService;
import sic.service.IPaisService;
import sic.service.IProveedorService;
import sic.service.IProvinciaService;

@RestController
@RequestMapping("/api/v1")
public class ProveedorController {
    
    private final IProveedorService proveedorService;
    private final IPaisService paisService;
    private final IProvinciaService provinciaService;
    private final ILocalidadService localidadService;
    private final IEmpresaService empresaService;
    
    @Autowired
    public ProveedorController(IProveedorService proveedorService, IPaisService paisService,
            IProvinciaService provinciaService, ILocalidadService localidadService, IEmpresaService empresaService) {
        this.proveedorService = proveedorService;
        this.paisService = paisService;
        this.provinciaService = provinciaService;
        this.localidadService = localidadService;
        this.empresaService = empresaService;
    }
    
    @GetMapping("/proveedores/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Proveedor getProveedorPorId(@PathVariable("id") long id) {
        return this.proveedorService.getProveedorPorId(id);
    }
    
    @PostMapping("/proveedores")
    @ResponseStatus(HttpStatus.CREATED)
    public Proveedor guardar(@RequestBody Proveedor proveedor) {
        proveedorService.guardar(proveedor);
        return proveedorService.getProveedorPorId(proveedor.getId_Proveedor());
    }
    
    @PutMapping("/proveedores")
    @ResponseStatus(HttpStatus.OK)
    public Proveedor actualizar(@RequestBody Proveedor proveedor) {
        if(proveedorService.getProveedorPorId(proveedor.getId_Proveedor()) != null) {
            proveedorService.actualizar(proveedor);
        }
        return proveedorService.getProveedorPorId(proveedor.getId_Proveedor());
    }
    
    @GetMapping("/proveedores/busqueda/criteria")
    @ResponseStatus(HttpStatus.OK)
    public List<Proveedor> buscarProveedores(@RequestParam(value = "codigo", required = false) String codigo,
                                             @RequestParam(value = "razonSocial", required = false) String razonSocial,
                                             @RequestParam(value = "idFiscal", required = false) String idFiscal,
                                             @RequestParam(value = "idPais", required = false) Long idPais,
                                             @RequestParam(value = "idProvincia", required = false) Long idProvincia,
                                             @RequestParam(value = "idLocalidad", required = false) Long idLocalidad,
                                             @RequestParam(value = "idEmpresa") long idEmpresa) {
        BusquedaProveedorCriteria criteria = new BusquedaProveedorCriteria(
                                                 (codigo != null), codigo,
                                                 (razonSocial != null), razonSocial,
                                                 (idFiscal != null), idFiscal,
                                                 (idPais != null), paisService.getPaisPorId(idPais),
                                                 (idProvincia != null), provinciaService.getProvinciaPorId(idProvincia),
                                                 (idLocalidad != null), localidadService.getLocalidadPorId(idLocalidad),
                                                  empresaService.getEmpresaPorId(idEmpresa), 0);
        return proveedorService.buscarProveedores(criteria);
    }
    
    @DeleteMapping("/proveedores/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") long id) {
        proveedorService.eliminar(proveedorService.getProveedorPorId(id));
    }
    
    @GetMapping("/proveedores/empresa/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Proveedor> getProveedores(@PathVariable("id") long id) {
        return proveedorService.getProveedores(empresaService.getEmpresaPorId(id));
    }
    
}
