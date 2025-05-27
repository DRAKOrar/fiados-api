package com.fiados_api.controller;

import com.fiados_api.entity.EstadoFactura;
import com.fiados_api.entity.Factura;
import com.fiados_api.repository.ClienteRepository;
import com.fiados_api.repository.FacturaRepository;
import com.fiados_api.service.ReporteFacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/facturas")
@RequiredArgsConstructor
public class FacturaController {
    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final ReporteFacturaService reporteFacturaService;

    @GetMapping
    public ResponseEntity<List<Factura>> listar() {
        return ResponseEntity.ok(facturaRepository.findAll());
    }


    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> obtenerFacturasPorCliente(@PathVariable Long clienteId) {
        return clienteRepository.findById(clienteId)
                .map(cliente -> ResponseEntity.ok(cliente.getFacturas()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{clienteId}")
    public ResponseEntity<Factura> crearFactura(@PathVariable Long clienteId, @RequestBody Factura factura) {
        return clienteRepository.findById(clienteId).map(cliente -> {
            factura.setCliente(cliente);
            factura.setFechaCreacion(LocalDate.now());
            factura.setFechaActualizacion(LocalDate.now());

            // Calcular total
            double total = factura.getProductos().stream()
                    .mapToDouble(p -> p.getCantidad() * p.getPrecioUnitario())
                    .sum();
            factura.setTotal(total);

            // Relacionar productos con la factura
            factura.getProductos().forEach(p -> p.setFactura(factura));

            return ResponseEntity.ok(facturaRepository.save(factura));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> obtener(@PathVariable Long id) {
        return facturaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Factura> actualizar(@PathVariable Long id, @RequestBody Factura datos) {
        return facturaRepository.findById(id).map(factura -> {
            factura.setFechaActualizacion(LocalDate.now());
            factura.setEstado(datos.getEstado());

            factura.getProductos().clear();
            datos.getProductos().forEach(p -> {
                p.setFactura(factura);
                factura.getProductos().add(p);
            });

            double total = factura.getProductos().stream()
                    .mapToDouble(p -> p.getCantidad() * p.getPrecioUnitario())
                    .sum();
            factura.setTotal(total);

            return ResponseEntity.ok(facturaRepository.save(factura));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return facturaRepository.findById(id).map(factura -> {
            if (factura.getEstado() == EstadoFactura.ACTIVA) {
                return ResponseEntity.status(HttpStatus.CONFLICT).<Void>build(); // No se puede eliminar
            }

            facturaRepository.delete(factura);
            return ResponseEntity.ok().<Void>build(); // CAST explícito
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/reporte")
    public ResponseEntity<byte[]> generarFacturaPDF(@PathVariable Long id) {
        return facturaRepository.findById(id)
                .map(factura -> {
                    byte[] pdf = reporteFacturaService.generarReporteFactura(factura);

                    String nombreCliente = (factura.getCliente() != null && factura.getCliente().getNombre() != null)
                            ? factura.getCliente().getNombre().replaceAll("[^a-zA-Z0-9]", "_")
                            : "cliente_desconocido";

                    String nombreArchivo = "factura_" + nombreCliente + "_" + factura.getId() + ".pdf";

                    return ResponseEntity.ok()
                            .header("Content-Disposition", "attachment; filename=" + nombreArchivo)
                            .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                            .body(pdf);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/reporte")
    public ResponseEntity<byte[]> generarReporteTodasFacturas() {
        List<Factura> facturas = facturaRepository.findAll();
        byte[] pdf = reporteFacturaService.generarReporteDeFacturas(facturas, "Listado de Facturas");
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=lista_facturas.pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);
    }



}
