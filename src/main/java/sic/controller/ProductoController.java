package sic.controller;

import java.util.ArrayList;
import java.util.List;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import sic.modelo.BusquedaProductoCriteria;
import sic.modelo.Empresa;
import sic.modelo.Factura;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.Rubro;
import sic.service.IEmpresaService;
import sic.service.IProductoService;
import sic.service.IProveedorService;
import sic.service.IRubroService;
import sic.service.ServiceException;
import sic.service.TipoDeOperacion;

@RestController
@RequestMapping("/api/v1")
public class ProductoController {
    
    private final IProductoService productoService;
    private final IEmpresaService empresaService;
    private final IRubroService rubroService;
    private final IProveedorService proveedorService;
    
    @Autowired
    public ProductoController(IProductoService productoService, IEmpresaService empresaService,
                              IRubroService rubroService, IProveedorService proveedorService){
       this.productoService = productoService;
       this.empresaService = empresaService;
       this.rubroService = rubroService;
       this.proveedorService = proveedorService;
    }
    
    @GetMapping("/productos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Producto getProductoPorId(@PathVariable("id") long id){
        return productoService.getProductoPorId(id);
    }
    
    @GetMapping("/productos/{descripcion}/empresas/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    public Producto getProductoPorDescripcion(@PathVariable("descripcion") String descripcion,
                                              @PathVariable(value = "idEmpresa") long idEmpresa){
        return productoService.getProductoPorDescripcion(descripcion, empresaService.getEmpresaPorId(idEmpresa));
    }
    
    @GetMapping("/productos/criteria/empresas/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    public List<Producto> getProductosPorDescripcionQueContenga(@PathVariable(value = "idEmpresa") long idEmpresa,
                                                                @RequestParam(value = "criteria", required = true) String criteria,
                                                                @RequestParam(value = "cantRegistros", required = true) int cantRegistros){
     return productoService.getProductosPorDescripcionQueContenga(criteria, cantRegistros, empresaService.getEmpresaPorId(idEmpresa));
    }
    
    @GetMapping("/productos/{codigo}/empresas/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    public Producto getProductoPorCodigo(@PathVariable(value = "idEmpresa") long idEmpresa,
                                         @RequestParam(value = "codigoProducto", required = false) String codigoProducto) {
        
        
        return productoService.getProductoPorCodigo(codigoProducto, empresaService.getEmpresaPorId(idEmpresa));
    }
    
    @GetMapping("/productos/criteria/{idRubro}/empresa/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    public List<Producto> buscarProductos(@RequestParam(value = "codigo", required = false) String codigo,
                                          @RequestParam(value = "descripcion", required = false) String descripcion,
                                          @PathVariable(value = "idRubro") long idRubro,
                                          @RequestParam(value = "razonSocialProveedor", required = false) String razonSocialProveedor,
                                          @PathVariable(value = "idEmpresa") long idEmpresa,
                                          @RequestParam(value = "cantidadRegistros", required = false) int cantidadRegistros,
                                          @RequestParam(value = "soloFaltantes", required = false) boolean soloFantantes) {
        
        Empresa empresa = empresaService.getEmpresaPorId(idEmpresa);
        Rubro rubro = rubroService.getRubroPorId(idRubro);
        Proveedor proveedor = proveedorService.getProveedorPorRazonSocial(razonSocialProveedor, empresa); 
        BusquedaProductoCriteria criteria = new BusquedaProductoCriteria(
                                                                        (codigo!=null), codigo,
                                                                        (descripcion!=null), descripcion,
                                                                        (idRubro!=0), rubro,
                                                                        (razonSocialProveedor!=null), proveedor,
                                                                        empresa, 0, soloFantantes);
        return productoService.buscarProductos(criteria);
    }
    
