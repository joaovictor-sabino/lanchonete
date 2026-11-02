package com.database.restaurante;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public List<Produto> listar() {
        return repository.findAll();
    }

    public Produto salvar(Produto produto) {
        return repository.save(produto);
    }

    public Produto buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    public List<Produto> buscarPorNome(String trecho) {
        return repository.findByNomeContainingIgnoreCase(trecho);
    }

    public List<Produto> buscarPorCategoria(String categoria) {
        return repository.findByCategoria(categoria);
    }
}