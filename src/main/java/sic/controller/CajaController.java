package sic.controller;

import java.util.ArrayList;
import java.util.Calendar;
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
import sic.modelo.BusquedaCajaCriteria;
import sic.modelo.Caja;
import sic.modelo.Gasto;
import sic.modelo.Pago;
import sic.service.ICajaService;
import sic.service.IEmpresaService;
import sic.service.IGastoService;
import sic.service.IPagoService;
import sic.service.IUsuarioService;

@RestController
@RequestMapping("/api/v1")
public class CajaController {
    
    private final ICajaService cajaService;
    private final IPagoService pagoService;
    private final IGastoService gastoService;
    private final IEmpresaService empresaService;
    private final IUsuarioService usuarioService;
    
    @Autowired
    public CajaController(ICajaService cajaService, IPagoService pagoService,
                          IGastoService gastoService, IEmpresaService empresaService,
                          IUsuarioService usuarioService) {
        this.cajaService = cajaService;
        this.pagoService = pagoService;
        this.gastoService = gastoService;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
    }
    
    @GetMapping("/cajas/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Caja getCajaPorId(@PathVariable("id") long id) {
        return cajaService.getCajaPorId(id);
    }
    
    @PutMapping("/cajas")
    @ResponseStatus(HttpStatus.OK)
    public Caja actualizar(@RequestBody Caja caja) {
        cajaService.actualizar(caja);
        return cajaService.getCajaPorId(caja.getId_Caja());
    }
    
    @PostMapping("/cajas")
    @ResponseStatus(HttpStatus.CREATED)
    public Caja guardar(@RequestBody Caja caja) {
        cajaService.guardar(caja);
        return cajaService.getCajaPorId(caja.getId_Caja());
    }
    
    @DeleteMapping("/cajas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") long id) {
        cajaService.eliminar(cajaService.getCajaPorId(id));
    }
    
    @GetMapping("/cajas/total-pagos")
    @ResponseStatus(HttpStatus.OK)
    public double calcularTotalPagos(@RequestParam(value = "id") long[] id) {
        List<Pago> pagos = new ArrayList<>();
        for(long Id : id) {
            pagos.add(pagoService.getPagoPorId(Id));
        }
        return cajaService.calcularTotalPagos(pagos);
    }
    
    @GetMapping("/cajas/total-gastos")
    @ResponseStatus(HttpStatus.OK)
    public double calcularTotalGastos(@RequestParam(value = "id") long[] id) {
        List<Gasto> gastos = new ArrayList<>();
        for(Long Id : id) {
            gastos.add(gastoService.getGastoPorId(Id));
        }
        return cajaService.calcularTotalGastos(gastos);
    }
    
    @GetMapping("/cajas/busqueda/criteria")
    @ResponseStatus(HttpStatus.OK)
    public List<Caja> getCajasCriteria(@RequestParam(value = "idEmpresa") long idEmpresa,
                                       @RequestParam(value = "desde", required = false) Long desde,
                                       @RequestParam(value = "hasta", required = false) Long hasta,
                                       @RequestParam(value = "idUsuario", required = false) Long idUsuario) {
        Calendar fechaDesde = Calendar.getInstance();
        fechaDesde.setTimeInMillis(desde);
        Calendar fechaHasta = Calendar.getInstance();
        fechaHasta.setTimeInMillis(hasta);
        BusquedaCajaCriteria criteria = BusquedaCajaCriteria.builder()
                                        .buscaPorFecha((desde != null) && (hasta != null))
                                        .fechaDesde(fechaDesde.getTime())
                                        .fechaHasta(fechaHasta.getTime())
                                        .empresa(empresaService.getEmpresaPorId(idEmpresa))
                                        .cantidadDeRegistros(0)
                                        .buscaPorUsuario(idUsuario != null)
                                        .usuario(usuarioService.getUsuarioPorId(idUsuario))
                                        .build();
        return cajaService.getCajasCriteria(criteria);        
    }
    
    @GetMapping("/cajas/empresa/{id}/ultima")
    @ResponseStatus(HttpStatus.OK)
    public Caja getUltimaCaja(@PathVariable long id) {
        return cajaService.getUltimaCaja(id);
    }
    
    @PutMapping("/cajas/empresa/{id}/cerrar-dia-anterior")
    @ResponseStatus(HttpStatus.OK)
    public Caja cerrarCajaDiaAnterior(@PathVariable long id) {
        return cajaService.cerrarCajaDiaAnterior(empresaService.getEmpresaPorId(id));
    }
    
    @GetMapping("/cajas/{idCaja}/empresa/{idEmpresa}/reporte")
    public ResponseEntity<byte[]> getReporteCaja(@PathVariable long idCaja,
                                                 @PathVariable long idEmpresa) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.setContentDispositionFormData("reporteCaja.pdf", "ReporteCaja.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        byte[] reportePDF = cajaService.getReporteCaja(cajaService.getCajaPorId(idCaja), idEmpresa);
        return new ResponseEntity<>(reportePDF, headers, HttpStatus.OK);
    }
    
}
