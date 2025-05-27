package com.fiados_api.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopClienteDTO {
    private String nombreCliente;
    private double totalFacturado;
}