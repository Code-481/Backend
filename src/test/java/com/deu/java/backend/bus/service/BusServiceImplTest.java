package com.deu.java.backend.bus.service;


import com.deu.java.backend.Bus.dto.BusDTO;
import com.deu.java.backend.Bus.entity.BusEntity;
import com.deu.java.backend.Bus.repository.BusRepository;
import com.deu.java.backend.Bus.service.BusServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusServiceImplTest {

    private BusRepository busRepository;
    private BusServiceImpl busService;

    @BeforeEach
    void setUp() {
        busRepository = mock(BusRepository.class);
        busService = new BusServiceImpl(busRepository);
    }

    @Test
    void testGetRealTimeBusInfo() {
        // given
        long routeId = 123L;

        BusEntity bus1 = new BusEntity();
        bus1.setBusId("BUS001");
        bus1.setPlateNumber("12가 3456");
        bus1.setCurrentStationId("ST001");
        bus1.setNextStationId("ST002");
        bus1.setStatus("운행중");

        BusEntity bus2 = new BusEntity();
        bus2.setBusId("BUS002");
        bus2.setPlateNumber("가 3456");
        bus2.setCurrentStationId("ST002");
        bus2.setNextStationId("ST003");
        bus2.setStatus("대기");
        
        List<BusEntity> mockData = Arrays.asList(bus1, bus2);

        when(busRepository.findByRouteId(routeId)).thenReturn(mockData);

        // when
        BusDTO result = busService.getRealTimeBusInfo(routeId);

        // then
        assertEquals(routeId, result.getRouteId());
        assertEquals(2, result.getBuses().size());

        BusDTO.BusInfo info1 = result.getBuses().get(0);
        assertEquals("BUS001", info1.getBusId());
        assertEquals("12가 3456", info1.getPlateNumber());
        assertEquals("ST001", info1.getCurrentStationId());
        assertEquals("ST002", info1.getNextStationId());
        assertEquals("운행중", info1.getStatus());

        BusDTO.BusInfo info2 = result.getBuses().get(1);
        assertEquals("BUS002", info2.getBusId());
        assertEquals("가 3456", info2.getPlateNumber());
        assertEquals("ST002", info2.getCurrentStationId());
        assertEquals("ST003", info2.getNextStationId());
        assertEquals("대기", info2.getStatus());

        verify(busRepository, times(1)).findByRouteId(routeId);
    }
}
