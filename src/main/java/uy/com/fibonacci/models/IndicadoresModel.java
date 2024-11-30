package uy.com.fibonacci.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="fib_indicadores")
public class IndicadoresModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resultado_id", referencedColumnName = "id")
    private ResultadoModel resultado;

    private Long requestCount=1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResultadoModel getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoModel resultado) {
        this.resultado = resultado;
    }

    public Long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Long requestCount) {
        this.requestCount = requestCount;
    }

    public IndicadoresModel(Long id, ResultadoModel resultado, Long requestCount) {
        this.id = id;
        this.resultado = resultado;
        this.requestCount = requestCount;
    }

    public IndicadoresModel(ResultadoModel resultado, Long requestCount) {
        this.resultado = resultado;
        this.requestCount = requestCount;
    }

    public IndicadoresModel() {
    }
    
}
