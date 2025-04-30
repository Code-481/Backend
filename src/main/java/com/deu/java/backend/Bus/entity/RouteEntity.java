package com.deu.java.backend.Bus.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "busRoute")
public class RouteEntity {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id") 
    private Long routeId; // 노선 식별자
    
    @Column(name = "route_name") // 6-1, 6 9 
    private String routeName;
}