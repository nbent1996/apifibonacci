package uy.com.fibonacci;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import uy.com.fibonacci.models.IndicadoresModel;
import uy.com.fibonacci.models.ResultadoModel;
import uy.com.fibonacci.repositories.ResultadoRepository;
import uy.com.fibonacci.services.IndicadoresService;
import uy.com.fibonacci.services.ResultadoService;

@SpringBootTest
class ResultadoServiceIntegrationTest {

    @Autowired
    private ResultadoService resultadoService;

    @MockitoBean
    private ResultadoRepository resultadoRepository;

    @MockitoBean
    private IndicadoresService indicadoresService;

	@Test
	void contextLoads() {
	}


    @Test
    public void testCalcularFibonacci_WithCachedResult() throws Exception {
        Long position = 10L;  // La posición que está siendo probada
        Long fibValueAtLastCached = 34L; // Ejemplo de valor de Fibonacci
        Long prevFibValue = 21L; // Fibonacci anterior al `lastCachedResult`
    
        // Simular el resultado almacenado en caché
        ResultadoModel lastCachedResult = new ResultadoModel();
        lastCachedResult.setPosition(9L);
        lastCachedResult.setFibonacci_value(fibValueAtLastCached);
        when(resultadoRepository.findMaxPositionWithLimit(position)).thenReturn(Optional.of(lastCachedResult));
    
        // Simular el resultado anterior al almacenado en caché
        ResultadoModel previousResult = new ResultadoModel();
        previousResult.setPosition(8L);
        previousResult.setFibonacci_value(prevFibValue);
        when(resultadoRepository.findByPosition(8L)).thenReturn(Optional.of(previousResult));
    
        // Simular salvado
        when(resultadoRepository.save(any(ResultadoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
    
        // Actuar: Ejecutar el método para calcular Fibonacci
        Optional<ResultadoModel> result = resultadoService.calcularFibonacci(position);
    
        // Aserciones
        assertTrue(result.isPresent());
        assertEquals(position, result.get().getPosition());
        assertEquals(Long.valueOf(55L), result.get().getFibonacci_value()); // 34 (previo) + 21 (anterior) == 55
    
        // Verificaciones de invocación del mock
        verify(resultadoRepository, times(1)).findMaxPositionWithLimit(position);
        verify(resultadoRepository, times(1)).findByPosition(8L);
        verify(resultadoRepository, atLeastOnce()).save(any(ResultadoModel.class));
    }

	@Test
	public void testObtenerResultado() {
		ArrayList<ResultadoModel> expectedList = new ArrayList<>();
		Mockito.when(resultadoRepository.find50Resultado()).thenReturn(expectedList);
		List<ResultadoModel> actualList = resultadoService.obtenerResultados();
		assertEquals(expectedList, actualList);
	}
    @Test
    public void testCalcularFibonacci_ExistingCache() throws Exception {
        Long position = 5L;
        ResultadoModel cachedResult = new ResultadoModel(position, 5L);
        when(resultadoRepository.findByPosition(position)).thenReturn(Optional.of(cachedResult));

        Optional<ResultadoModel> result = resultadoService.calcularFibonacci(position);

        assertTrue(result.isPresent());
        assertEquals(cachedResult, result.get());
        verify(resultadoRepository, never()).save(any(ResultadoModel.class));
    }

    @Test
    public void testCalcularFibonacci_NoCache() throws Exception {
        Long position = 6L;
        when(resultadoRepository.findByPosition(position)).thenReturn(Optional.empty());
        when(resultadoRepository.findMaxPositionWithLimit(position)).thenReturn(Optional.empty());

        // Configurar mock para save
        when(resultadoRepository.save(any(ResultadoModel.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        ResultadoModel expectedCalculated = new ResultadoModel(position, 8L); // Ajustar valor esperado
        Optional<ResultadoModel> result = resultadoService.calcularFibonacci(position);

        assertTrue(result.isPresent());
        assertEquals(13L, result.get().getFibonacci_value());
        verify(resultadoRepository, atLeastOnce()).save(any(ResultadoModel.class));
    }

    @Test()
    public void testGetFibonacciValue_EntradaNegativa() throws Exception {
        assertThrows(Exception.class, () -> resultadoService.getFibonacciValue(-1L));
    }
    
    @Test
    public void testGuardarResultado() throws Exception {
        // Crear y configurar el modelo simulado
        ResultadoModel resultado = new ResultadoModel();
        resultado.setFibonacci_value(121393L);
        resultado.setPosition(25L);

        // Configurar el comportamiento del mock del repositorio
        when(resultadoRepository.save(any(ResultadoModel.class))).thenReturn(resultado);
        when(resultadoRepository.findByPosition(25L)).thenReturn(Optional.of(resultado));

        // Ejecuta el método que queremos probar
        Optional<ResultadoModel> persistedResult = resultadoService.persistirResultado(25L, 121393L, 25);
        
        // Asegúrate de que se haya persistido un resultado
        assertTrue(persistedResult.isPresent());
        ResultadoModel savedResultado = persistedResult.get();

        // Verificar que los resultados son correctos
        assertNotNull(savedResultado);
        assertEquals(121393L, savedResultado.getFibonacci_value());
        assertEquals(25L, savedResultado.getPosition());
    }

    @Test
    public void testGetFibonacciValue_resultadoExistente() throws Exception {
        Long position = 5L;
        Long fibonacciValue = 5L;
        ResultadoModel existingResult = new ResultadoModel(position, fibonacciValue);
        when(resultadoRepository.findByPosition(position)).thenReturn(Optional.of(existingResult));

        ResponseEntity<ResultadoModel> response = resultadoService.getFibonacciValue(position);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(position, response.getBody().getPosition());
        assertEquals(fibonacciValue, response.getBody().getFibonacci_value());

        verify(resultadoRepository, never()).save(any(ResultadoModel.class));
        verify(indicadoresService).aumentarIndicador(existingResult); 
    }
    @Test
    public void testLimpiarCache() {
        // Llamar al método
        resultadoService.limpiarCache();

        // Verificar que se llaman a deleteAll en ambos servicios
        verify(indicadoresService, times(1)).deleteAll();
        verify(resultadoRepository, times(1)).deleteAll();
    }
    @Test
    public void testGetFibonacciValue_CalculoNuevo() throws Exception {
        Long position = 6L; // La posición que estás probando
        when(resultadoRepository.findByPosition(position)).thenReturn(Optional.empty());
    
        // Simulamos un resultado calculado
        ResultadoModel calculatedResult = new ResultadoModel(position, 8L);
        when(resultadoRepository.save(any(ResultadoModel.class))).thenReturn(calculatedResult);
    
        // Simular que el cálculo intermedio desde la caché está disponible
        when(resultadoRepository.findByPosition(5L)).thenReturn(Optional.of(new ResultadoModel(5L, 5L)));
    
        ResponseEntity<ResultadoModel> response = resultadoService.getFibonacciValue(position);
    
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(position, response.getBody().getPosition());
        assertEquals(Long.valueOf(8L), response.getBody().getFibonacci_value());
    
        // Ajusta verificar la cantidad de veces que save es llamado
        verify(resultadoRepository, times(3)).save(any(ResultadoModel.class));
    }

    @Test
public void testGetFibonacciValue_ExceptionHandling() {
    Long position = 10L;

    try {
        when(resultadoRepository.findByPosition(position)).thenReturn(Optional.empty());
        when(resultadoRepository.findMaxPositionWithLimit(position)).thenReturn(Optional.empty());
        // Simulando una excepción durante el cálculo de Fibonacci
        doThrow(new RuntimeException("Simulated exception")).when(resultadoRepository).save(any(ResultadoModel.class));
        
        ResponseEntity<ResultadoModel> response = resultadoService.getFibonacciValue(position);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    
    } catch (Exception e) {
        fail("No deberia haberse lanzado una excepcion durante esta prueba: " + e.getMessage());
    }
}

}

