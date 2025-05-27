package com.fiados_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DashboardEstadisticasDTO {
    private long totalClientes;
    private long totalFacturas;
    private double totalFacturado;
    private long facturasActivas;
    private long facturasInactivas;
    private List<TopClienteDTO> topClientes;
}