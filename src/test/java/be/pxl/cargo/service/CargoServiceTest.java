package be.pxl.cargo.service;

import be.pxl.cargo.api.request.CreateCargoRequest;
import be.pxl.cargo.api.response.CargoStatistics;
import be.pxl.cargo.domain.*;
import be.pxl.cargo.exceptions.NonUniqueCodeException;
import be.pxl.cargo.repository.CargoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargoServiceTest {
    @Mock
    private CargoRepository cargoRepository;

    @InjectMocks
    private CargoService cargoService;

    //Succes Case
    @Test
    public void createCargo_shouldSaveCargo_whenCodeIsUnique(){
        CreateCargoRequest createCargoRequest = new CreateCargoRequest("CARGO-1", 100, Location.AIRPORT_X, Location.WAREHOUSE_A);

        when(cargoRepository.findCargoByCode("CARGO-1"))
                .thenReturn(Optional.empty());

        cargoService.createCargo(createCargoRequest);

        verify(cargoRepository).findCargoByCode("CARGO-1");
        verify(cargoRepository).save(any(Cargo.class));
    }

    //Exception Case
    @Test
    public void createCargo_shouldThrowException_whenCodeExists() {
        CreateCargoRequest cargoRequest = new CreateCargoRequest("CARGO-1", 100, Location.AIRPORT_X, Location.WAREHOUSE_A);

        when(cargoRepository.findCargoByCode("CARGO-1"))
                .thenReturn(Optional.of(new Cargo()));

        assertThrows(NonUniqueCodeException.class, () -> cargoService.createCargo(cargoRequest));

        verify(cargoRepository, times(1)).findCargoByCode("CARGO-1");
        verify(cargoRepository, never()).save(any());
    }

    //Main Test
    @Test
    public void getCargoStatistics_shouldReturnCorrectStatistics(){
        Cargo c1 = new Cargo("C1", 100, Location.AIRPORT_X, Location.WAREHOUSE_A);
        Cargo c2 = new Cargo("C2", 200, Location.CITY_B, Location.CITY_B);
        Cargo c3 = new Cargo("C3", 50, Location.CITY_B, Location.WAREHOUSE_A);

        c2.setCargoStatus(CargoStatus.DELIVERED);
        c2.arrive(Location.CITY_B);
        c1.arrive(Location.WAREHOUSE_A);

        when(cargoRepository.findAll())
                .thenReturn(List.of(c1, c2, c3));

        CargoStatistics stats = cargoService.getCargoStatistics();

        assertEquals("C2", stats.getHeaviestCargo());
        assertEquals(116.666, stats.getAverageCargoWeight(), 0.01);
        assertEquals(1, stats.getCountCargosAtWarehouseA());
        assertEquals(200, stats.getTotalWeightDeliveredAtCityB(), 0.01);

        verify(cargoRepository).findAll();

    }
}
