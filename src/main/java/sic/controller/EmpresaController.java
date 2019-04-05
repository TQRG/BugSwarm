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
import sic.modelo.Empresa;
import sic.service.IEmpresaService;

@RestController
@RequestMapping("/api/v1")
public class EmpresaController {
        
    public final IEmpresaService empresaService;

    @Autowired
    public EmpresaController(IEmpresaService empresaService) {
        this.empresaService = empresaService;
    }    
    
    @GetMapping("/empresas")
    @ResponseStatus(HttpStatus.OK)
    public List<Empresa> getEmpresa() {
        return empresaService.getEmpresas();
    }
    
    @GetMapping("/empresas/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Empresa getEmpresaPorId(@PathVariable("id") long id) {
        return empresaService.getEmpresaPorId(id);
    }
    
    @PostMapping("/empresas")
    @ResponseStatus(HttpStatus.CREATED)
    public Empresa guardar(@RequestBody Empresa empresa) {
        empresaService.guardar(empresa);
        return empresaService.getEmpresaPorId(empresa.getId_Empresa());
    }
    
    @PutMapping("/empresas")
    @ResponseStatus(HttpStatus.OK)
    public Empresa actualizar(@RequestBody Empresa empresa) {
        if(empresaService.getEmpresaPorId(empresa.getId_Empresa()) != null) {
            empresaService.actualizar(empresa);
        }
        return empresaService.getEmpresaPorId(empresa.getId_Empresa());
    }
    
    @DeleteMapping("/empresas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") long id) {
        Empresa empresa = empresaService.getEmpresaPorId(id);
        empresa.setEliminada(true);
        empresaService.actualizar(empresa);
    }
}
