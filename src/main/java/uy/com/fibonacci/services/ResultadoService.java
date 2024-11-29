package uy.com.fibonacci.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public ResultadoModel getFibonacciValue(Long n) throws Exception {
        try{
            Optional<ResultadoModel> resultadoExistente = resultadoRepository.findById(n);
        if(resultadoExistente.isPresent()){
            resultadoExistente.get().setId(n);
            //indicadoresService.aumentarIndicador(resultadoExistente.get());
            return resultadoExistente.get();
        }else{
            //Calcular el valor de fibonacci porque no esta en la base
            Long fibonacciValue= calcularFibonacci(n);
            //Persistimos
            ResultadoModel resultadoNuevo = new ResultadoModel();
            resultadoNuevo.setFibonacci_value(fibonacciValue);
            resultadoNuevo.setId(n);
            resultadoNuevo= resultadoRepository.save(resultadoNuevo);
            //indicadoresService.aumentarIndicador(resultadoNuevo);
            return resultadoNuevo;
        }
    }catch(Exception ex){
        logger.error("Error al calcular el nro fibonacci, " + ex.getMessage() );
    }
    return null;
    }
        private Long calcularFibonacci(Long n) throws Exception {
        if(n<=0){
            throw new Exception("El indice debe ser un numero positivo.");
        }

        //Si existe el valor lo devuelvo
        Optional<ResultadoModel> cachedResult = resultadoRepository.findById(n);
        if(cachedResult.isPresent()){
            return cachedResult.get().getFibonacci_value();
        }

        if(n==1){
            return 1L;
        }
        if(n==2){
            return 2L;
        }
        Long fib = 2L;
        Long prevFib=1L;
        int start = 3;

        //Encontramos hasta donde hay almacenado
        Optional<ResultadoModel> lastCachedResult = resultadoRepository.findTopByPosition();
        if(lastCachedResult.isPresent()){
            prevFib = lastCachedResult.get().getFibonacci_value() - fib;
            fib = lastCachedResult.get().getFibonacci_value();
            start = lastCachedResult.get().getId().intValue()+1;
        }

        //Continuamos la secuencia
        for(int i=start;i<=n;i++){
            Long newFib = fib+prevFib; //Calcular F(n+1) = F(n) + F(n-1)
            prevFib = fib; //F(n-1 se convierte en  F(n-2 en la siguiente iteración))
            fib=newFib; // F(n) se convierte en F(n-1 en la siguiente iteración)
        }
        return fib;
    }

}
