package com.database.restaurante;

public enum TipoEntrega {
    RETIRADA("Retirar no local"),
    ENTREGA("Entrega no endereço");

    private final String descricao;

    TipoEntrega(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
}