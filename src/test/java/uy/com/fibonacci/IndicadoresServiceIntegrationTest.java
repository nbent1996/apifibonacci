package uy.com.fibonacci;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import uy.com.fibonacci.dto.IndicadoresDTO;
import uy.com.fibonacci.models.IndicadoresModel;
import uy.com.fibonacci.models.ResultadoModel;
import uy.com.fibonacci.repositories.IndicadoresRepository;
import uy.com.fibonacci.services.IndicadoresService;

@SpringBootTest
public class IndicadoresServiceIntegrationTest {

    @Autowired
    private IndicadoresService indicadoresService;

    @MockitoBean
    private IndicadoresRepository indicadoresRepository;

    @Test
    public void testObtenerIndicadores() {

        List<IndicadoresModel> listaIndicadores = new ArrayList<>();

        ResultadoModel resultado = new ResultadoModel();
        resultado.setId(1L);
        resultado.setPosition(5L);
        resultado.setFibonacci_value(8L);

        IndicadoresModel indicador = new IndicadoresModel();
        indicador.setId(1L);
        indicador.setResultado(resultado);
        indicador.setRequestCount(3L);

        listaIndicadores.add(indicador);

        Mockito.when(indicadoresRepository.findAll()).thenReturn(listaIndicadores);

        List<IndicadoresDTO> actualList = indicadoresService.obtenerIndicadores();

        assertNotNull(actualList);
        assertEquals(1, actualList.size());

        IndicadoresDTO dto = actualList.get(0);

        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getResultadoId());
        assertEquals(5L, dto.getNumeroConsultado());
        assertEquals(8L, dto.getValorResultado());
        assertEquals(3L, dto.getRequestCount());

