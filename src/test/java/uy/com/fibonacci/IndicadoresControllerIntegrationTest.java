package uy.com.fibonacci;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.*;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

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


import uy.com.fibonacci.controllers.IndicadoresController;
import uy.com.fibonacci.models.IndicadoresModel;
import uy.com.fibonacci.services.IndicadoresService;


@WebMvcTest(IndicadoresController.class)
public class IndicadoresControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IndicadoresService indicadoresService;

    @Test
    public void testObtenerIndicadores() throws Exception {
        Pageable pageable = PageRequest.of(0, 50);
        Page<IndicadoresModel> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(indicadoresService.obtenerIndicadores()).thenReturn(page);

        mockMvc.perform(get("/indicador/obtenerTodos"))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetIndicador() throws Exception {
        IndicadoresModel indicadoresModel = new IndicadoresModel();
        ResponseEntity<IndicadoresModel> responseEntity = ResponseEntity.of(Optional.of(indicadoresModel));

        when(indicadoresService.obtenerIndicadorPorPositionResultado(anyLong())).thenReturn(responseEntity);

        mockMvc.perform(get("/indicador/getIndicador/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testTopRequestByOrderDesc() throws Exception {
        ArrayList<IndicadoresModel> indicadoresModels = new ArrayList<>();

        when(indicadoresService.TopRequestByOrderDesc()).thenReturn(indicadoresModels);

        mockMvc.perform(get("/indicador/mejoresIndicadores"))
                .andExpect(status().isOk());
    }
}
   