package com.ecom.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Producto;

public interface ProductoService  {
        
        public Producto guardarProducto(Producto producto);

        public List<Producto> getAllProductos();

        public Boolean eliminarProducto(Integer id);

        public Producto getProductoById(Integer id);

        public Producto actualizarProducto(Producto producto, MultipartFile imagen);

        public List<Producto> getAllActiveProducto(String categoria);

        public List<Producto> buscarProducto(String ch);
}       

