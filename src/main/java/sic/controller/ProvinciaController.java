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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sic.modelo.Provincia;
import sic.service.IPaisService;
import sic.service.IProvinciaService;

@RestController
@RequestMapping("/api/v1")
public class ProvinciaController {
    
    private final IProvinciaService provinciaService;
    private final IPaisService paisService;
    
    @Autowired
    public ProvinciaController(IProvinciaService provinciaService, IPaisService paisService) {  
            this.provinciaService = provinciaService;
            this.paisService = paisService;
    }
    
    @GetMapping("/provincias/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Provincia getProvinciaPorId(@PathVariable("id") long id) {
        return provinciaService.getProvinciaPorId(id);
    }
    
    @PutMapping("/provincias")
    @ResponseStatus(HttpStatus.OK)
    public Provincia actualizar(@RequestBody Provincia provincia) { 
        if(provinciaService.getProvinciaPorId(provincia.getId_Provincia()) != null) {
            provinciaService.actualizar(provincia);
        }
        return provinciaService.getProvinciaPorId(provincia.getId_Provincia());
    }
    
    @DeleteMapping("/provincias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") long id) {
        Provincia provincia = provinciaService.getProvinciaPorId(id);
        if(provincia != null) {
            provinciaService.eliminar(provincia);
        }
    }
    
    @GetMapping("/provincias/paises/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Provincia> getProvinciasDelPais(@PathVariable("id") long id) {
        return provinciaService.getProvinciasDelPais(paisService.getPaisPorId(id));
    }
    
    @PostMapping("/provincias")
    @ResponseStatus(HttpStatus.CREATED)
    public Provincia guardar(@RequestBody Provincia provincia) {
        provinciaService.guardar(provincia);
        return provinciaService.getProvinciaPorId(provincia.getId_Provincia());
    }
}
