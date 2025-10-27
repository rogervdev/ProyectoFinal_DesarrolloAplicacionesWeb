package com.ecom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.model.Cart;

@Service
public interface CartService {
    public Cart saveCart(Integer productoId, Integer usuarioId);

    public List<Cart> getCartsByUser(Integer usuarioId);

    public Integer getCountCart(Integer usuario);

}
