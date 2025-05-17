package com.fiados_api.service;

import com.fiados_api.entity.Cliente;
import com.fiados_api.entity.EstadoFactura;
import com.fiados_api.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public Cliente crear(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente actualizar(Long id, Cliente nuevosDatos) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setTelefono(nuevosDatos.getTelefono());
        cliente.setCorreo(nuevosDatos.getCorreo());
        cliente.setDireccion(nuevosDatos.getDireccion());
        cliente.setCedula(nuevosDatos.getCedula());

        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        boolean tieneFacturasActivas = cliente.getFacturas().stream()
                .anyMatch(f -> f.getEstado() == EstadoFactura.ACTIVA);

        if (tieneFacturasActivas) {
            throw new IllegalStateException("No se puede eliminar el cliente porque tiene facturas activas");
        }

        clienteRepository.delete(cliente);
    }
}
