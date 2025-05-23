package com.fiados_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    @Column(nullable = false)
    private EstadoFactura estado = EstadoFactura.ACTIVA;



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    @JsonIgnoreProperties({"facturas", "hibernateLazyInitializer", "handler"})
    private Cliente cliente;


    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Producto> productos;

    private Double total;


}