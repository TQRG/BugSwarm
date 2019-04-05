package sic.controller;

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
import sic.modelo.Gasto;
import sic.service.IGastoService;

@RestController
@RequestMapping("/api/v1")
public class GastoController {
    
    private final IGastoService gastoService;
    
    @Autowired
    public GastoController(IGastoService gastoService) {
        this.gastoService = gastoService;
    }
    
    @GetMapping("/gastos/{idGasto}")
    @ResponseStatus(HttpStatus.OK)
    public Gasto getGastoPorId(@PathVariable long idGasto) {
        return gastoService.getGastoPorId(idGasto);
    }
    
    @PutMapping("/gastos")
    @ResponseStatus(HttpStatus.OK)
    public void actualizar(@RequestBody Gasto gasto) {
        if (gastoService.getGastoPorId(gasto.getId_Gasto()) != null) {
            gastoService.actualizar(gasto);
        }
    }
    
    @DeleteMapping("/gastos/{idGasto}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable long idGasto) {
        gastoService.eliminar(idGasto);
    }
    
    @GetMapping("/gastos/busqueda")
    @ResponseStatus(HttpStatus.OK)
    public List<Gasto> getGastosPorFechaYFormaDePago(@RequestParam long idEmpresa,
                                                     @RequestParam long idFormaDePago,
                                                     @RequestParam long desde,
                                                     @RequestParam long hasta) {
        Calendar fechaDesde = Calendar.getInstance();
        fechaDesde.setTimeInMillis(desde);
        Calendar fechaHasta = Calendar.getInstance();
        fechaHasta.setTimeInMillis(hasta);
        return gastoService.getGastosPorFechaYFormaDePago(idEmpresa, idFormaDePago,
                fechaDesde.getTime(), fechaHasta.getTime());
    }
    
    @PostMapping("/gastos")
    @ResponseStatus(HttpStatus.CREATED)
    public Gasto guardar(@RequestBody Gasto gasto) {
        return gastoService.guardar(gasto);
    }

}
