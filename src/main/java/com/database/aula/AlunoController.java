package com.database.aula;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AlunoController {

    private final AlunoService service;

    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @GetMapping("/alunos")
    public String listar(@RequestParam(required = false) String busca, Model model) {
        if (busca != null && !busca.isBlank()) {
            model.addAttribute("alunos", service.buscarPorNome(busca));
        } else {
            model.addAttribute("alunos", service.listar());
        }
        return "alunos";
    }

    @GetMapping("/alunos/novo")
    public String novo(Model model) {
        model.addAttribute("aluno", new Aluno());
        return "form";
    }

    @PostMapping("/alunos/salvar")
    public String salvar(@ModelAttribute Aluno aluno) {
        service.salvar(aluno);
        return "redirect:/alunos";
    }

    @GetMapping("/alunos/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("aluno", service.buscarPorId(id));
        return "form";
    }

    @PostMapping("/alunos/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        service.excluir(id);
        return "redirect:/alunos";
    }
}