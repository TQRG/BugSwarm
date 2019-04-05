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
import sic.modelo.Pais;
import sic.service.IPaisService;


@RestController
@RequestMapping("/api/v1")
public class PaisController {
    
    private final IPaisService paisService;
    
    @Autowired
    public PaisController(IPaisService paisService) {
        this.paisService = paisService;
    }
    
    @GetMapping("/paises/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Pais getPaisPorId(@PathVariable("id") long id) {
        return paisService.getPaisPorId(id);
    }
    
    @PutMapping("/paises")
    @ResponseStatus(HttpStatus.OK)
    public Pais actualizar(@RequestBody Pais pais) {
        if(paisService.getPaisPorId(pais.getId_Pais()) != null) {
            paisService.actualizar(pais);
        }
        return paisService.getPaisPorId(pais.getId_Pais());
    }
    
    @DeleteMapping("/paises/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") long id) {
        Pais pais = paisService.getPaisPorId(id);
        if(pais != null) {
            paisService.eliminar(pais);
        }
    }
    
    @PostMapping("/paises")
    @ResponseStatus(HttpStatus.CREATED)
    public Pais guardar(@RequestBody Pais pais) {
        paisService.guardar(pais);
        return paisService.getPaisPorId(pais.getId_Pais());
    }
    
    @GetMapping("/paises")
    @ResponseStatus(HttpStatus.OK)
    public List<Pais> getPaises() {
        return paisService.getPaises();
    }
    
}
