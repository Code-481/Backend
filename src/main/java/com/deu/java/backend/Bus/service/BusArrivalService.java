package com.deu.java.backend.Bus.service;

import com.deu.java.backend.Bus.dto.BusArrivalDto;
import java.util.List;

public interface BusArrivalService {
    public List<BusArrivalDto> getBusArrivalsByStopId(String routeId);
}