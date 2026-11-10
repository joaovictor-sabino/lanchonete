package com.database.restaurante;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminPedidoController {

    private final PedidoService service;

    public AdminPedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping("/admin/pedidos")
    public String listar(@RequestParam(required = false) String status, Model model) {
        StatusPedido filtro = converter(status);
        model.addAttribute("pedidos", service.listar(filtro));
        model.addAttribute("statusAtual", filtro);
        model.addAttribute("todosStatus", StatusPedido.values());
        model.addAttribute("abaAtiva", "pedidos");
        return "admin-pedidos";
    }

    @PostMapping("/admin/pedidos/{id}/status")
    public String mudarStatus(@PathVariable Long id, @RequestParam String status) {
        StatusPedido novo = converter(status);
        if (novo != null) {
            service.atualizarStatus(id, novo);
        }
        return "redirect:/admin/pedidos";
    }

    // Aceita só valores válidos do enum; qualquer outra coisa vira null
    private StatusPedido converter(String valor) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return StatusPedido.valueOf(valor);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}