package br.edu.ifrn.chamados.service;

import br.edu.ifrn.chamados.enums.Prioridade;
import br.edu.ifrn.chamados.enums.Status;
import br.edu.ifrn.chamados.exception.RegraNegocioException;
import br.edu.ifrn.chamados.model.Chamado;
import br.edu.ifrn.chamados.model.Categoria;
import br.edu.ifrn.chamados.model.Usuario;
import br.edu.ifrn.chamados.repository.ChamadoRepository;
import br.edu.ifrn.chamados.enums.TipoUsuario;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;

    public ChamadoService(
            ChamadoRepository chamadoRepository,
            UsuarioService usuarioService,
            CategoriaService categoriaService) {
        this.chamadoRepository = chamadoRepository;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
    }

    @Transactional
    public Chamado create(Chamado chamado) {
        chamado.setId(null);
        chamado.setTecnico(null);
        chamado.setDataFechamento(null);
        chamado.setSolucao(null);

        Usuario solicitante = findSolicitante(chamado);
        Categoria categoria = findCategoria(chamado);

        chamado.setSolicitante(solicitante);
        chamado.setCategoria(categoria);
        chamado.setStatus(Status.ABERTO);
        chamado.setDataAbertura(LocalDateTime.now());

        if (chamado.getPrioridade() == null) {
            chamado.setPrioridade(Prioridade.MEDIA);
        }

        return chamadoRepository.save(chamado);
    }

    @Transactional
    public Chamado assumir(Long id, Long tecnicoId) {
        Chamado chamado = findById(id);
        Usuario tecnico = findTecnico(tecnicoId);

        if (chamado.getStatus() != Status.ABERTO) {
            throw new RegraNegocioException("Chamado não está aberto.");
        }

        if (tecnico.getSetor() == null || chamado.getCategoria() == null
                || chamado.getCategoria().getSetor() == null
                || !Objects.equals(tecnico.getSetor().getId(), chamado.getCategoria().getSetor().getId())) {
            throw new RegraNegocioException("Técnico de outro setor");
        }

        chamado.setStatus(Status.EM_ATENDIMENTO);
        chamado.setTecnico(tecnico);
        return chamadoRepository.save(chamado);
    }

    @Transactional
    public Chamado resolver(Long id, Long tecnicoId, String solucao) {
        Chamado chamado = findById(id);

        if (chamado.getStatus() != Status.EM_ATENDIMENTO) {
            throw new RegraNegocioException("Chamado não está em atendimento.");
        }

        if (chamado.getTecnico() == null || !Objects.equals(chamado.getTecnico().getId(), tecnicoId)) {
            throw new RegraNegocioException("Apenas o técnico atribuído pode resolver");
        }

        if (solucao == null || solucao.isBlank()) {
            throw new RegraNegocioException("Solução é obrigatória");
        }

        chamado.setStatus(Status.RESOLVIDO);
        chamado.setSolucao(solucao);
        chamado.setDataFechamento(LocalDateTime.now());
        return chamadoRepository.save(chamado);
    }

    @Transactional
    public Chamado cancelar(Long id, Long solicitanteId) {
        Chamado chamado = findById(id);

        if (chamado.getStatus() != Status.ABERTO) {
            throw new RegraNegocioException("Chamado não está aberto.");
        }

        if (chamado.getSolicitante() == null
                || !Objects.equals(chamado.getSolicitante().getId(), solicitanteId)) {
            throw new RegraNegocioException("Apenas o solicitante pode cancelar");
        }

        chamado.setStatus(Status.CANCELADO);
        chamado.setDataFechamento(LocalDateTime.now());
        return chamadoRepository.save(chamado);
    }

    public Page<Chamado> findAll(
            Status status,
            Prioridade prioridade,
            Long setorId,
            Long categoriaId,
            Long solicitanteId,
            Long tecnicoId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Pageable pageable) {
        return chamadoRepository.findByFiltros(
                status, prioridade, setorId, categoriaId, solicitanteId, tecnicoId,
                dataInicio, dataFim, pageable);
    }

    public Page<Chamado> findByUsuario(Long usuarioId, Status status, Pageable pageable) {
        usuarioService.findById(usuarioId);
        return chamadoRepository.findByUsuarioIdAndStatus(usuarioId, status, pageable);
    }

    public Chamado findById(Long id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado: " + id));
    }

    public Chamado findByIdComInteracoes(Long id) {
        return chamadoRepository.findByIdWithInteracoes(id)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado: " + id));
    }

    private Usuario findTecnico(Long tecnicoId) {
        try {
            Usuario tecnico = usuarioService.findById(tecnicoId);
            if (tecnico.getTipo() != TipoUsuario.TECNICO) {
                throw new RegraNegocioException("Usuário informado não é técnico.");
            }
            return tecnico;
        } catch (EntityNotFoundException exception) {
            throw new RegraNegocioException("Técnico não encontrado.");
        }
    }

    private Usuario findSolicitante(Chamado chamado) {
        if (chamado == null || chamado.getSolicitante() == null
                || chamado.getSolicitante().getId() == null) {
            throw new EntityNotFoundException("Solicitante não informado");
        }

        Usuario solicitante = usuarioService.findById(chamado.getSolicitante().getId());
        if (solicitante.getTipo() != TipoUsuario.SOLICITANTE) {
            throw new RegraNegocioException("Apenas solicitantes podem abrir chamados.");
        }

        return solicitante;
    }

    private Categoria findCategoria(Chamado chamado) {
        if (chamado == null || chamado.getCategoria() == null
                || chamado.getCategoria().getId() == null) {
            throw new EntityNotFoundException("Categoria não informada");
        }

        return categoriaService.findById(chamado.getCategoria().getId());
    }
}
