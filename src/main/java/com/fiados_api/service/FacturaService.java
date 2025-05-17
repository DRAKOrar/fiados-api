package com.fiados_api.service;
import com.fiados_api.entity.Cliente;
import com.fiados_api.entity.EstadoFactura;
import com.fiados_api.entity.Factura;
import com.fiados_api.repository.ClienteRepository;
import com.fiados_api.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacturaService {
    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;

    public Factura crear(Long clienteId, Factura factura) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        factura.setCliente(cliente);
        factura.setFechaCreacion(LocalDate.now());
        factura.setFechaActualizacion(LocalDate.now());
        factura.setEstado(EstadoFactura.ACTIVA);

        factura.getProductos().forEach(p -> p.setFactura(factura));

        double total = factura.getProductos().stream()
                .mapToDouble(p -> p.getCantidad() * p.getPrecioUnitario())
                .sum();
        factura.setTotal(total);

        return facturaRepository.save(factura);
    }

    public Factura actualizar(Long id, Factura nuevosDatos) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        factura.setEstado(nuevosDatos.getEstado());
        factura.setFechaActualizacion(LocalDate.now());

        factura.getProductos().clear();
        nuevosDatos.getProductos().forEach(p -> {
            p.setFactura(factura);
            factura.getProductos().add(p);
        });

        double total = factura.getProductos().stream()
                .mapToDouble(p -> p.getCantidad() * p.getPrecioUnitario())
                .sum();
        factura.setTotal(total);

        return facturaRepository.save(factura);
    }

    public Optional<Factura> obtenerPorId(Long id) {
        return facturaRepository.findById(id);
    }

    public void eliminar(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
        facturaRepository.delete(factura);
    }
}
