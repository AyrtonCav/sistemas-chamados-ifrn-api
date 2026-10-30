package br.edu.ifrn.chamados.controller;

import br.edu.ifrn.chamados.model.Categoria;
import br.edu.ifrn.chamados.service.CategoriaService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<Categoria> create(@RequestBody Categoria categoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.create(categoria));
    }

    @GetMapping
    public ResponseEntity<Page<Categoria>> findAll(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "setorId", required = false) Long setorId,
            Pageable pageable) {
        return ResponseEntity.ok(categoriaService.findAll(nome, setorId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> update(
            @PathVariable Long id,
            @RequestBody Categoria categoria) {
        return ResponseEntity.ok(categoriaService.update(id, categoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
