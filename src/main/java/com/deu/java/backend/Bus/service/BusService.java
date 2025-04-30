/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deu.java.backend.Bus.service;

import com.deu.java.backend.Bus.dto.BusDTO;
import java.util.List;

public interface BusService {
    public BusDTO getRealTimeBusInfo(long parseLong);
}