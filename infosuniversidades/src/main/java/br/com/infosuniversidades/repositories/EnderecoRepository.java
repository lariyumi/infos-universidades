package br.com.infosuniversidades.repositories;

import br.com.infosuniversidades.models.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
