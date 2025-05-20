package com.deu.java.backend.Bus.service;

import com.deu.java.backend.Bus.dto.BusDTO;
import com.deu.java.backend.entity.BusEntity;
import com.deu.java.backend.Bus.repository.BusRepository;
import java.util.List;
import java.util.stream.Collectors;

public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;

    public BusServiceImpl(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    @Override
    public BusDTO getRealTimeBusInfo(long routeId) {
        List<BusEntity> entities = busRepository.findByRouteId(routeId);
        BusDTO dto = new BusDTO();
        dto.setRouteId(routeId);        
        dto.setBuses(entities.stream()
            .map(b -> new BusDTO.BusInfo(
                    b.getBusId(), b.getPlateNumber(), b.getCurrentStationId(),
                    b.getNextStationId(), b.getStatus()))
            .collect(Collectors.toList()));
        
        return dto;
    }
}

