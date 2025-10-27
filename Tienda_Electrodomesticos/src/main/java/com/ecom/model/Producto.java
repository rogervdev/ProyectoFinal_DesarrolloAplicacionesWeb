package com.ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "productos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 500, message = "El título no puede exceder los 500 caracteres")
    @Column(name = "titulo", nullable = false, length = 500)
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 5000, message = "La descripción no puede exceder los 5000 caracteres")
    @Column(name = "descripcion", nullable = false, length = 5000)
    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false, foreignKey = @ForeignKey(name = "fk_producto_categoria"))
    private Categoria categoria;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    @Column(name = "precio", nullable = false)
    private Double precio;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    @Size(max = 255, message = "Nombre de imagen demasiado largo")
    @Column(name = "imagen", length = 255)
    private String imagen;

    @Min(value = 0, message = "El descuento no puede ser negativo")
    @Max(value = 100, message = "El descuento no puede ser mayor a 100%")
    @Column(name = "descuento", nullable = false)
    private Integer descuento = 0;

    @Column(name = "precio_con_descuento")
    private Double precioConDescuento;

    @Column(name = "is_active")
    private Boolean isActive = true;
}