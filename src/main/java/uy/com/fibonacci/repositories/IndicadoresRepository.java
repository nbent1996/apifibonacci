package uy.com.fibonacci.repositories;

import org.springframework.stereotype.Repository;

import uy.com.fibonacci.models.IndicadoresModel;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

@Repository
public interface IndicadoresRepository extends CrudRepository<IndicadoresModel, Long> {

    @Query("SELECT i FROM IndicadoresModel i ORDER BY i.requestCount DESC")
    ArrayList<IndicadoresModel> findTopRequestByOrderDesc();

    @Query("SELECT i FROM IndicadoresModel i WHERE i.resultado.id = :resultadoId")
    Optional<IndicadoresModel> getIndicadorByResultadoId(@Param("resultadoId")Long resultadoId);
}
