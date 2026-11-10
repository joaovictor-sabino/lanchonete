package com.database.restaurante;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    private static final String SESSAO_ULTIMO_PEDIDO = "ultimoPedidoId";

    private final PedidoService pedidoService;

    public CheckoutController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> carrinhoDaSessao(HttpSession session) {
        return (Map<String, Integer>) session.getAttribute("carrinho");
    }

    private boolean carrinhoVazio(Map<String, Integer> carrinho) {
        return carrinho == null || carrinho.isEmpty();
    }

    private void preencherResumo(Model model, List<ItemCarrinho> itens) {
        BigDecimal total = pedidoService.calcularTotal(itens);
        model.addAttribute("itens", itens);
        model.addAttribute("total", total);
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Map<String, Integer> carrinho = carrinhoDaSessao(session);
        if (carrinhoVazio(carrinho)) {
            return "redirect:/carrinho";
        }
        List<ItemCarrinho> itens = pedidoService.montarItens(carrinho);
        if (itens.isEmpty()) {
            return "redirect:/carrinho";
        }
        preencherResumo(model, itens);
        model.addAttribute("form", new CheckoutForm());
        return "checkout";
    }

    @PostMapping("/checkout/confirmar")
    public String confirmar(@ModelAttribute("form") CheckoutForm form,
                            HttpSession session, Model model) {
        Map<String, Integer> carrinho = carrinhoDaSessao(session);
        if (carrinhoVazio(carrinho)) {
            return "redirect:/carrinho";
        }
        List<ItemCarrinho> itens = pedidoService.montarItens(carrinho);
        if (itens.isEmpty()) {
            return "redirect:/carrinho";
        }

        List<String> erros = pedidoService.validar(form);
        if (!erros.isEmpty()) {
            preencherResumo(model, itens);
            model.addAttribute("erros", erros);
            return "checkout";
        }

        Pedido pedido = pedidoService.criarPedido(form, itens);

        carrinho.clear();
        session.setAttribute(SESSAO_ULTIMO_PEDIDO, pedido.getId());
        return "redirect:/pedido/" + pedido.getId() + "/confirmado";
    }

    @GetMapping("/pedido/{id}/confirmado")
    public String confirmado(@PathVariable Long id, HttpSession session, Model model) {
        // Só mostra o pedido para quem acabou de fazê-lo (evita enumerar ids de outros clientes)
        Long ultimo = (Long) session.getAttribute(SESSAO_ULTIMO_PEDIDO);
        if (ultimo == null || !ultimo.equals(id)) {
            return "redirect:/cardapio";
        }
        model.addAttribute("pedido", pedidoService.buscarPorId(id));
        return "pedido-confirmado";
    }
}