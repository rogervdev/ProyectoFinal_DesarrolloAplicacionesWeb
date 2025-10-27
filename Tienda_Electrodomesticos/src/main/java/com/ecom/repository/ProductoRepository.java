package com.ecom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {


    List<Producto> findByIsActiveTrue();


    List<Producto> findByCategoria_Nombre(String nombreCategoria);

 
    List<Producto> findByTituloStartingWithIgnoreCaseOrCategoria_NombreStartingWithIgnoreCase(String titulo, String nombreCategoria);
}
