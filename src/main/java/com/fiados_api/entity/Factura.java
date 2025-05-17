package com.fiados_api.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaCreacion;
    private LocalDate fechaActualizacion;

    @Enumerated(EnumType.STRING)
    private EstadoFactura estado;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @JsonBackReference
    @JsonIgnoreProperties("facturas") // Previene loops cuando cliente se serializa desde factura
    private Cliente cliente;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Producto> productos;

    private Double total;
}