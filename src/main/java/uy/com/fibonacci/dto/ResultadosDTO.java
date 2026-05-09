package uy.com.fibonacci.dto;


public class ResultadosDTO {

    private Long id;
    private Long position;
    private Long fibonacciValue;

    public ResultadosDTO() {
    }

    public ResultadosDTO(Long id, Long position, Long fibonacciValue) {
        this.id = id;
        this.position = position;
        this.fibonacciValue = fibonacciValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Long getFibonacciValue() {
        return fibonacciValue;
    }

    public void setFibonacciValue(Long fibonacciValue) {
        this.fibonacciValue = fibonacciValue;
    }

}