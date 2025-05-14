package com.deu.java.backend.Bus.service;

import com.deu.java.backend.Bus.dto.BusDTO;

public interface BusService {
    public BusDTO getRealTimeBusInfo(long parseLong);
}