package com.database.restaurante;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoService produtoService;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoService produtoService) {
        this.pedidoRepository = pedidoRepository;
        this.produtoService = produtoService;
    }

    /** Converte o carrinho da sessão (chave "id-tamanho" -> quantidade) em itens com preço atual. */
    public List<ItemCarrinho> montarItens(Map<String, Integer> carrinho) {
        List<ItemCarrinho> itens = new ArrayList<>();
        for (Map.Entry<String, Integer> e : carrinho.entrySet()) {
            try {
                String[] partes = e.getKey().split("-");
                Produto produto = produtoService.buscarPorId(Long.parseLong(partes[0]));
                itens.add(new ItemCarrinho(produto, partes[1], e.getValue()));
            } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
                // produto excluído ou chave inválida: ignora o item
            }
        }
        return itens;
    }

    public BigDecimal calcularTotal(List<ItemCarrinho> itens) {
        return itens.stream()
            .map(ItemCarrinho::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Devolve a lista de erros (vazia se estiver tudo certo). */
    public List<String> validar(CheckoutForm form) {
        List<String> erros = new ArrayList<>();

        if (vazio(form.getNomeCliente())) {
            erros.add("Informe seu nome.");
        }
        String telefone = somenteDigitos(form.getTelefone());
        if (telefone.length() < 10 || telefone.length() > 11) {
            erros.add("Informe um telefone válido com DDD.");
        }
        if (form.getTipoEntrega() == null) {
            erros.add("Escolha entre retirar no local ou entrega.");
        } else if (form.getTipoEntrega() == TipoEntrega.ENTREGA) {
            if (vazio(form.getRua()))    erros.add("Informe a rua.");
            if (vazio(form.getNumero())) erros.add("Informe o número.");
            if (vazio(form.getBairro())) erros.add("Informe o bairro.");
            if (vazio(form.getCidade())) erros.add("Informe a cidade.");
            String cep = somenteDigitos(form.getCep());
            if (!cep.isEmpty() && cep.length() != 8) {
                erros.add("O CEP deve ter 8 dígitos.");
            }
        }
        return erros;
    }

    @Transactional
    public Pedido criarPedido(CheckoutForm form, List<ItemCarrinho> itens) {
        Pedido pedido = new Pedido();
        pedido.setNomeCliente(form.getNomeCliente().trim());
        pedido.setTelefone(somenteDigitos(form.getTelefone()));
        pedido.setTipoEntrega(form.getTipoEntrega());
        pedido.setObservacoes(limpar(form.getObservacoes()));
        pedido.setCriadoEm(LocalDateTime.now());

        // Endereço só é gravado quando for entrega; na retirada fica tudo nulo
        if (form.getTipoEntrega() == TipoEntrega.ENTREGA) {
            String cep = somenteDigitos(form.getCep());
            pedido.setCep(cep.isEmpty() ? null : cep);
            pedido.setRua(form.getRua().trim());
            pedido.setNumero(form.getNumero().trim());
            pedido.setComplemento(limpar(form.getComplemento()));
            pedido.setBairro(form.getBairro().trim());
            pedido.setCidade(form.getCidade().trim());
        }

        for (ItemCarrinho item : itens) {
            ItemPedido ip = new ItemPedido();
            ip.setProdutoId(item.getProduto().getId());
            ip.setNomeProduto(item.getProduto().getNome());
            ip.setTamanho(item.getTamanho());
            ip.setQuantidade(item.getQuantidade());
            ip.setPrecoUnitario(item.getPrecoUnitario());
            pedido.adicionarItem(ip);
        }
        // Total recalculado no servidor, nunca vindo do formulário
        pedido.setTotal(calcularTotal(itens));

        return pedidoRepository.save(pedido);
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));
    }

    private static boolean vazio(String s) {
        return s == null || s.isBlank();
    }

    private static String limpar(String s) {
        return vazio(s) ? null : s.trim();
    }

    private static String somenteDigitos(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }
}