package com.database.restaurante;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import jakarta.servlet.http.HttpSession;

@Controller
public class CarrinhoController {

    private final ProdutoService service;

    public CarrinhoController(ProdutoService service) {
        this.service = service;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> getCarrinho(HttpSession session) {
        Map<String, Integer> carrinho = (Map<String, Integer>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new LinkedHashMap<>();
            session.setAttribute("carrinho", carrinho);
        }
        return carrinho;
    }

    private String tamanhoValido(Produto p, String tam) {
        boolean temTamanhos = p.getPrecoM() != null || p.getPrecoG() != null;
        if (!temTamanhos) return "U";
        if ("M".equals(tam) && p.getPrecoM() != null) return "M";
        if ("G".equals(tam) && p.getPrecoG() != null) return "G";
        return "P";
    }

    
    private int adicionarItens(Map<String, String> params, HttpSession session) {
    int adicionados = 0;
    for (Map.Entry<String, String> e : params.entrySet()) {
        if (!e.getKey().startsWith("qtd_")) continue;

        int quantidade;
        try {
            quantidade = Integer.parseInt(e.getValue());
        } catch (NumberFormatException ex) {
            continue;
        }
        if (quantidade < 1) continue;
        if (quantidade > 20) quantidade = 20;

        try {
            Long id = Long.parseLong(e.getKey().substring(4));
            Produto produto = service.buscarPorId(id);
            String tam = tamanhoValido(produto, params.get("tam_" + id));
            getCarrinho(session).merge(id + "-" + tam, quantidade, Integer::sum);
            adicionados += quantidade;
        } catch (IllegalArgumentException ex) {
            // id inválido ou produto inexistente: ignora
        }
    }
    return adicionados;
}

// Reserva: se o JavaScript estiver desligado, o formulário ainda funciona
@PostMapping("/carrinho/adicionar-varios")
public String adicionarVarios(@RequestParam Map<String, String> params, HttpSession session) {
    adicionarItens(params, session);
    return "redirect:/carrinho";
}

// Usado pelo cardápio: adiciona e devolve os números, sem redirecionar
@PostMapping("/carrinho/adicionar-ajax")
@ResponseBody
public Map<String, Integer> adicionarAjax(@RequestParam Map<String, String> params, HttpSession session) {
    int adicionados = adicionarItens(params, session);
    int totalItens = getCarrinho(session).values().stream().mapToInt(Integer::intValue).sum();
    return Map.of("totalItens", totalItens, "adicionados", adicionados);
}

    @GetMapping("/carrinho")
    public String ver(HttpSession session, Model model) {
        List<ItemCarrinho> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> e : new ArrayList<>(getCarrinho(session).entrySet())) {
            try {
                String[] partes = e.getKey().split("-");
                Produto produto = service.buscarPorId(Long.parseLong(partes[0]));
                ItemCarrinho item = new ItemCarrinho(produto, partes[1], e.getValue());
                itens.add(item);
                total = total.add(item.getSubtotal());
            } catch (IllegalArgumentException ex) {
                getCarrinho(session).remove(e.getKey()); // produto excluído
            }
        }

        model.addAttribute("itens", itens);
        model.addAttribute("total", total);
        return "carrinho";
    }

    @PostMapping("/carrinho/remover")
    public String remover(@RequestParam String chave, HttpSession session) {
        getCarrinho(session).remove(chave);
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/limpar")
    public String limpar(HttpSession session) {
        getCarrinho(session).clear();
        return "redirect:/carrinho";
    }
}