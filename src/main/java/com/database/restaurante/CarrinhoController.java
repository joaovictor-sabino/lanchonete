package com.database.restaurante;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class CarrinhoController {

    private final ProdutoService service;

    public CarrinhoController(ProdutoService service) {
        this.service = service;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCarrinho(HttpSession session) {
        Map<Long, Integer> carrinho = (Map<Long, Integer>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new LinkedHashMap<>();
            session.setAttribute("carrinho", carrinho);
        }
        return carrinho;
    }

    @PostMapping("/carrinho/adicionar")
    public String adicionar(@RequestParam Long id,
                            @RequestParam(defaultValue = "1") int quantidade,
                            HttpSession session) {
        if (quantidade < 1) quantidade = 1;
        if (quantidade > 20) quantidade = 20;

        service.buscarPorId(id); // garante que o produto existe
        getCarrinho(session).merge(id, quantidade, Integer::sum);
        return "redirect:/cardapio";
    }

    @GetMapping("/carrinho")
    public String ver(HttpSession session, Model model) {
        List<ItemCarrinho> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> e : new ArrayList<>(getCarrinho(session).entrySet())) {
            try {
                ItemCarrinho item = new ItemCarrinho(service.buscarPorId(e.getKey()), e.getValue());
                itens.add(item);
                total = total.add(item.getSubtotal());
            } catch (IllegalArgumentException ex) {
                // produto foi excluído pelo administrador: tira do carrinho
                getCarrinho(session).remove(e.getKey());
            }
        }

        model.addAttribute("itens", itens);
        model.addAttribute("total", total);
        return "carrinho";
    }

    @PostMapping("/carrinho/remover/{id}")
    public String remover(@PathVariable Long id, HttpSession session) {
        getCarrinho(session).remove(id);
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/limpar")
    public String limpar(HttpSession session) {
        getCarrinho(session).clear();
        return "redirect:/carrinho";
    }
}