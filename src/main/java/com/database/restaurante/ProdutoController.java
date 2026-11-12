package com.database.restaurante;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/admin/produtos";
    }

    @GetMapping("/admin/produtos")
    public String listar(@RequestParam(required = false) String busca, Model model) {
        if (busca != null && !busca.isBlank()) {
            model.addAttribute("produtos", service.buscarPorNome(busca));
        } else {
            model.addAttribute("produtos", service.listar());
        }
        model.addAttribute("abaAtiva", "produtos");
        return "cardapio";
    }

    @GetMapping("/admin/produtos/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new Produto());
        return "form";
    }

    @PostMapping("/admin/produtos/salvar")
    public String salvar(@ModelAttribute Produto produto) {
        service.salvar(produto);
        return "redirect:/admin/produtos";
    }

    @GetMapping("/admin/produtos/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("produto", service.buscarPorId(id));
        return "form";
    }

    @PostMapping("/admin/produtos/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        service.excluir(id);
        return "redirect:/admin/produtos";
    }
    @SuppressWarnings("unchecked")
    private static final List<String> ORDEM_CATEGORIAS =
    List.of("Hambúrguer", "Acompanhamento", "Bebida", "Sobremesa");

@GetMapping("/cardapio")
public String CardapioPublico(Model model, HttpSession session) {
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

    Map<String, Integer> carrinho = (Map<String, Integer>) session.getAttribute("carrinho");
    int totalItens = carrinho == null ? 0
        : carrinho.values().stream().mapToInt(Integer::intValue).sum();
    model.addAttribute("totalItens", totalItens);

    return "CardapioPublico";
}
}