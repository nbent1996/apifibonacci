package uy.com.fibonacci;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import uy.com.fibonacci.dto.ResultadosDTO;
import uy.com.fibonacci.services.ResultadoService;
import uy.com.fibonacci.controllers.ResultadoController;
import uy.com.fibonacci.models.ResultadoModel;

@WebMvcTest(ResultadoController.class)
public class ResultadoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResultadoService resultadoService;

    @Test
    public void testObtenerResultados() throws Exception {
        ArrayList<ResultadosDTO> lista = new ArrayList<>();

        when(resultadoService.obtenerResultados()).thenReturn(lista);

        mockMvc.perform(get("/resultado/obtenerTodos"))
                .andExpect(status().isOk());
    }

    @Test
    public void testLimpiarCache() throws Exception {
        doNothing().when(resultadoService).limpiarCache();

        mockMvc.perform(get("/resultado/limpiarCache"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFibonacci() throws Exception {
        ResultadosDTO resultadoDTO = new ResultadosDTO();
        resultadoDTO.setId(1L);
        resultadoDTO.setPosition(5L);
        resultadoDTO.setFibonacciValue(8L);

        ResponseEntity<ResultadosDTO> responseEntity = ResponseEntity.of(Optional.of(resultadoDTO));

        when(resultadoService.getFibonacciValue(anyLong())).thenReturn(responseEntity);

        mockMvc.perform(get("/resultado/getFibonacci/5"))
                .andExpect(status().isOk());
    }
}