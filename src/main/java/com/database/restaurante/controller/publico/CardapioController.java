package com.database.restaurante.controller.publico;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.database.restaurante.model.Produto;
import com.database.restaurante.service.ProdutoService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CardapioController {

    // Ordem em que as categorias aparecem no cardápio
    private static final List<String> ORDEM_CATEGORIAS =
        List.of("Hambúrguer", "Acompanhamento", "Bebida", "Sobremesa");

    private final ProdutoService service;

    public CardapioController(ProdutoService service) {
        this.service = service;
    }

    // localhost:8080 abre direto o cardápio
    @GetMapping("/")
    public String inicio() {
        return "redirect:/cardapio";
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/cardapio")
    public String cardapio(Model model, HttpSession session) {
        List<Produto> todos = service.listar();

        // Mantém a ordem definida acima; categorias sem produto não aparecem
        Map<String, List<Produto>> categorias = new LinkedHashMap<>();
        for (String cat : ORDEM_CATEGORIAS) {
            List<Produto> lista = todos.stream()
                .filter(p -> cat.equalsIgnoreCase(p.getCategoria()))
                .toList();
            if (!lista.isEmpty()) {
                categorias.put(cat, lista);
            }
        }

        // Produtos com categoria fora da lista (ex.: cadastros antigos) não somem
        List<Produto> outros = todos.stream()
            .filter(p -> ORDEM_CATEGORIAS.stream().noneMatch(c -> c.equalsIgnoreCase(p.getCategoria())))
            .toList();
        if (!outros.isEmpty()) {
            categorias.put("Outros", outros);
        }
        model.addAttribute("categorias", categorias);

        // Quantidade de itens no carrinho (contador do topo)
        Map<String, Integer> carrinho = (Map<String, Integer>) session.getAttribute("carrinho");
        int totalItens = carrinho == null ? 0
            : carrinho.values().stream().mapToInt(Integer::intValue).sum();
        model.addAttribute("totalItens", totalItens);

        // Link "Acompanhar meu pedido" (último pedido feito nesta sessão)
        model.addAttribute("ultimoPedidoId", session.getAttribute("ultimoPedidoId"));

        return "CardapioPublico";
    }
}