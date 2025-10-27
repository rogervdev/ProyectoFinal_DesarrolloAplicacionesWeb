package com.ecom.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Cart;
import com.ecom.model.Producto;
import com.ecom.model.Usuario;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductoRepository;
import com.ecom.repository.UsuarioRepository;
import com.ecom.service.CartService;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Cart saveCart(Integer productoId, Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).get();
        Producto producto = productoRepository.findById(productoId).get();

        Cart cartStatus = cartRepository.findByProductoIdAndUsuarioId(productoId, usuarioId);
        Cart cart = null;

        if (ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setProducto(producto);
            cart.setUsuario(usuario);
            cart.setCantidad(1);
            cart.setPrecioTotal(1 * producto.getPrecioConDescuento());
        } else {
            cart = cartStatus;
            cart.setCantidad(cart.getCantidad() + 1);
            cart.setPrecioTotal(cart.getCantidad() * cart.getProducto().getPrecioConDescuento());
        }

        Cart saveCart = cartRepository.save(cart);
        return saveCart;
    }

    @Override
    public List<Cart> getCartsByUser(Integer usuarioId) {
        List<Cart> carts = cartRepository.findByUsuarioId(usuarioId);

        Double precioTotalDeOrdenes = 0.0;
        List<Cart> updateCarts = new ArrayList<>();
        for (Cart c : carts) {
            Double precioTotal = (c.getProducto().getPrecioConDescuento() * c.getCantidad());
            c.setPrecioTotal(precioTotal);
            precioTotalDeOrdenes = precioTotalDeOrdenes + precioTotal;
            c.setTotalPrecioOrdenes(precioTotalDeOrdenes);
            updateCarts.add(c);
        }

        return updateCarts;
    }

    @Override
    public Integer getCountCart(Integer usuarioId) {

        Integer countByUsuarioId = cartRepository.countByUsuarioId(usuarioId);
        return countByUsuarioId;
    }

}
