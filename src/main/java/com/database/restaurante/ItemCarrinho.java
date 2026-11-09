package com.database.restaurante;

import java.math.BigDecimal;

public class ItemCarrinho {

    private final Produto produto;
    private final String tamanho; // P, M, G ou U (único)
    private final int quantidade;

    public ItemCarrinho(Produto produto, String tamanho, int quantidade) {
        this.produto = produto;
        this.tamanho = tamanho;
        this.quantidade = quantidade;
    }

    public Produto getProduto() { return produto; }
    public String getTamanho() { return tamanho; }
    public int getQuantidade() { return quantidade; }

    public String getChave() { return produto.getId() + "-" + tamanho; }

    public BigDecimal getPrecoUnitario() {
        BigDecimal p = switch (tamanho) {
            case "M" -> produto.getPrecoM();
            case "G" -> produto.getPrecoG();
            default -> produto.getPreco();
        };
        return p == null ? BigDecimal.ZERO : p;
    }

    public BigDecimal getSubtotal() {
        return getPrecoUnitario().multiply(BigDecimal.valueOf(quantidade));
    }
}