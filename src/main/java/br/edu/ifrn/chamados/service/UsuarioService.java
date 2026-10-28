package br.edu.ifrn.chamados.service;

import br.edu.ifrn.chamados.enums.TipoUsuario;
import br.edu.ifrn.chamados.exception.RegraNegocioException;
import br.edu.ifrn.chamados.model.Setor;
import br.edu.ifrn.chamados.model.Usuario;
import br.edu.ifrn.chamados.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SetorService setorService;

    public UsuarioService(UsuarioRepository usuarioRepository, SetorService setorService) {
        this.usuarioRepository = usuarioRepository;
        this.setorService = setorService;
    }

    public Usuario create(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw emailDuplicado();
        }

        usuario.setSetor(resolveSetor(usuario));
        return usuarioRepository.save(usuario);
    }

    public Page<Usuario> findAll(String nome, TipoUsuario tipo, Long setorId, Pageable pageable) {
        String nomeFiltro = nome == null || nome.isBlank() ? null : nome;
        return usuarioRepository.findByFiltros(nomeFiltro, tipo, setorId, pageable);
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));
    }

    public Usuario update(Long id, Usuario usuarioAtualizado) {
        Usuario usuario = findById(id);

        if (usuarioRepository.existsByEmailAndIdNot(usuarioAtualizado.getEmail(), id)) {
            throw emailDuplicado();
        }

        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setMatricula(usuarioAtualizado.getMatricula());
        usuario.setTipo(usuarioAtualizado.getTipo());
        usuario.setSetor(resolveSetor(usuarioAtualizado));
        return usuarioRepository.save(usuario);
    }

    private Setor resolveSetor(Usuario usuario) {
        if (usuario.getTipo() == TipoUsuario.SOLICITANTE) {
            return null;
        }

        if (usuario.getTipo() != TipoUsuario.TECNICO) {
            throw new RegraNegocioException("Tipo de usuário é obrigatório.");
        }

        if (usuario.getSetor() == null || usuario.getSetor().getId() == null) {
            throw new RegraNegocioException("Técnicos devem estar vinculados a um setor.");
        }

        return setorService.findById(usuario.getSetor().getId());
    }

    private RegraNegocioException emailDuplicado() {
        return new RegraNegocioException("E-mail já cadastrado.");
    }
}
