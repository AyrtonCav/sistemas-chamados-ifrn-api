package br.edu.ifrn.chamados.controller;

import br.edu.ifrn.chamados.model.Setor;
import br.edu.ifrn.chamados.model.Categoria;
import br.edu.ifrn.chamados.service.CategoriaService;
import br.edu.ifrn.chamados.service.SetorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setores")
public class SetorController {

    private final SetorService setorService;
    private final CategoriaService categoriaService;

    public SetorController(SetorService setorService, CategoriaService categoriaService) {
        this.setorService = setorService;
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<Setor> create(@RequestBody Setor setor) {
        Setor setorCriado = setorService.create(setor);
        return ResponseEntity.status(HttpStatus.CREATED).body(setorCriado);
    }

    @GetMapping
    public ResponseEntity<Page<Setor>> findAll(
            @RequestParam(name = "nome", required = false) String nome,
            Pageable pageable) {
        return ResponseEntity.ok(setorService.findAll(nome, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Setor> findById(@PathVariable Long id) {
        return ResponseEntity.ok(setorService.findById(id));
    }

    @GetMapping("/{id}/categorias")
    public ResponseEntity<Page<Categoria>> findCategorias(
            @PathVariable Long id,
            Pageable pageable) {
        return ResponseEntity.ok(categoriaService.findBySetorId(id, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Setor> update(
            @PathVariable Long id,
            @RequestBody Setor setor) {
        return ResponseEntity.ok(setorService.update(id, setor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        setorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
