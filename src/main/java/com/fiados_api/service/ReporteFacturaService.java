package com.fiados_api.service;

import com.fiados_api.entity.Factura;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReporteFacturaService {

    public byte[] generarReporteFactura(Factura factura) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        Document document = new Document(pdf);

        document.add(new Paragraph("Factura #" + factura.getId()));
        document.add(new Paragraph("Fecha de creación: " + (factura.getFechaCreacion() != null ? factura.getFechaCreacion() : "N/A")));

        if (factura.getCliente() != null) {
            document.add(new Paragraph("Cliente: " + factura.getCliente().getNombre() + " (" + factura.getCliente().getCedula() + ")"));
        } else {
            document.add(new Paragraph("Cliente: N/A"));
        }

        document.add(new Paragraph("Estado: " + (factura.getEstado() != null ? factura.getEstado().toString() : "SIN ESTADO")));
        document.add(new Paragraph(""));

        Table tabla = new Table(4);
        tabla.addCell("Producto");
        tabla.addCell("Cantidad");
        tabla.addCell("Precio Unitario");
        tabla.addCell("Subtotal");

        factura.getProductos().forEach(p -> {
            tabla.addCell(p.getNombre());
            tabla.addCell(String.valueOf(p.getCantidad()));
            tabla.addCell(String.valueOf(p.getPrecioUnitario()));
            tabla.addCell(String.valueOf(p.getCantidad() * p.getPrecioUnitario()));
        });

        document.add(tabla);
        document.add(new Paragraph("\nTotal: $" + factura.getTotal()));

        document.close();
        return out.toByteArray();
    }

    public byte[] generarReporteDeFacturas(List<Factura> facturas, String titulo) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        Document document = new Document(pdf);

        document.add(new Paragraph(titulo).setBold().setFontSize(14));
        document.add(new Paragraph("Total facturas: " + facturas.size()));
        document.add(new Paragraph(""));

        Table tabla = new Table(5);
        tabla.addCell("ID");
        tabla.addCell("Cliente");
        tabla.addCell("Fecha");
        tabla.addCell("Estado");
        tabla.addCell("Total");

        facturas.forEach(f -> {
            tabla.addCell(f.getId() != null ? f.getId().toString() : "N/A");
            tabla.addCell(f.getCliente() != null ? f.getCliente().getNombre() : "N/A");
            tabla.addCell(f.getFechaCreacion() != null ? f.getFechaCreacion().toString() : "N/A");
            tabla.addCell(f.getEstado() != null ? f.getEstado().toString() : "SIN ESTADO");
            tabla.addCell("$" + (f.getTotal() != null ? f.getTotal() : 0));
        });

        document.add(tabla);
        document.close();
        return out.toByteArray();
    }
}
