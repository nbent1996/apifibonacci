package uy.com.fibonacci.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uy.com.fibonacci.models.ResultadoModel;
import uy.com.fibonacci.services.ResultadoService;

@RestController
@RequestMapping("/resultado")
public class ResultadoController {
    @Autowired
    ResultadoService resultadoService;

    @GetMapping("/obtenerTodos")
    public Page<ResultadoModel> obtenerResultados(){
        return resultadoService.obtenerResultados();
    }

    @GetMapping("/limpiarCache")
    public void limpiarCache(){
        resultadoService.limpiarCache();
    }

    @GetMapping("/getFibonacci/{n}")
    public ResponseEntity<ResultadoModel> getFibonacci(@PathVariable Long n) throws Exception {
        return resultadoService.getFibonacciValue(n);
    }
    
}
