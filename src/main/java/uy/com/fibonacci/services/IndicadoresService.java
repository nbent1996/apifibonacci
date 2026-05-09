package uy.com.fibonacci.services;

import uy.com.fibonacci.dto.IndicadoresDTO;
import uy.com.fibonacci.models.IndicadoresModel;
import uy.com.fibonacci.models.ResultadoModel;
import uy.com.fibonacci.repositories.IndicadoresRepository;

import java.util.ArrayList;
import java.util.List;
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
    
    public List<IndicadoresDTO> obtenerIndicadores(){
        return mapToIndicadoresDTOList(indicadoresRepository.findAll());
    }

    public List<IndicadoresDTO> TopRequestByOrderDesc(){
        return mapToIndicadoresDTOList(indicadoresRepository.findTopRequestByOrderDesc());
    }

    public void deleteAll(){
        indicadoresRepository.deleteAll();
    }

    public IndicadoresDTO guardarIndicador(IndicadoresModel indicador){
        return mapToIndicadoresDTO(indicadoresRepository.save(indicador));
    }

    public ResponseEntity<IndicadoresDTO> obtenerIndicadorPorPositionResultado(@Param("resultadoPosition") Long position) throws Exception {
        IndicadoresModel indicador = indicadoresRepository.getIndicadorByResultadoPosition(position)
                .orElseThrow(() -> new Exception("No existe indicador para la posición: " + position));

        return ResponseEntity.ok(mapToIndicadoresDTO(indicador));
    }
 
    public ResponseEntity<IndicadoresDTO> aumentarIndicador(ResultadoModel resultado) throws Exception {
        try{
        Optional<IndicadoresModel> indicadorExistente = indicadoresRepository.getIndicadorByResultadoPosition(resultado.getPosition());
        if(indicadorExistente.isPresent()){
            indicadorExistente.get().setRequestCount(indicadorExistente.get().getRequestCount()+1L);
            indicadoresRepository.save(indicadorExistente.get());
            return ResponseEntity.ok(mapToIndicadoresDTO(indicadorExistente.get()));
        }else{
            IndicadoresModel ind = new IndicadoresModel();
            ind.setResultado(resultado);
            ind.setRequestCount(1L);
            return ResponseEntity.ok(mapToIndicadoresDTO(indicadoresRepository.save(ind)));
        }
    }catch(Exception ex){
        logger.error("Error al obtener el indicador por ID de resultado: " + ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    }
    private List<IndicadoresDTO> mapToIndicadoresDTOList(Iterable<IndicadoresModel> iterable) {
        List<IndicadoresDTO> retorno = new ArrayList<>();

        for (IndicadoresModel item : iterable) {
            retorno.add(mapToIndicadoresDTO(item));
        }

        return retorno;
    }
    private IndicadoresDTO mapToIndicadoresDTO(IndicadoresModel model) {
        if (model == null) {
            return null;
        }

        ResultadoModel resultado = model.getResultado();

        return new IndicadoresDTO(
                model.getId(),
                resultado != null ? resultado.getId() : null,
                resultado != null ? resultado.getPosition() : null,
                resultado != null ? resultado.getFibonacci_value() : null,
                model.getRequestCount()
        );
    }
}
