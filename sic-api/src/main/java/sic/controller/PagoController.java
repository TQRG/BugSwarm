package sic.controller;

import java.util.ArrayList;
import java.util.Calendar;
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
import sic.modelo.Factura;
import sic.modelo.Pago;
import sic.service.IFacturaService;
import sic.service.IFormaDePagoService;
import sic.service.IPagoService;

@RestController
@RequestMapping("/api/v1")
public class PagoController {
    
    private final IPagoService pagoService;
    private final IFacturaService facturaService;
    private final IFormaDePagoService formaDePagoService;
    
    @Autowired
    public PagoController(IPagoService pagoService, IFacturaService facturaService,
                          IFormaDePagoService formaDePago) {
        this.pagoService = pagoService;
        this.facturaService = facturaService;
        this.formaDePagoService = formaDePago;
    }
    
    @GetMapping("/pagos/{idPago}")
    @ResponseStatus(HttpStatus.OK)
    public Pago getProductoPorId(@PathVariable long idPago) {
        return pagoService.getPagoPorId(idPago);
    }
    
    @PostMapping("/pagos/facturas/{idFactura}")
    @ResponseStatus(HttpStatus.CREATED)
    public Pago guardar(@PathVariable long idFactura,
                        @RequestBody Pago pago) {
        pago.setFactura(facturaService.getFacturaPorId(idFactura));
        return pagoService.guardar(pago); 
    }
    
    @DeleteMapping("/pagos/{idPago}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable long idPago) {
        pagoService.eliminar(idPago);
    }
    
    @GetMapping("/pagos/facturas/{idFactura}")
    @ResponseStatus(HttpStatus.OK)
    public List<Pago> getPagosDeLaFactura(@PathVariable long idFactura) {
        return pagoService.getPagosDeLaFactura(idFactura);
    }
    
    @GetMapping("/pagos/facturas/{idFactura}/saldo")
    @ResponseStatus(HttpStatus.OK)
    public double getSaldoAPagar(@PathVariable long idFactura) {
        return pagoService.getSaldoAPagar(facturaService.getFacturaPorId(idFactura));
    }
    
    @GetMapping("/pagos/facturas/{idFactura}/total-pagado")
    @ResponseStatus(HttpStatus.OK)
    public double getTotalPagado(@PathVariable long idFactura) {
        return facturaService.getTotalPagado(facturaService.getFacturaPorId(idFactura));
    }
    
    @GetMapping("/pagos/busqueda")
    @ResponseStatus(HttpStatus.OK)
    public List<Pago> getPagosEntreFechasYFormaDePago(@RequestParam long idEmpresa,
                                                      @RequestParam long idFormaDePago,
                                                      @RequestParam long desde,
                                                      @RequestParam long hasta) {
        Calendar fechaDesde = Calendar.getInstance();
        fechaDesde.setTimeInMillis(desde);
        Calendar fechaHasta = Calendar.getInstance();
        fechaHasta.setTimeInMillis(hasta);
        return pagoService.getPagosEntreFechasYFormaDePago(idEmpresa, idFormaDePago,
                fechaDesde.getTime(), fechaHasta.getTime());
    }
    
    @PutMapping("/pagos/pagar-multiples-facturas")
    @ResponseStatus(HttpStatus.OK)
    public void pagarMultiplesFacturas(@RequestParam long[] idFactura,
                                       @RequestParam double monto,
                                       @RequestParam long idFormaDePago,                                       
                                       @RequestParam long fechaHora,
                                       @RequestParam(required = false) String nota) {
        if (nota == null) {
            nota = "";
        }
        Calendar fechaYHora = Calendar.getInstance();
        fechaYHora.setTimeInMillis(fechaHora);
        List<Factura> facturas = new ArrayList<>();
        for (long i : idFactura) {
            facturas.add(facturaService.getFacturaPorId(i));
        }
        pagoService.pagarMultiplesFacturas(facturas, monto,
                formaDePagoService.getFormasDePagoPorId(idFormaDePago), nota, fechaYHora.getTime());
    }
}
