package sic.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import sic.modelo.PreciosProducto;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.Rubro;
import sic.service.IEmpresaService;
import sic.service.IMedidaService;
import sic.service.IProductoService;
import sic.service.IProveedorService;
import sic.service.IRubroService;

@RestController
@RequestMapping("/api/v1")
public class ProductoController {
    
    private final IProductoService productoService;
    private final IEmpresaService empresaService;
    private final IRubroService rubroService;
    private final IProveedorService proveedorService;  
    private final IMedidaService medidaService;
    
    @Autowired
    public ProductoController(IProductoService productoService, IEmpresaService empresaService,
                              IRubroService rubroService, IProveedorService proveedorService,
                              IMedidaService medidaService) {        
       this.productoService = productoService;
       this.empresaService = empresaService;
       this.rubroService = rubroService;
       this.proveedorService = proveedorService;
       this.medidaService = medidaService;
    }
    
    @GetMapping("/productos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Producto getProductoPorId(@PathVariable("id") long id) {
        return productoService.getProductoPorId(id);
    }
    
    @GetMapping("/productos/busqueda")
    @ResponseStatus(HttpStatus.OK)
    public Producto getProductoPorCodigo(@RequestParam(value = "idEmpresa") long idEmpresa,
                                         @RequestParam(value = "codigo") String codigo) {        
        return productoService.getProductoPorCodigo(codigo, empresaService.getEmpresaPorId(idEmpresa));
    }
    
    @GetMapping("/productos/busqueda/criteria") 
    @ResponseStatus(HttpStatus.OK)
    public List<Producto> buscarProductos(@RequestParam(value = "codigo", required = false) String codigo,
                                          @RequestParam(value = "descripcion", required = false) String descripcion,
                                          @RequestParam(value = "idRubro", required = false) Long idRubro,
                                          @RequestParam(value = "idProveedor", required = false) Long idProveedor,
                                          @RequestParam(value = "idEmpresa") long idEmpresa,
                                          @RequestParam(value = "soloFaltantes", required = false) boolean soloFantantes) {        
        Rubro rubro = rubroService.getRubroPorId(idRubro);
        Proveedor proveedor = proveedorService.getProveedorPorId(idProveedor);
        BusquedaProductoCriteria criteria = BusquedaProductoCriteria.builder()
                                            .buscarPorCodigo((codigo!=null))
                                            .codigo(codigo)
                                            .buscarPorDescripcion(descripcion!=null)
                                            .descripcion(descripcion)
                                            .buscarPorRubro(rubro!=null)
                                            .rubro(rubro)
                                            .buscarPorProveedor(proveedor!=null)
                                            .proveedor(proveedor)
                                            .empresa(empresaService.getEmpresaPorId(idEmpresa))
                                            .cantRegistros(0)
                                            .listarSoloFaltantes(soloFantantes)
                                            .build();
        return productoService.buscarProductos(criteria);
    }
    
