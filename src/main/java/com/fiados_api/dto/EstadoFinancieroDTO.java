package com.fiados_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstadoFinancieroDTO {
    private double totalFacturado;
    private double totalPagado;
    private double totalPendiente;
    private long facturasActivas;
    private long facturasInactivas;
}
