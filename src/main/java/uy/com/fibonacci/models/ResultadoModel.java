package uy.com.fibonacci.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="fib_resultados")
public class ResultadoModel {
    @Id
    private Long id;

    @Column(name = "fibonacci_value", nullable = false)
    private Long fibonacci_value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFibonacci_value() {
        return fibonacci_value;
    }

    public void setFibonacci_value(Long fibonacci_value) {
        this.fibonacci_value = fibonacci_value;
    }
}
