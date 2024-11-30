package uy.com.fibonacci.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import uy.com.fibonacci.models.IndicadoresModel;
import uy.com.fibonacci.models.ResultadoModel;
import uy.com.fibonacci.repositories.ResultadoRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ResultadoService  {

    private static final Logger logger = LoggerFactory.getLogger(ResultadoService.class);

    @Autowired
    ResultadoRepository resultadoRepository;

    @Autowired
    IndicadoresService indicadoresService;

    public ArrayList<ResultadoModel> obtenerResultados(){
        return (ArrayList<ResultadoModel>) resultadoRepository.findAll();
    }

    public void limpiarCache(){
        indicadoresService.deleteAll();
        resultadoRepository.deleteAll();
    }

    public ResultadoModel guardarResultado(ResultadoModel resultado){
        return resultadoRepository.save(resultado);
    }

    public ResponseEntity<ResultadoModel> getFibonacciValue(Long n) throws Exception {
        if(n<=0){
            throw new Exception("La posicion a calcular debe ser un numero positivo.");
        }
        try{
            Optional<ResultadoModel> resultadoExistente = resultadoRepository.findByPosition(n);
        if(resultadoExistente.isPresent()){
            indicadoresService.aumentarIndicador(resultadoExistente.get());
            return ResponseEntity.ok(resultadoExistente.get());
        }else{
            //Calcular el valor de fibonacci porque no esta en la base
            Optional<ResultadoModel> resultadoNuevo = calcularFibonacci(n);
            if(!resultadoNuevo.isPresent()){
                return ResponseEntity.internalServerError().build();
            }
            return ResponseEntity.ok(resultadoNuevo.get());
        }
    }catch(Exception ex){
        logger.error("Error al calcular el nro fibonacci, " + ex.getMessage() );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    }

    public Optional<ResultadoModel> calcularFibonacci(Long n) throws Exception {
        if(n<=0){
            throw new Exception("El indice debe ser un numero positivo.");
        }
        //Si existe el valor lo devuelvo
        Optional<ResultadoModel> cachedResult = resultadoRepository.findByPosition(n);
        if(cachedResult.isPresent()){
            return cachedResult;
        }

        if(n==1){
            return persistirResultado(n, 1L, 1);
        }
        if(n==2){
            return persistirResultado(n,2L,2);
        }

        //Valores por defecto, posiciones iniciales
        Long fib = 2L;
        Long prevFib=1L;
        int start = 3;

       // Encuentra el máximo almacenado por debajo de `n`
    Optional<ResultadoModel> lastCachedResult = resultadoRepository.findMaxPositionWithLimit(n);
    if (lastCachedResult.isPresent()) {
        ResultadoModel lastCached = lastCachedResult.get();
        // Corregir cálculo de prevFib
        if (lastCached.getPosition() > 1) {
            Optional<ResultadoModel> previous = resultadoRepository.findByPosition(lastCached.getPosition() - 1);
            if (previous.isPresent()) {
                prevFib = previous.get().getFibonacci_value();
            } else {
                prevFib = lastCached.getFibonacci_value() - fib; 
            }
        }
        fib = lastCached.getFibonacci_value();
        start = lastCached.getPosition().intValue() + 1; 
    }

    Optional<ResultadoModel> retorno = Optional.empty();
    // Continuamos la secuencia
    for (int i = start; i <= n; i++) {
        Long newFib = fib + prevFib; // Calcular F(n+1) = F(n) + F(n-1)
        logger.info("Iteracion i=" + i + " // " + "prevFib=" + prevFib + " // " + "fib=" + fib + " // " + "newFib: " + newFib + " // n=" + n);
        prevFib = fib; 
        fib = newFib;
        // Persistimos valor intermedio actualizado
        retorno = persistirResultado(i+0L, fib, i);
    }
    logger.info("-----------------------------------------------------------------------------");
    return retorno;
    }
    public Optional<ResultadoModel> persistirResultado(Long n, Long fib, int position) throws Exception{
            /*Persistimos valor intermedio*/
            Optional<ResultadoModel> resultadoExistente = resultadoRepository.findByPosition(position+0L);
            if(!resultadoExistente.isPresent()){
                ResultadoModel resultadoNuevo = new ResultadoModel();
                resultadoNuevo.setFibonacci_value(fib);
                resultadoNuevo.setPosition(position+0L);
                resultadoNuevo= resultadoRepository.save(resultadoNuevo);
                indicadoresService.aumentarIndicador(resultadoNuevo);
                return Optional.of(resultadoNuevo);
            }else{
            return resultadoExistente;
        }
}
}
