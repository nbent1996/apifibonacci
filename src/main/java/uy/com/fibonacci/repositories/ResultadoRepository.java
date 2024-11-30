package uy.com.fibonacci.repositories;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uy.com.fibonacci.models.ResultadoModel;

@Repository
public interface ResultadoRepository extends CrudRepository<ResultadoModel, Long> {

    @Query("SELECT r FROM ResultadoModel r WHERE r.position = :position")
    Optional<ResultadoModel> findByPosition(Long position);

    @Query("SELECT r FROM ResultadoModel r ORDER BY r.position DESC LIMIT 1")
    Optional<ResultadoModel> findMaxPosition();

    @Query("SELECT r FROM ResultadoModel r WHERE r.position <= :maxPosition ORDER BY r.position DESC LIMIT 1")
    Optional<ResultadoModel> findMaxPositionWithLimit(Long maxPosition);

    @Query(value = "SELECT * FROM ResultadoModel ORDER BY id ASC LIMIT 50", nativeQuery = true)
    ArrayList<ResultadoModel> find50Resultado();

}
