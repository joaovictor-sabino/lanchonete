package com.database.restaurante.model;

public enum StatusPedido {
    NOVO("Novo"),
    EM_PREPARO("Em preparo"),
    PRONTO("Pronto"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPedido(String descricao) { this.descricao = descricao; }

    public String getDescricao() { return descricao; }
}