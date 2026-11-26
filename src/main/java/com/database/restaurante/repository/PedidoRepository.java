package com.database.restaurante.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.database.restaurante.model.Pedido;
import com.database.restaurante.model.StatusPedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = "itens")
    List<Pedido> findAllByOrderByCriadoEmDesc();

    @EntityGraph(attributePaths = "itens")
    List<Pedido> findByStatusOrderByCriadoEmDesc(StatusPedido status);

    @EntityGraph(attributePaths = "itens")
    List<Pedido> findByClienteUsernameOrderByCriadoEmDesc(String username);
}