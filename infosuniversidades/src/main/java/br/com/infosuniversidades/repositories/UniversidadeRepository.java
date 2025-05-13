package br.com.infosuniversidades.repositories;

import br.com.infosuniversidades.models.Universidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//interage com o banco de dados, criando objeto e injetando ele no banco
@Repository
public interface UniversidadeRepository extends JpaRepository<Universidade, Long> {
    Optional<Universidade> findByNome(String nome);
}
