package br.edu.ifrn.chamados.controller;

import br.edu.ifrn.chamados.enums.TipoUsuario;
import br.edu.ifrn.chamados.model.Usuario;
import br.edu.ifrn.chamados.model.Chamado;
import br.edu.ifrn.chamados.enums.Status;
import br.edu.ifrn.chamados.service.ChamadoService;
import br.edu.ifrn.chamados.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ChamadoService chamadoService;

    public UsuarioController(UsuarioService usuarioService, ChamadoService chamadoService) {
        this.usuarioService = usuarioService;
        this.chamadoService = chamadoService;
    }

    @PostMapping
    public ResponseEntity<Usuario> create(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(usuario));
    }

    @GetMapping
    public ResponseEntity<Page<Usuario>> findAll(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "tipo", required = false) TipoUsuario tipo,
            @RequestParam(name = "setorId", required = false) Long setorId,
            Pageable pageable) {
        return ResponseEntity.ok(usuarioService.findAll(nome, tipo, setorId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> findById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.update(id, usuario));
    }

    @GetMapping("/{id}/chamados")
    public ResponseEntity<Page<Chamado>> findChamados(
            @PathVariable Long id,
            @RequestParam(name = "status", required = false) Status status,
            Pageable pageable) {
        return ResponseEntity.ok(chamadoService.findByUsuario(id, status, pageable));
    }
}
