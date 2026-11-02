package com.database.aula;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    List<Aluno> findByNomeContainingIgnoreCase(String trecho);

    List<Aluno> findByCurso(String curso);
}