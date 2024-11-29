package uy.com.fibonacci.services;

import uy.com.fibonacci.models.IndicadoresModel;
import uy.com.fibonacci.models.ResultadoModel;
import uy.com.fibonacci.repositories.IndicadoresRepository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@Service
public class IndicadoresService {

    @Autowired
    IndicadoresRepository indicadoresRepository;
    
    public ArrayList<IndicadoresModel> obtenerIndicadores(){
        return (ArrayList<IndicadoresModel>) indicadoresRepository.findAll();
    }

    public ArrayList<IndicadoresModel> TopRequestByOrderDesc(){
        return indicadoresRepository.findTopRequestByOrderDesc();
    }

    public void deleteAll(){
        indicadoresRepository.deleteAll();
    }

    public IndicadoresModel guardarIndicador(IndicadoresModel indicador){
        return indicadoresRepository.save(indicador);
    }

    public Optional<IndicadoresModel> obtenerIndicadorPorIdResultado(@Param("resultadoId") Long id){
        return indicadoresRepository.getIndicadorByResultadoId(id);
    }
 
    public IndicadoresModel aumentarIndicador(ResultadoModel resultado) throws Exception {
        Optional<IndicadoresModel> indicadorExistente = indicadoresRepository.getIndicadorByResultadoId(resultado.getId());
        if(indicadorExistente.isPresent()){
            indicadorExistente.get().setRequestCount(indicadorExistente.get().getRequestCount()+1L);
            indicadoresRepository.save(indicadorExistente.get());
            return indicadorExistente.get();
        }else{
            IndicadoresModel ind = new IndicadoresModel();
            ind.setResultado(resultado);
            ind.setRequestCount(1L);
            return indicadoresRepository.save(ind);
        }
    
    }
}