        verify(indicadoresRepository, times(1)).findAll();
    }

    @Test
    public void testGuardarIndicador() {

        IndicadoresModel newIndicador = new IndicadoresModel();

        IndicadoresModel savedIndicador = new IndicadoresModel();
        savedIndicador.setId(1L);
        savedIndicador.setRequestCount(1L);

        Mockito.when(indicadoresRepository.save(newIndicador)).thenReturn(savedIndicador);

        IndicadoresDTO returnedIndicador = indicadoresService.guardarIndicador(newIndicador);

        assertNotNull(returnedIndicador);
        assertEquals(savedIndicador, returnedIndicador);

        verify(indicadoresRepository, times(1)).save(newIndicador);
    }

    @Test
    public void testAumentarIndicador_Existente() throws Exception {

        ResultadoModel resultado = new ResultadoModel();
        resultado.setId(1L);
        resultado.setPosition(1L);
        resultado.setFibonacci_value(1L);

        IndicadoresModel existingIndicador = new IndicadoresModel();
        existingIndicador.setId(1L);
        existingIndicador.setResultado(resultado);
        existingIndicador.setRequestCount(0L);

        Mockito.when(indicadoresRepository.getIndicadorByResultadoPosition(resultado.getPosition()))
                .thenReturn(Optional.of(existingIndicador));

        Mockito.when(indicadoresRepository.save(existingIndicador))
                .thenReturn(existingIndicador);

        ResponseEntity<IndicadoresDTO> response = indicadoresService.aumentarIndicador(resultado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        IndicadoresDTO dto = response.getBody();

        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getRequestCount());
        assertEquals(resultado.getId(), dto.getResultadoId());
        assertEquals(resultado.getPosition(), dto.getNumeroConsultado());
        assertEquals(resultado.getFibonacci_value(), dto.getValorResultado());

        verify(indicadoresRepository, times(1))
                .getIndicadorByResultadoPosition(resultado.getPosition());

        verify(indicadoresRepository, times(1))
                .save(existingIndicador);
    }

    @Test
    public void testTopRequestByOrderDesc() {

        ArrayList<IndicadoresModel> listaIndicadores = new ArrayList<>();

        ResultadoModel resultado = new ResultadoModel();
        resultado.setId(1L);
        resultado.setPosition(6L);
        resultado.setFibonacci_value(13L);

        IndicadoresModel indicador = new IndicadoresModel();
        indicador.setId(1L);
        indicador.setResultado(resultado);
        indicador.setRequestCount(10L);

        listaIndicadores.add(indicador);

        Mockito.when(indicadoresRepository.findTopRequestByOrderDesc())
                .thenReturn(listaIndicadores);

        List<IndicadoresDTO> actualList = indicadoresService.TopRequestByOrderDesc();

        assertNotNull(actualList);
        assertEquals(1, actualList.size());

        IndicadoresDTO dto = actualList.get(0);

        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getResultadoId());
        assertEquals(6L, dto.getNumeroConsultado());
        assertEquals(13L, dto.getValorResultado());
        assertEquals(10L, dto.getRequestCount());

        verify(indicadoresRepository, times(1)).findTopRequestByOrderDesc();
    }

    @Test
    public void testDeleteAll() {

        indicadoresService.deleteAll();

        verify(indicadoresRepository, times(1)).deleteAll();
    }

    @Test
    public void testObtenerIndicadorPorPositionResultado() throws Exception {

        Long position = 1L;

        ResultadoModel resultado = new ResultadoModel();
        resultado.setId(1L);
        resultado.setPosition(position);
        resultado.setFibonacci_value(1L);

        IndicadoresModel expectedIndicador = new IndicadoresModel();
        expectedIndicador.setId(1L);
        expectedIndicador.setResultado(resultado);
        expectedIndicador.setRequestCount(5L);

        Mockito.when(indicadoresRepository.getIndicadorByResultadoPosition(position))
                .thenReturn(Optional.of(expectedIndicador));

        ResponseEntity<IndicadoresDTO> response =
                indicadoresService.obtenerIndicadorPorPositionResultado(position);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        IndicadoresDTO dto = response.getBody();

        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getResultadoId());
        assertEquals(position, dto.getNumeroConsultado());
        assertEquals(1L, dto.getValorResultado());
        assertEquals(5L, dto.getRequestCount());

        verify(indicadoresRepository, times(1))
                .getIndicadorByResultadoPosition(position);
    }

    @Test
    public void testAumentarIndicador_NuevoIndicador() throws Exception {

        ResultadoModel resultado = new ResultadoModel();
        resultado.setId(1L);
        resultado.setPosition(1L);
        resultado.setFibonacci_value(1L);

        Mockito.when(indicadoresRepository.getIndicadorByResultadoPosition(resultado.getPosition()))
                .thenReturn(Optional.empty());

        IndicadoresModel nuevoIndicador = new IndicadoresModel();
        nuevoIndicador.setId(1L);
        nuevoIndicador.setResultado(resultado);
        nuevoIndicador.setRequestCount(1L);

        Mockito.when(indicadoresRepository.save(any(IndicadoresModel.class)))
                .thenReturn(nuevoIndicador);

        ResponseEntity<IndicadoresDTO> response = indicadoresService.aumentarIndicador(resultado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        IndicadoresDTO dto = response.getBody();

        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getRequestCount());
        assertEquals(resultado.getId(), dto.getResultadoId());
        assertEquals(resultado.getPosition(), dto.getNumeroConsultado());
        assertEquals(resultado.getFibonacci_value(), dto.getValorResultado());

        verify(indicadoresRepository, times(1))
                .getIndicadorByResultadoPosition(resultado.getPosition());

        verify(indicadoresRepository, times(1))
                .save(any(IndicadoresModel.class));
    }

    @Test
    public void testAumentarIndicador_ExceptionHandling() {

        ResultadoModel resultado = new ResultadoModel();
        resultado.setPosition(1L);

        try {
            Mockito.doThrow(new RuntimeException("Simulated exception"))
                    .when(indicadoresRepository)
                    .getIndicadorByResultadoPosition(resultado.getPosition());

            ResponseEntity<IndicadoresDTO> response = indicadoresService.aumentarIndicador(resultado);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

            verify(indicadoresRepository, times(1))
                    .getIndicadorByResultadoPosition(resultado.getPosition());

        } catch (Exception e) {
            fail("Exception should not have been thrown during the test: " + e.getMessage());
        }
    }
}