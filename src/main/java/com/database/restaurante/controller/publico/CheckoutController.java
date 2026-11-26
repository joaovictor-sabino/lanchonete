package com.database.restaurante.controller.publico;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.database.restaurante.dto.CheckoutForm;
import com.database.restaurante.dto.ItemCarrinho;
import com.database.restaurante.model.Pedido;
import com.database.restaurante.model.Usuario;
import com.database.restaurante.repository.UsuarioRepository;
import com.database.restaurante.service.PedidoService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    private static final String SESSAO_ULTIMO_PEDIDO = "ultimoPedidoId";

    private final PedidoService pedidoService;
    private final UsuarioRepository usuarios;

    public CheckoutController(PedidoService pedidoService, UsuarioRepository usuarios) {
        this.pedidoService = pedidoService;
        this.usuarios = usuarios;
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

    // devolve o usuário logado, ou null se for convidado
    private Usuario clienteLogado(Authentication auth) {
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return usuarios.findByUsername(auth.getName()).orElse(null);
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model, Authentication auth) {
        Map<String, Integer> carrinho = carrinhoDaSessao(session);
        if (carrinhoVazio(carrinho)) {
            return "redirect:/carrinho";
        }
        List<ItemCarrinho> itens = pedidoService.montarItens(carrinho);
        if (itens.isEmpty()) {
            return "redirect:/carrinho";
        }

        // se estiver logado, já vem com nome e telefone preenchidos
        CheckoutForm form = new CheckoutForm();
        Usuario cliente = clienteLogado(auth);
        if (cliente != null) {
            form.setNomeCliente(cliente.getNome());
            form.setTelefone(cliente.getTelefone());
        }

        preencherResumo(model, itens);
        model.addAttribute("form", form);
        return "checkout";
    }

    @PostMapping("/checkout/confirmar")
    public String confirmar(@ModelAttribute("form") CheckoutForm form,
                            HttpSession session, Model model, Authentication auth) {
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

        // o terceiro argumento é o cliente logado (ou null, se for convidado)
        Pedido pedido = pedidoService.criarPedido(form, itens, clienteLogado(auth));

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