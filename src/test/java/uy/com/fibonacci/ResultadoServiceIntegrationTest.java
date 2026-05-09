package uy.com.fibonacci;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import uy.com.fibonacci.dto.ResultadosDTO;
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
        Long position = 10L;
        // F1 = 1
        // F2 = 2
        // F3 = 3
        // F4 = 5
        // F5 = 8
        // F6 = 13
        // F7 = 21
        // F8 = 34
        // F9 = 55
        // F10 = 89

        ResultadoModel lastCachedResult = new ResultadoModel();
        lastCachedResult.setPosition(9L);
        lastCachedResult.setFibonacci_value(55L);

        ResultadoModel previousResult = new ResultadoModel();
        previousResult.setPosition(8L);
        previousResult.setFibonacci_value(34L);

        when(resultadoRepository.findByPosition(position)).thenReturn(Optional.empty());
        when(resultadoRepository.findMaxPositionWithLimit(position)).thenReturn(Optional.of(lastCachedResult));
        when(resultadoRepository.findByPosition(8L)).thenReturn(Optional.of(previousResult));

        when(resultadoRepository.save(any(ResultadoModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Optional<ResultadosDTO> result = resultadoService.calcularFibonacci(position);

        assertTrue(result.isPresent());
        assertEquals(position, result.get().getPosition());
        assertEquals(Long.valueOf(89L), result.get().getFibonacciValue());

        verify(resultadoRepository, times(1)).findMaxPositionWithLimit(position);
        verify(resultadoRepository, times(1)).findByPosition(8L);
        verify(resultadoRepository, atLeastOnce()).save(any(ResultadoModel.class));
    }

    @Test
    public void testObtenerResultado() {
        ArrayList<ResultadoModel> expectedList = new ArrayList<>();

        ResultadoModel resultado1 = new ResultadoModel();
        resultado1.setId(1L);
        resultado1.setPosition(1L);
        resultado1.setFibonacci_value(1L);

        ResultadoModel resultado2 = new ResultadoModel();
        resultado2.setId(2L);
        resultado2.setPosition(2L);
        resultado2.setFibonacci_value(2L);

        expectedList.add(resultado1);
        expectedList.add(resultado2);

        Mockito.when(resultadoRepository.find50Resultado()).thenReturn(expectedList);

        List<ResultadosDTO> actualList = resultadoService.obtenerResultados();

        assertNotNull(actualList);
        assertEquals(2, actualList.size());

        assertEquals(1L, actualList.get(0).getPosition());
        assertEquals(1L, actualList.get(0).getFibonacciValue());

        assertEquals(2L, actualList.get(1).getPosition());
        assertEquals(2L, actualList.get(1).getFibonacciValue());
    }

    @Test
    public void testCalcularFibonacci_ExistingCache() throws Exception {
        Long position = 5L;

        ResultadoModel cachedResult = new ResultadoModel();
        cachedResult.setId(1L);
        cachedResult.setPosition(position);
        cachedResult.setFibonacci_value(8L);

        when(resultadoRepository.findByPosition(position)).thenReturn(Optional.of(cachedResult));

        Optional<ResultadosDTO> result = resultadoService.calcularFibonacci(position);

        assertTrue(result.isPresent());
        assertEquals(position, result.get().getPosition());
        assertEquals(Long.valueOf(8L), result.get().getFibonacciValue());

        verify(resultadoRepository, never()).save(any(ResultadoModel.class));
    }

    @Test
    public void testCalcularFibonacci_NoCache() throws Exception {
        Long position = 6L;

        when(resultadoRepository.findByPosition(anyLong())).thenReturn(Optional.empty());
        when(resultadoRepository.findMaxPositionWithLimit(position)).thenReturn(Optional.empty());

        when(resultadoRepository.save(any(ResultadoModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Optional<ResultadosDTO> result = resultadoService.calcularFibonacci(position);

        assertTrue(result.isPresent());
        assertEquals(position, result.get().getPosition());
        assertEquals(Long.valueOf(13L), result.get().getFibonacciValue());

        verify(resultadoRepository, atLeastOnce()).save(any(ResultadoModel.class));
    }

    @Test
    public void testGetFibonacciValue_EntradaNegativa() {
        assertThrows(Exception.class, () -> resultadoService.getFibonacciValue(-1L));
    }

    @Test
    public void testGuardarResultado() throws Exception {
        ResultadoModel resultado = new ResultadoModel();
        resultado.setId(1L);
        resultado.setFibonacci_value(121393L);
        resultado.setPosition(25L);

        when(resultadoRepository.findByPosition(25L)).thenReturn(Optional.empty());

        when(resultadoRepository.save(any(ResultadoModel.class)))
                .thenReturn(resultado);

        Optional<ResultadoModel> persistedResult = resultadoService.persistirResultado(25L, 121393L, 25);

        assertTrue(persistedResult.isPresent());

        ResultadoModel savedResultado = persistedResult.get();

        assertNotNull(savedResultado);
        assertEquals(121393L, savedResultado.getFibonacci_value());
        assertEquals(25L, savedResultado.getPosition());

        verify(resultadoRepository, times(1)).save(any(ResultadoModel.class));
        verify(indicadoresService, times(1)).aumentarIndicador(resultado);
    }

    @Test
    public void testGetFibonacciValue_resultadoExistente() throws Exception {
        Long position = 5L;
        Long fibonacciValue = 8L;

        ResultadoModel existingResult = new ResultadoModel();
        existingResult.setId(1L);
        existingResult.setPosition(position);
        existingResult.setFibonacci_value(fibonacciValue);

        when(resultadoRepository.findByPosition(position)).thenReturn(Optional.of(existingResult));

        ResponseEntity<ResultadosDTO> response = resultadoService.getFibonacciValue(position);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(position, response.getBody().getPosition());
        assertEquals(fibonacciValue, response.getBody().getFibonacciValue());

        verify(resultadoRepository, never()).save(any(ResultadoModel.class));
        verify(indicadoresService, times(1)).aumentarIndicador(existingResult);
    }

    @Test
    public void testLimpiarCache() {
        resultadoService.limpiarCache();

        verify(indicadoresService, times(1)).deleteAll();
        verify(resultadoRepository, times(1)).deleteAll();
    }

    @Test
    public void testGetFibonacciValue_CalculoNuevo() throws Exception {
        Long position = 6L;

        when(resultadoRepository.findByPosition(anyLong())).thenReturn(Optional.empty());
        when(resultadoRepository.findMaxPositionWithLimit(position)).thenReturn(Optional.empty());

        when(resultadoRepository.save(any(ResultadoModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<ResultadosDTO> response = resultadoService.getFibonacciValue(position);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(position, response.getBody().getPosition());
        assertEquals(Long.valueOf(13L), response.getBody().getFibonacciValue());

        verify(resultadoRepository, atLeastOnce()).save(any(ResultadoModel.class));
    }

    @Test
    public void testGetFibonacciValue_ExceptionHandling() {
        Long position = 10L;

        try {
            when(resultadoRepository.findByPosition(anyLong())).thenReturn(Optional.empty());
            when(resultadoRepository.findMaxPositionWithLimit(position)).thenReturn(Optional.empty());

            doThrow(new RuntimeException("Simulated exception"))
                    .when(resultadoRepository)
                    .save(any(ResultadoModel.class));

            ResponseEntity<ResultadosDTO> response = resultadoService.getFibonacciValue(position);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        } catch (Exception e) {
            fail("No deberia haberse lanzado una excepcion durante esta prueba: " + e.getMessage());
        }
    }
}
