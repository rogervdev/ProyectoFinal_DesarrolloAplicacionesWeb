package com.ecom.service;

import java.util.List;

import com.ecom.model.Categoria;

public interface CategoriaService {
    public Categoria guardarCategoria(Categoria categoria);

    public Boolean existeCategoria(String nombre);

    List<Categoria> getAllCategoria();

    public Boolean eliminarCategoria(int id);

    public Categoria obtenerCategoriaPorId(int id);

    public List<Categoria> getAllActiveCategoria();
}
