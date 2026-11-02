package com.database.restaurante;

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
    @GetMapping("/cardapio")
    public String cardapioPublico(Model model) {
        model.addAttribute("produtos", service.listar());
        return "CardapioPublico";
    }
}