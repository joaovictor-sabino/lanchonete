package com.database.restaurante;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MinhaContaController {

    private final PedidoService pedidoService;

    public MinhaContaController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/minha-conta/pedidos")
    public String pedidos(Authentication auth, Model model) {
        List<Pedido> lista = pedidoService.listarDoCliente(auth.getName());

        model.addAttribute("pedidos", lista);
        // etapas da linha de progresso (a ordem segue o enum StatusPedido)
        model.addAttribute("etapas", List.of(StatusPedido.NOVO, StatusPedido.EM_PREPARO,
                                             StatusPedido.PRONTO, StatusPedido.FINALIZADO));
        model.addAttribute("temAtivo", lista.stream().anyMatch(p -> !p.isEncerrado()));
        return "meus-pedidos";
    }
}