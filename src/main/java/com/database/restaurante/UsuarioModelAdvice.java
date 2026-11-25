package com.database.restaurante;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UsuarioModelAdvice {

    @ModelAttribute("usuarioLogado")
    public String usuarioLogado(Authentication auth) {
        return auth == null ? null : auth.getName();
    }

    @ModelAttribute("ehAdmin")
    public boolean ehAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}