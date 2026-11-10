package com.database.restaurante;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = "itens")
    List<Pedido> findAllByOrderByCriadoEmDesc();

    @EntityGraph(attributePaths = "itens")
    List<Pedido> findByStatusOrderByCriadoEmDesc(StatusPedido status);
}