package uy.com.fibonacci.services;

import uy.com.fibonacci.models.IndicadoresModel;
import uy.com.fibonacci.models.ResultadoModel;
import uy.com.fibonacci.repositories.IndicadoresRepository;

import java.util.ArrayList;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class IndicadoresService {

    private static final Logger logger = LoggerFactory.getLogger(IndicadoresService.class);

    @Autowired
    IndicadoresRepository indicadoresRepository;
    
    public ArrayList<IndicadoresModel> obtenerIndicadores(){
        return (ArrayList<IndicadoresModel>)indicadoresRepository.findAll();
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

    public ResponseEntity<IndicadoresModel> obtenerIndicadorPorPositionResultado(@Param("resultadoPosition") Long position){
        return ResponseEntity.ok(indicadoresRepository.getIndicadorByResultadoPosition(position).get());
    }
 
    public ResponseEntity<IndicadoresModel> aumentarIndicador(ResultadoModel resultado) throws Exception {
        try{
        Optional<IndicadoresModel> indicadorExistente = indicadoresRepository.getIndicadorByResultadoPosition(resultado.getPosition());
        if(indicadorExistente.isPresent()){
            indicadorExistente.get().setRequestCount(indicadorExistente.get().getRequestCount()+1L);
            indicadoresRepository.save(indicadorExistente.get());
            return ResponseEntity.ok(indicadorExistente.get());
        }else{
            IndicadoresModel ind = new IndicadoresModel();
            ind.setResultado(resultado);
            ind.setRequestCount(1L);
            return ResponseEntity.ok(indicadoresRepository.save(ind));
        }
    }catch(Exception ex){
        logger.error("Error al obtener el indicador por ID de resultado: " + ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    }

}
