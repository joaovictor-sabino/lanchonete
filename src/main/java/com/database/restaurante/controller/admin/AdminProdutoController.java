package com.database.restaurante.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.database.restaurante.model.Produto;
import com.database.restaurante.service.ArmazenamentoImagemService;
import com.database.restaurante.service.ProdutoService;

@Controller
public class AdminProdutoController {

    private final ProdutoService service;
    private final ArmazenamentoImagemService imagens;

    public AdminProdutoController(ProdutoService service, ArmazenamentoImagemService imagens) {
        this.service = service;
        this.imagens = imagens;
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

    @GetMapping("/admin/produtos/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("produto", service.buscarPorId(id));
        return "form";
    }

    @PostMapping("/admin/produtos/salvar")
    public String salvar(@ModelAttribute Produto produto,
                         @RequestParam(value = "arquivoImagem", required = false) MultipartFile arquivoImagem,
                         Model model) {

        // nunca confia em nome de imagem vindo do formulário: mantém o que já está salvo
        String imagemAtual = null;
        if (produto.getId() != null) {
            imagemAtual = service.buscarPorId(produto.getId()).getImagem();
        }
        produto.setImagem(imagemAtual);

        // se uma foto nova foi enviada, grava com o nome do produto (ex.: x-burguer.jpg)
        if (arquivoImagem != null && !arquivoImagem.isEmpty()) {
            try {
                produto.setImagem(imagens.salvar(arquivoImagem, produto.getNome()));
            } catch (IllegalArgumentException e) {
                model.addAttribute("erroImagem", e.getMessage());
                return "form";
            }
        }

        service.salvar(produto);

        // só apaga a foto antiga se o nome do arquivo mudou (ex.: de .png para .jpg)
        if (imagemAtual != null && !imagemAtual.equals(produto.getImagem())) {
            imagens.remover(imagemAtual);
        }
        return "redirect:/admin/produtos";
    }

    @PostMapping("/admin/produtos/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        String imagem = service.buscarPorId(id).getImagem();
        service.excluir(id);
        imagens.remover(imagem);   // apaga também a foto do produto
        return "redirect:/admin/produtos";
    }
}