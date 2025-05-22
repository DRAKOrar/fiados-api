package com.fiados_api.controller;

import com.fiados_api.dto.DashboardEstadisticasDTO;
import com.fiados_api.dto.TopClienteDTO;
import com.fiados_api.entity.EstadoFactura;
import com.fiados_api.repository.ClienteRepository;
import com.fiados_api.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ClienteRepository clienteRepository;
    private final FacturaRepository facturaRepository;

    @GetMapping("/estadisticas")
    public DashboardEstadisticasDTO obtenerEstadisticas() {
        long totalClientes = clienteRepository.count();
        long totalFacturas = facturaRepository.count();
        double totalFacturado = facturaRepository.findAll()
                .stream().mapToDouble(f -> f.getTotal() != null ? f.getTotal() : 0).sum();
        long facturasActivas = facturaRepository.countByEstado(EstadoFactura.ACTIVA);
        long facturasInactivas = facturaRepository.countByEstado(EstadoFactura.INACTIVA);

        // Top 5 clientes por total facturado
        List<TopClienteDTO> topClientes = facturaRepository.findAll().stream()
                .collect(Collectors.groupingBy(f -> f.getCliente().getNombre(),
                        Collectors.summingDouble(f -> f.getTotal() != null ? f.getTotal() : 0)))
                .entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(e -> new TopClienteDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new DashboardEstadisticasDTO(totalClientes, totalFacturas, totalFacturado, facturasActivas, facturasInactivas, topClientes);
    }
}
