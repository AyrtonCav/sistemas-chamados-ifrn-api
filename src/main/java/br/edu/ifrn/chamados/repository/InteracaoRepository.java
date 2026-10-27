package br.edu.ifrn.chamados.repository;

import br.edu.ifrn.chamados.model.Interacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteracaoRepository extends JpaRepository<Interacao, Long> {

    List<Interacao> findByChamadoIdOrderByDataHoraDesc(Long chamadoId);

}
