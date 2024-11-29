package uy.com.fibonacci.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uy.com.fibonacci.models.ResultadoModel;

@Repository
public interface ResultadoRepository extends CrudRepository<ResultadoModel, Long> {

    @Query("SELECT r FROM ResultadoModel r ORDER BY r.id DESC LIMIT 1")
    Optional<ResultadoModel> findTopByPosition();
}
