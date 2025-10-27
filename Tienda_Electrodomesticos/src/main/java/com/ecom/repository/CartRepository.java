package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    public Cart findByProductoIdAndUsuarioId(Integer productoId, Integer usuarioId);

    public Integer countByUsuarioId(Integer usuarioId);

    public List<Cart> findByUsuarioId(Integer usuarioId);

}
