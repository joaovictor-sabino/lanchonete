package com.database.restaurante.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.database.restaurante.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByNomeContainingIgnoreCase(String trecho);

    List<Produto> findByCategoria(String categoria);
}