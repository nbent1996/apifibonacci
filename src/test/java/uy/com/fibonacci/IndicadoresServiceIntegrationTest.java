package uy.com.fibonacci;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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
		Page<IndicadoresModel> expectedPage = Mockito.mock(Page.class);
		Mockito.when(indicadoresRepository.findAll()).thenReturn(expectedPage);
		Page<IndicadoresModel> actualPage = indicadoresService.obtenerIndicadores();
		assertEquals(expectedPage, actualPage);
	}
 
    @Test
    public void testGuardarIndicador() {
        IndicadoresModel newIndicador = new IndicadoresModel();
        IndicadoresModel savedIndicador = new IndicadoresModel();
        Mockito.when(indicadoresRepository.save(newIndicador)).thenReturn(savedIndicador);
        IndicadoresModel returnedIndicador = indicadoresService.guardarIndicador(newIndicador);
        assertEquals(savedIndicador, returnedIndicador);
        Mockito.verify(indicadoresRepository).save(newIndicador);
    }

    @Test
    public void testAumentarIndicador() throws Exception {
        ResultadoModel resultado = new ResultadoModel();
        resultado.setPosition(1L);
        Optional<IndicadoresModel> existingIndicador = Optional.of(new IndicadoresModel());
        existingIndicador.get().setRequestCount(0L);
        Mockito.when(indicadoresRepository.getIndicadorByResultadoPosition(resultado.getPosition())).thenReturn(existingIndicador);
        ResponseEntity<IndicadoresModel> response = indicadoresService.aumentarIndicador(resultado);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        IndicadoresModel returnedIndicador = response.getBody();
        assertEquals(1L, returnedIndicador.getRequestCount());
        Mockito.verify(indicadoresRepository).save(existingIndicador.get());
    }

	@Test
    public void testTopRequestByOrderDesc() {
        ArrayList<IndicadoresModel> expectedList = new ArrayList<>();
        expectedList.add(new IndicadoresModel());
        Mockito.when(indicadoresRepository.findTopRequestByOrderDesc()).thenReturn(expectedList);

        ArrayList<IndicadoresModel> actualList = indicadoresService.TopRequestByOrderDesc();
        assertEquals(expectedList, actualList);
    }

	 @Test
    public void testDeleteAll() {
        indicadoresService.deleteAll();
        Mockito.verify(indicadoresRepository, times(1)).deleteAll();
 
}
@Test
    public void testObtenerIndicadorPorPositionResultado() {
        Long position = 1L;
        IndicadoresModel expectedIndicador = new IndicadoresModel();
        Mockito.when(indicadoresRepository.getIndicadorByResultadoPosition(position))
               .thenReturn(Optional.of(expectedIndicador));

        ResponseEntity<IndicadoresModel> response = 
                indicadoresService.obtenerIndicadorPorPositionResultado(position);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedIndicador, response.getBody());
    }
 @Test
    public void testAumentarIndicador_NuevoIndicador() throws Exception {
        ResultadoModel resultado = new ResultadoModel();
        resultado.setPosition(1L);
        Mockito.when(indicadoresRepository.getIndicadorByResultadoPosition(resultado.getPosition()))
               .thenReturn(Optional.empty());

        IndicadoresModel nuevoIndicador = new IndicadoresModel();
        nuevoIndicador.setRequestCount(1L);
        Mockito.when(indicadoresRepository.save(any(IndicadoresModel.class))).thenReturn(nuevoIndicador);

        ResponseEntity<IndicadoresModel> response = indicadoresService.aumentarIndicador(resultado);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getRequestCount());
    }

	@Test
public void testAumentarIndicador_ExceptionHandling() {
    ResultadoModel resultado = new ResultadoModel();
    resultado.setPosition(1L);

    try {
        // Simula que obtener un indicador lanza una excepción
        Mockito.doThrow(new RuntimeException("Simulated exception")).when(indicadoresRepository).getIndicadorByResultadoPosition(resultado.getPosition());
        
        // Llamar al método que se prueba
        ResponseEntity<IndicadoresModel> response = indicadoresService.aumentarIndicador(resultado);

        // Verificar el comportamiento esperado
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // Verificar que el repositorio fue llamado y causó una excepción
        verify(indicadoresRepository, times(1)).getIndicadorByResultadoPosition(resultado.getPosition());
        
        // Aquí podrías verificar los logs si tienes una herramienta específica para eso
        // Generalmente no se verifica en tests estándar
    } catch (Exception e) {
        fail("Exception should not have been thrown during the test: " + e.getMessage());
    }
}
}
