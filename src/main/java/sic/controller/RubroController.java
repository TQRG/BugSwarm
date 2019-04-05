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
import sic.modelo.Rubro;
import sic.service.IEmpresaService;
import sic.service.IRubroService;

@RestController
@RequestMapping("/api/v1")
public class RubroController {
    
    private final IRubroService rubroService;
    private final IEmpresaService empresaService;
    
    @Autowired
    public RubroController(IRubroService rubroService, IEmpresaService empresaService) {
        this.rubroService = rubroService;
        this.empresaService = empresaService;
    }
    
    @GetMapping("/rubros/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Rubro getRubroPorId(@PathVariable("id") long id) {
        return rubroService.getRubroPorId(id);
    }
    
    @PutMapping("/rubros")
    @ResponseStatus(HttpStatus.OK)
    public Rubro actualizar(@RequestBody Rubro rubro) { 
        if(rubroService.getRubroPorId(rubro.getId_Rubro()) != null) {
           rubroService.actualizar(rubro);
        }
        return rubroService.getRubroPorId(rubro.getId_Rubro());
    }
    
    @DeleteMapping("/rubros/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") long id) {
       Rubro rubro = rubroService.getRubroPorId(id);
       if(rubro != null) {
           rubroService.eliminar(rubro);
       }
    }
    
    @PostMapping("/rubros")
    @ResponseStatus(HttpStatus.OK)
    public Rubro guardar(@RequestBody Rubro rubro) {
        rubroService.guardar(rubro);
        return rubroService.getRubroPorId(rubro.getId_Rubro());
    }
    
    @GetMapping("/rubros/empresa/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Rubro> getMedidas(@PathVariable long id) {
        return rubroService.getRubros(empresaService.getEmpresaPorId(id));
    }
}