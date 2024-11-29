package uy.com.fibonacci.controllers;

import uy.com.fibonacci.models.IndicadoresModel;
import uy.com.fibonacci.models.ResultadoModel;
import uy.com.fibonacci.services.IndicadoresService;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/indicador")
public class IndicadoresController {
    @Autowired
    IndicadoresService indicadoresService;

    @GetMapping("/obtenerTodos")
    public ArrayList<IndicadoresModel> obtenerIndicadores(){
        return indicadoresService.obtenerIndicadores();
    }

    @GetMapping("/getIndicador/{id}")
    public Optional<IndicadoresModel> getIndicador(@PathVariable Long n) throws Exception{
        return this.indicadoresService.obtenerIndicadorPorIdResultado(n);
    }

    @GetMapping("/mejoresIndicadores")
    public ArrayList<IndicadoresModel> TopRequestByOrderDesc(){
        return indicadoresService.TopRequestByOrderDesc();
    }
}