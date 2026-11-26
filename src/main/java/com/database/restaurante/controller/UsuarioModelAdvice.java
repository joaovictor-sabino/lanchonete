package com.database.restaurante.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.database.restaurante.repository.UsuarioRepository;

@ControllerAdvice
public class UsuarioModelAdvice {

    private final UsuarioRepository usuarios;

    public UsuarioModelAdvice(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    // Nome mostrado no topo: primeiro nome da pessoa (ou o login, se não tiver nome)
    @ModelAttribute("usuarioLogado")
    public String usuarioLogado(Authentication auth) {
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return null;   // ninguém logado
        }
        return usuarios.findByUsername(auth.getName())
            .map(u -> (u.getNome() == null || u.getNome().isBlank())
                    ? u.getUsername()                      // admin não tem nome: mostra "admin"
                    : u.getNome().trim().split("\\s+")[0]) // "Maria da Silva" vira "Maria"
            .orElse(auth.getName());
    }

    // Diz se é administrador (usado para mostrar o link "Painel")
    @ModelAttribute("ehAdmin")
    public boolean ehAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}