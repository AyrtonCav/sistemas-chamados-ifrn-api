package br.edu.ifrn.chamados.repository;

import br.edu.ifrn.chamados.enums.Status;
import br.edu.ifrn.chamados.enums.Prioridade;
import br.edu.ifrn.chamados.model.Chamado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChamadoRepository extends JpaRepository<Chamado, Long>, JpaSpecificationExecutor<Chamado> {
    @Query("SELECT c FROM Chamado c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:prioridade IS NULL OR c.prioridade = :prioridade) AND " +
           "(:setorId IS NULL OR c.categoria.setor.id = :setorId) AND " +
           "(:categoriaId IS NULL OR c.categoria.id = :categoriaId) AND " +
           "(:solicitanteId IS NULL OR c.solicitante.id = :solicitanteId) AND " +
           "(:tecnicoId IS NULL OR c.tecnico.id = :tecnicoId) AND " +
           "(:dataInicio IS NULL OR c.dataAbertura >= :dataInicio) AND " +
           "(:dataFim IS NULL OR c.dataAbertura <= :dataFim)")
    Page<Chamado> findByFiltros(
            @Param("status") Status status,
            @Param("prioridade") Prioridade prioridade,
            @Param("setorId") Long setorId,
            @Param("categoriaId") Long categoriaId,
            @Param("solicitanteId") Long solicitanteId,
            @Param("tecnicoId") Long tecnicoId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @Query("SELECT c FROM Chamado c WHERE " +
           "(c.solicitante.id = :usuarioId OR c.tecnico.id = :usuarioId) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<Chamado> findByUsuarioIdAndStatus(
            @Param("usuarioId") Long usuarioId,
            @Param("status") Status status,
            Pageable pageable
    );

    @Query("SELECT c FROM Chamado c LEFT JOIN FETCH c.interacoes WHERE c.id = :id")
    Optional<Chamado> findByIdWithInteracoes(@Param("id") Long id);

    /* Mantém a consulta legada disponível para os próximos endpoints. */
    @Query("SELECT c FROM Chamado c WHERE " +
           "(c.solicitante.id = :usuarioId OR c.tecnico.id = :usuarioId) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<Chamado> findByFiltros(
            @Param("usuarioId") Long usuarioId,
            @Param("status") Status status,
            Pageable pageable
    );
}
