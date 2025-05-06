package com.deu.java.backend.controller;

import com.deu.java.backend.Bus.controller.BusController;
import com.deu.java.backend.Bus.dto.BusDTO;
import com.deu.java.backend.Bus.service.BusService;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class BusControllerTest {

    private BusService mockBusService;
    private Context mockCtx;
    private BusController controller;

    @BeforeEach
    void setup() {
        mockBusService = mock(BusService.class);
        mockCtx = mock(Context.class);
        controller = new BusController(mockBusService);
    }

    @Test
    void testHandleGetBusInfo_validRouteId() {
        // given
        when(mockCtx.queryParam("routeId")).thenReturn("6");
        BusDTO dummyDto = new BusDTO(6L, Collections.emptyList());
        when(mockBusService.getRealTimeBusInfo(6L)).thenReturn(dummyDto);

        // when
        controller.handleGetBusInfo(mockCtx);

        // then
        verify(mockCtx).json(dummyDto);
        verify(mockCtx, never()).status(400);
    }

    @Test
    void testHandleGetBusInfo_missingRouteId() {
        // given
        when(mockCtx.queryParam("routeId")).thenReturn(null);

        // when
        controller.handleGetBusInfo(mockCtx);

        // then
        verify(mockCtx).status(400);
        verify(mockCtx).json(any(BusController.Error.class));
        verify(mockCtx, never()).json(isA(BusDTO.class));
    }

    @Test
    void testHandleGetBusInfo_emptyRouteId() {
        
        when(mockCtx.queryParam("routeId")).thenReturn("");
        when(mockCtx.status(400)).thenReturn(mockCtx);

        controller.handleGetBusInfo(mockCtx);

        verify(mockCtx).status(400);
        verify(mockCtx).json(any(BusController.Error.class));
        verify(mockCtx, never()).json(isA(BusDTO.class));
    }
}