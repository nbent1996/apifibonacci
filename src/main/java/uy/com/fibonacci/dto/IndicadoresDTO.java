package uy.com.fibonacci.dto;

public class IndicadoresDTO {

    private Long id;
    private Long resultadoId;
    private Long numeroConsultado;
    private Long valorResultado;
    private Long requestCount;

    public IndicadoresDTO() {
    }

    public IndicadoresDTO(Long id, Long resultadoId, Long numeroConsultado, Long valorResultado, Long requestCount) {
        this.id = id;
        this.resultadoId = resultadoId;
        this.numeroConsultado = numeroConsultado;
        this.valorResultado = valorResultado;
        this.requestCount = requestCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResultadoId() {
        return resultadoId;
    }

    public void setResultadoId(Long resultadoId) {
        this.resultadoId = resultadoId;
    }

    public Long getNumeroConsultado() {
        return numeroConsultado;
    }

    public void setNumeroConsultado(Long numeroConsultado) {
        this.numeroConsultado = numeroConsultado;
    }

    public Long getValorResultado() {
        return valorResultado;
    }

    public void setValorResultado(Long valorResultado) {
        this.valorResultado = valorResultado;
    }

    public Long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Long requestCount) {
        this.requestCount = requestCount;
    }
}