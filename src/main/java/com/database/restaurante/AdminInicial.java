package com.database.restaurante;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInicial implements CommandLineRunner {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final String usuario;
    private final String senha;

    public AdminInicial(UsuarioRepository repo, PasswordEncoder encoder,
                        @Value("${app.admin.usuario}") String usuario,
                        @Value("${app.admin.senha}") String senha) {
        this.repo = repo;
        this.encoder = encoder;
        this.usuario = usuario;
        this.senha = senha;
    }

    @Override
    public void run(String... args) {
        if (repo.findByUsername(usuario).isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername(usuario);
            admin.setSenha(encoder.encode(senha));
            admin.setPapel("ADMIN");
            repo.save(admin);
        }
    }
}