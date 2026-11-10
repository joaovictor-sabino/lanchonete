package com.database.restaurante;

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