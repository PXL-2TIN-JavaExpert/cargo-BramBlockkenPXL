package be.pxl.cargo.api;

import be.pxl.cargo.api.request.CreateCargoRequest;
import be.pxl.cargo.api.response.CargoStatistics;
import be.pxl.cargo.domain.Location;
import be.pxl.cargo.service.CargoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(CargoController.class)
public class CargoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CargoService cargoService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void addCargo_shouldReturn201() throws Exception{
        CreateCargoRequest createCargoRequest = new CreateCargoRequest("CARGO-1", 100, Location.AIRPORT_X, Location.WAREHOUSE_A);

        mockMvc.perform(MockMvcRequestBuilders.post("/cargos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCargoRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        Mockito.verify(cargoService, Mockito.times(1))
                .createCargo(ArgumentMatchers.any(CreateCargoRequest.class));
    }

    @Test
    public void addCargoValidationFailTest() throws Exception{
        CreateCargoRequest request = new CreateCargoRequest("", 0, null, null);

        mockMvc.perform(MockMvcRequestBuilders.post("/cargos")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verifyNoInteractions(cargoService);
    }

    @Test
    public void getCargoStatisticsTest() throws Exception{
        CargoStatistics stats = new CargoStatistics();

        Mockito.when(cargoService.getCargoStatistics()).thenReturn(stats);
        mockMvc.perform(MockMvcRequestBuilders.get("/cargos/statistics"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(cargoService, Mockito.times(1)).getCargoStatistics();
    }
}
