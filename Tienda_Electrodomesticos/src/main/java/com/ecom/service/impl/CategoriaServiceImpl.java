package com.ecom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Categoria;
import com.ecom.repository.CategoriaRepository;
import com.ecom.service.CategoriaService;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    public CategoriaRepository categoriaRepository;

    @Override
    public Categoria guardarCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    public Boolean existeCategoria(String nombre) {
        return categoriaRepository.existsByNombre(nombre);
    }

    @Override
    public List<Categoria> getAllCategoria() {
        return categoriaRepository.findAll();
    }

    @Override
    public Boolean eliminarCategoria(int id) {
        Categoria categoria = categoriaRepository.findById(id).orElse(null);
        if (!ObjectUtils.isEmpty(categoria)) {
            categoriaRepository.delete(categoria);
            return true;
        }
        return false;
    }

    @Override
    public Categoria obtenerCategoriaPorId(int id) {
       
        Categoria categoria = categoriaRepository.findById(id).orElse(null);
        return categoria;
    }

    @Override
    public List<Categoria> getAllActiveCategoria() {
       List<Categoria> categorias =  categoriaRepository.findByIsActiveTrue();
        return categorias;
    }

}
