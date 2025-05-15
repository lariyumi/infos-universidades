package br.com.infosuniversidades.repositories;

import br.com.infosuniversidades.models.ProjetoPedagogico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjetoPedagogicoRepository extends JpaRepository<ProjetoPedagogico, Long> {
}
