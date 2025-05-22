package com.fiados_api.controller;
import com.fiados_api.dto.EstadoFinancieroDTO;
import com.fiados_api.entity.Cliente;
import com.fiados_api.entity.EstadoFactura;
import com.fiados_api.entity.Factura;
import com.fiados_api.repository.ClienteRepository;
import com.fiados_api.service.ReporteFacturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;


@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteRepository clienteRepository;

    private final ReporteFacturaService reporteFacturaService;
    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(clienteRepository.findAll());
    }

    @GetMapping("/cedula-existe/{cedula}")
    public ResponseEntity<Boolean> existeCedula(@PathVariable String cedula) {
        boolean existe = clienteRepository.existsByCedula(cedula);
        return ResponseEntity.ok(existe);
    }
    


    @PostMapping
    public ResponseEntity<Cliente> crear(@Valid @RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteRepository.save(cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id, @Valid @RequestBody Cliente datos) {
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setTelefono(datos.getTelefono());
            cliente.setCorreo(datos.getCorreo());
            cliente.setDireccion(datos.getDireccion());
            cliente.setCedula(datos.getCedula());
            return ResponseEntity.ok(clienteRepository.save(cliente));
        }).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/reporte-facturas")
    public ResponseEntity<byte[]> generarReporteFacturasCliente(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    List<Factura> facturas = cliente.getFacturas();
                    byte[] pdf = reporteFacturaService.generarReporteDeFacturas(facturas, "Facturas de " + cliente.getNombre());

                    // 👇 nombre del archivo usando el nombre del cliente
                    String nombreArchivo = "facturas_" + cliente.getNombre().replaceAll("\\s+", "_") + ".pdf";

                    return ResponseEntity.ok()
                            .header("Content-Disposition", "attachment; filename=" + nombreArchivo)
                            .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                            .body(pdf);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return clienteRepository.findById(id).map(cliente -> {
            boolean tieneFacturasActivas = cliente.getFacturas().stream()
                    .anyMatch(f -> f.getEstado() == EstadoFactura.ACTIVA);

            if (tieneFacturasActivas) {
                return ResponseEntity.status(HttpStatus.CONFLICT).<Void>build(); // CAST explícito
            }

            clienteRepository.delete(cliente);
            return ResponseEntity.ok().<Void>build(); // CAST explícito
        }).orElse(ResponseEntity.notFound().build());
    }


}

