package com.database.restaurante;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminPedidoController {

    private final PedidoService service;

    public AdminPedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping("/admin/pedidos")
    public String listar(Model model) {
        // uma lista por status, na ordem do enum
        Map<StatusPedido, List<Pedido>> colunas = new LinkedHashMap<>();
        for (StatusPedido s : StatusPedido.values()) {
            colunas.put(s, new ArrayList<>());
        }
        for (Pedido p : service.listar(null)) {
            colunas.get(p.getStatus()).add(p);
        }

        model.addAttribute("colunas", colunas);
        model.addAttribute("abaAtiva", "pedidos");
        return "admin-pedidos";
    }

    // chamado pelo JavaScript quando o cartão é solto em outra coluna
    @PostMapping("/admin/pedidos/{id}/mover")
    @ResponseBody
    public ResponseEntity<Void> mover(@PathVariable Long id, @RequestParam String status) {
        StatusPedido novo = converter(status);
        if (novo == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            service.atualizarStatus(id, novo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    // mantido da versão anterior (não é mais usado pelo Kanban)
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