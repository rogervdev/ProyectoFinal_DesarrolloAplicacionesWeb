package com.ecom.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Producto;
import com.ecom.repository.ProductoRepository;
import com.ecom.service.ProductoService;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Boolean eliminarProducto(Integer id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (!ObjectUtils.isEmpty(producto)) {
            productoRepository.delete(producto);
            return true;
        }
        return false;
    }

    @Override
    public Producto getProductoById(Integer id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        return producto;
    }

    @Override
    public Producto actualizarProducto(Producto producto, MultipartFile imagen) {

        Producto productoDB = getProductoById(producto.getId());
        String imagenNombre = imagen.isEmpty() ? productoDB.getImagen() : imagen.getOriginalFilename();

        productoDB.setTitulo(producto.getTitulo());
        productoDB.setDescripcion(producto.getDescripcion());
        productoDB.setCategoria(producto.getCategoria());
        productoDB.setPrecio(producto.getPrecio());
        productoDB.setStock(producto.getStock());
        productoDB.setImagen(imagenNombre);
        productoDB.setIsActive(producto.getIsActive());

        productoDB.setDescuento(producto.getDescuento());

        Double descuento = producto.getPrecio() * (producto.getDescuento() / 100.0);
        Double precioConDescuento = producto.getPrecio() - descuento;
        productoDB.setPrecioConDescuento(precioConDescuento);

        Producto productoActualizado = productoRepository.save(productoDB);
        if (!ObjectUtils.isEmpty(productoActualizado)) {
            if (!imagen.isEmpty()) {

                try {
                    File saveFile = new ClassPathResource("static/img").getFile();

                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
                            + imagen.getOriginalFilename());
                    Files.copy(imagen.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return producto;
        }
        return null;
    }

    @Override
    public List<Producto> getAllActiveProducto(String categoria) {
        List<Producto> productos = null;

        if (ObjectUtils.isEmpty(categoria)) {
            // Si no hay categoría, trae todos los productos activos
            productos = productoRepository.findByIsActiveTrue();
        } else {
            // Si hay categoría, busca por nombre de categoría
            productos = productoRepository.findByCategoria_Nombre(categoria);
        }

        return productos;
    }

    @Override
    public List<Producto> buscarProducto(String ch) {
        // Busca por título o nombre de categoría ignorando mayúsculas
        return productoRepository.findByTituloStartingWithIgnoreCaseOrCategoria_NombreStartingWithIgnoreCase(ch, ch);
    }

}
