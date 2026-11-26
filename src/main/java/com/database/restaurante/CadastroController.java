package com.database.restaurante;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CadastroController {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public CadastroController(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    @GetMapping("/cadastro")
    public String formulario(Authentication auth, Model model) {
        if (auth != null) return "redirect:/cardapio";   // já está logado
        model.addAttribute("form", new CadastroForm());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@ModelAttribute("form") CadastroForm form, Model model) {
        List<String> erros = validar(form);

        if (erros.isEmpty()) {
            Usuario u = new Usuario();
            u.setNome(form.getNome().trim());
            u.setUsername(form.getEmail().trim().toLowerCase());   // o e-mail é o login
            u.setTelefone(somenteDigitos(form.getTelefone()).isEmpty() ? null : somenteDigitos(form.getTelefone()));
            u.setSenha(encoder.encode(form.getSenha()));
            u.setPapel("CLIENTE");                                  // nunca vem do formulário
            try {
                usuarios.save(u);
                return "redirect:/login?cadastrado";
            } catch (DataIntegrityViolationException e) {
                erros.add("Já existe uma conta com esse e-mail.");   // duas pessoas cadastrando ao mesmo tempo
            }
        }

        model.addAttribute("erros", erros);
        return "cadastro";
    }

    private List<String> validar(CadastroForm f) {
        List<String> erros = new ArrayList<>();

        if (f.getNome() == null || f.getNome().isBlank() || f.getNome().length() > 100) {
            erros.add("Informe seu nome (até 100 caracteres).");
        }

        String email = f.getEmail() == null ? "" : f.getEmail().trim().toLowerCase();
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") || email.length() > 150) {
            erros.add("Informe um e-mail válido.");
        } else if (usuarios.existsByUsername(email)) {
            erros.add("Já existe uma conta com esse e-mail.");
        }

        String tel = somenteDigitos(f.getTelefone());
        if (!tel.isEmpty() && (tel.length() < 10 || tel.length() > 11)) {
            erros.add("O telefone deve ter DDD e 10 ou 11 dígitos.");
        }

        String senha = f.getSenha() == null ? "" : f.getSenha();
        if (senha.length() < 8 || senha.length() > 72) {
            erros.add("A senha deve ter de 8 a 72 caracteres.");
        } else if (!senha.equals(f.getConfirmarSenha())) {
            erros.add("As senhas não são iguais.");
        }
        return erros;
    }

    private static String somenteDigitos(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }
}