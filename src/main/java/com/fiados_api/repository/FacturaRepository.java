package com.fiados_api.repository;

import com.fiados_api.entity.EstadoFactura;
import com.fiados_api.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    long countByEstado(EstadoFactura estado);

}
