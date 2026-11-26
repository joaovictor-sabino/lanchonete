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
import org.springframework.web.multipart.MultipartFile;



@Controller
public class ProdutoController {

    private final ProdutoService service;
    private final ArmazenamentoImagemService imagens;

    public ProdutoController(ProdutoService service, ArmazenamentoImagemService imagens) {
        this.service = service;
        this.imagens = imagens;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/cardapio";
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
    public String salvar(@ModelAttribute Produto produto,
                        @RequestParam(value = "arquivoImagem", required = false) MultipartFile arquivoImagem,
                        Model model) {

        String imagemAtual = null;
        if (produto.getId() != null) {
            imagemAtual = service.buscarPorId(produto.getId()).getImagem();
        }
        produto.setImagem(imagemAtual);

        if (arquivoImagem != null && !arquivoImagem.isEmpty()) {
            try {
                produto.setImagem(imagens.salvar(arquivoImagem, produto.getNome()));
            } catch (IllegalArgumentException e) {
                model.addAttribute("erroImagem", e.getMessage());
                return "form";
            }
        }

        service.salvar(produto);

        // só apaga a foto antiga se o nome mudou (ex.: de .png para .jpg)
        if (imagemAtual != null && !imagemAtual.equals(produto.getImagem())) {
            imagens.remover(imagemAtual);
        }
        return "redirect:/admin/produtos";
    }

    @GetMapping("/admin/produtos/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("produto", service.buscarPorId(id));
        return "form";
    }

    @PostMapping("/admin/produtos/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        String imagem = service.buscarPorId(id).getImagem();
        service.excluir(id);
        imagens.remover(imagem);
        return "redirect:/admin/produtos";
    }
    
    private static final List<String> ORDEM_CATEGORIAS =
        List.of("Hambúrguer", "Acompanhamento", "Bebida", "Sobremesa");
@SuppressWarnings("unchecked")
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