    @DeleteMapping("/productos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") long id){
        Producto producto = productoService.getProductoPorId(id);
        producto.setEliminado(true);
        productoService.actualizar(producto);
    }
    
    @DeleteMapping("/productos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarMultiplesProductos(
            @RequestParam(value = "Ids", required = true) long[] Ids){
             List<Producto> productos = new ArrayList<>();
             for(Long Id : Ids){
                  productos.add(productoService.getProductoPorId(Id));
             }
            productoService.eliminarMultiplesProductos(productos);
    }
    
    @PutMapping("/productos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Producto actualizar(@PathVariable("id") long id, @RequestBody Producto producto){
        if(productoService.getProductoPorId(id) != null){
            productoService.actualizar(producto);
        }
        return productoService.getProductoPorId(producto.getId_Producto());
    }
    
    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public Producto guardar(@RequestBody Producto producto){
        productoService.guardar(producto);
        return productoService.getProductoPorId(producto.getId_Producto());
    }
    
    @PutMapping("/productos/stock")
    @ResponseStatus(HttpStatus.NO_CONTENT) 
    public void actualizarStock(@RequestBody Factura factura, @RequestBody TipoDeOperacion tipoDeOperacion){
        productoService.actualizarStock(factura, tipoDeOperacion);
    }
    
    @GetMapping("/productos/gananciaNeto")
    @ResponseStatus(HttpStatus.OK)
    public double calcularGanancia_Neto(@RequestParam(value = "precioCosto", required = true) double precioCosto, 
                                        @RequestParam(value = "gananciaPorcentaje", required = true) double gananciaPorcentaje){
    return productoService.calcularGanancia_Neto(precioCosto, gananciaPorcentaje);
    }
    
    @GetMapping("/productos/stock/{id}")
    @ResponseStatus(HttpStatus.OK)
    public boolean existeStockDisponible(@RequestParam(value = "cantidad", required = true) double cantidad, 
                                         @PathVariable("Id") long id){
     return productoService.existeStockDisponible(id, cantidad);
    }
    
    @GetMapping("/productos/gananciaPorcentaje")
    @ResponseStatus(HttpStatus.OK)
    public double calcularGanancia_Porcentaje(@RequestParam(value = "precioCosto", required = true) double precioCosto, 
                                              @RequestParam(value = "pvp", required = true) double pvp){
     return productoService.calcularGanancia_Porcentaje(precioCosto, pvp);
    }
    
    @GetMapping("/productos/IVANeto")
    @ResponseStatus(HttpStatus.OK)
    public double calcularIVA_Neto(@RequestParam(value = "precioCosto", required = true) double precioCosto, 
                                   @RequestParam(value = "iva_porcentaje", required = true) double iva_porcentaje){
     return productoService.calcularIVA_Neto(precioCosto, iva_porcentaje);
    }
    
    @GetMapping("/productos/impInternoNeto")
    @ResponseStatus(HttpStatus.OK)
    public double calcularImpInterno_Neto(@RequestParam(value = "precioCosto", required = true) double precioCosto, 
                                          @RequestParam(value = "impInterno_porcentaje", required = true) double impInterno_porcentaje){
     return productoService.calcularImpInterno_Neto(precioCosto, impInterno_porcentaje);
    }
    
    @GetMapping("/productos/PVP")
    @ResponseStatus(HttpStatus.OK)
    public double calcularPVP(@RequestParam(value = "precioCosto", required = true) double precioCosto, 
                              @RequestParam(value = "ganancia_porcentaje", required = true) double ganancia_porcentaje){
     return productoService.calcularPVP(precioCosto, ganancia_porcentaje);
    }
    
    @GetMapping("/productos/precioLista")
    @ResponseStatus(HttpStatus.OK)
    public double calcularPrecioLista(@RequestParam(value = "pvp", required = true) double PVP, 
                                      @RequestParam(value = "iva_porcentaje", required = true) double iva_porcentaje, 
                                      @RequestParam(value = "impInterno_porcentaje", required = true) double impInterno_porcentaje){
     return productoService.calcularPrecioLista(PVP, iva_porcentaje, impInterno_porcentaje);
    }

    @GetMapping(value = "/productos", produces=MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] getReporteListaDePrecios(@RequestBody List<Producto> productos){
       try{
        JasperPrint reporte = productoService.getReporteListaDePrecios(productos);
        return JasperExportManager.exportReportToPdf(reporte);
       } catch(JRException ex){
        throw new ServiceException(ex);
       }
    }
}