    @DeleteMapping("/productos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") long id) {        
        Producto producto = productoService.getProductoPorId(id);
        producto.setEliminado(true);
        productoService.actualizar(producto);
    }
    
    @DeleteMapping("/productos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarMultiplesProductos(@RequestParam(value = "id") long[] id) {
        List<Producto> productos = new ArrayList<>();
        for(Long Id : id){
             productos.add(productoService.getProductoPorId(Id));
        }
        productoService.eliminarMultiplesProductos(productos);
    }
    
    @PutMapping("/productos")
    @ResponseStatus(HttpStatus.OK)
    public Producto actualizar(@RequestBody Producto producto) {
        if(productoService.getProductoPorId(producto.getId_Producto()) != null) {
            productoService.actualizar(producto);
        }
        return productoService.getProductoPorId(producto.getId_Producto());
    }
    
    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public Producto guardar(@RequestBody Producto producto) {
        productoService.guardar(producto);
        return productoService.getProductoPorId(producto.getId_Producto());
    }
    
    @GetMapping("/productos/{id}/stock/disponibilidad")
    @ResponseStatus(HttpStatus.OK)
    public boolean existeStockDisponible(@PathVariable("Id") long id,
                                         @RequestParam(value = "cantidad") double cantidad) {
        return productoService.existeStockDisponible(id, cantidad);
    }
    
    @GetMapping("/productos/gananciaNeto")
    @ResponseStatus(HttpStatus.OK)
    public double calcularGanancia_Neto(@RequestParam(value = "precioCosto") double precioCosto, 
                                        @RequestParam(value = "gananciaPorcentaje") double gananciaPorcentaje) {
        return productoService.calcularGanancia_Neto(precioCosto, gananciaPorcentaje);
    }    
    
    @GetMapping("/productos/gananciaPorcentaje")
    @ResponseStatus(HttpStatus.OK)
    public double calcularGanancia_Porcentaje(@RequestParam(value = "precioCosto") double precioCosto, 
                                              @RequestParam(value = "pvp") double pvp){
        return productoService.calcularGanancia_Porcentaje(precioCosto, pvp);
    }
    
    @GetMapping("/productos/IVANeto")
    @ResponseStatus(HttpStatus.OK)
    public double calcularIVA_Neto(@RequestParam(value = "precioCosto") double precioCosto, 
                                   @RequestParam(value = "ivaPorcentaje") double ivaPorcentaje) {
        return productoService.calcularIVA_Neto(precioCosto, ivaPorcentaje);
    }
    
    @GetMapping("/productos/impInternoNeto")
    @ResponseStatus(HttpStatus.OK)
    public double calcularImpInterno_Neto(@RequestParam(value = "precioCosto") double precioCosto, 
                                          @RequestParam(value = "impInternoPorcentaje") double impInternoPorcentaje){
        return productoService.calcularImpInterno_Neto(precioCosto, impInternoPorcentaje);
    }
    
    @GetMapping("/productos/PVP")
    @ResponseStatus(HttpStatus.OK)
    public double calcularPVP(@RequestParam(value = "precioCosto") double precioCosto, 
                              @RequestParam(value = "gananciaPorcentaje") double gananciaPorcentaje) {
        return productoService.calcularPVP(precioCosto, gananciaPorcentaje);
    }
    
    @GetMapping("/productos/precioLista")
    @ResponseStatus(HttpStatus.OK)
    public double calcularPrecioLista(@RequestParam(value = "pvp") double PVP, 
                                      @RequestParam(value = "ivaPorcentaje") double ivaPorcentaje, 
                                      @RequestParam(value = "impInternoPorcentaje") double impInternoPorcentaje) {
        return productoService.calcularPrecioLista(PVP, ivaPorcentaje, impInternoPorcentaje);
    }

    @GetMapping("/productos/reporte/criteria")
    public ResponseEntity<byte[]> getReporteListaDePrecios(@RequestParam(value = "idEmpresa") long idEmpresa,
                                                           @RequestParam(value = "codigo", required = false) String codigo,
                                                           @RequestParam(value = "descripcion", required = false) String descripcion,
                                                           @RequestParam(value = "idRubro", required = false) Long idRubro,
                                                           @RequestParam(value = "idProveedor", required = false) Long idProveedor,                                                           
                                                           @RequestParam(value = "soloFaltantes", required = false) boolean soloFantantes) {
        
        Rubro rubro = rubroService.getRubroPorId(idRubro);
        Proveedor proveedor = proveedorService.getProveedorPorId(idProveedor);
        BusquedaProductoCriteria criteria = BusquedaProductoCriteria.builder()
                .buscarPorCodigo((codigo != null))
                .codigo(codigo)
                .buscarPorDescripcion(descripcion != null)
                .descripcion(descripcion)
                .buscarPorRubro(rubro != null)
                .rubro(rubro)
                .buscarPorProveedor(proveedor != null)
                .proveedor(proveedor)
                .empresa(empresaService.getEmpresaPorId(idEmpresa))
                .cantRegistros(0)
                .listarSoloFaltantes(soloFantantes)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.setContentDispositionFormData("ListaDePrecios.pdf", "ListaDePrecios.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        byte[] reportePDF = productoService.getReporteListaDePreciosPorEmpresa(productoService.buscarProductos(criteria), idEmpresa);
        return new ResponseEntity<>(reportePDF, headers, HttpStatus.OK);
    }
    
    @PutMapping("/productos/multiples")
    @ResponseStatus(HttpStatus.OK)
    public List<Producto> modificarMultiplesProductos(@RequestParam(value = "id") long[] id,                                                       
                                                      @RequestParam(value = "idMedida", required = false) Long idMedida,
                                                      @RequestParam(value = "idRubro", required = false) Long idRubro,
                                                      @RequestParam(value = "idProveedor", required = false) Long idProveedor,
                                                      @RequestBody(required = false) PreciosProducto preciosProducto) {
        List<Producto> productos = new ArrayList<>();
        for(long idProducto : id) {
            productos.add(productoService.getProductoPorId(idProducto));
        }       
        productoService.modificarMultiplesProductos(productos,
                                                   (preciosProducto!=null), preciosProducto,
                                                   (idMedida!=null), medidaService.getMedidaPorId(idMedida),
                                                   (idRubro!=null), rubroService.getRubroPorId(idRubro),
                                                   (idProveedor!=null), proveedorService.getProveedorPorId(idProveedor));
        return productos;
    
    }
}
