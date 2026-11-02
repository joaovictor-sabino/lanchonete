package com.database.aula;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AlunoService {

    private final AlunoRepository repository;

    public AlunoService(AlunoRepository repository) {
        this.repository = repository;
    }

    public List<Aluno> listar() {
        return repository.findAll();
    }

    public Aluno salvar(Aluno aluno) {
        return repository.save(aluno);
    }

    public Aluno buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado: " + id));
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
    public List<Aluno> buscarPorNome(String trecho) {
        return repository.findByNomeContainingIgnoreCase(trecho);
    }

    public List<Aluno> buscarPorCurso(String curso) {
        return repository.findByCurso(curso);
    }
}