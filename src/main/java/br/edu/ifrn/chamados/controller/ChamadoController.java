package br.edu.ifrn.chamados.controller;

import br.edu.ifrn.chamados.model.Chamado;
import br.edu.ifrn.chamados.service.ChamadoService;
import br.edu.ifrn.chamados.enums.Prioridade;
import br.edu.ifrn.chamados.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoService chamadoService;

    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @PostMapping
    public ResponseEntity<Chamado> create(@RequestBody Chamado chamado) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chamadoService.create(chamado));
    }

    @GetMapping
    public ResponseEntity<Page<Chamado>> findAll(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Prioridade prioridade,
            @RequestParam(required = false) Long setorId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long solicitanteId,
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataFim,
            Pageable pageable) {
        return ResponseEntity.ok(chamadoService.findAll(
                status, prioridade, setorId, categoriaId, solicitanteId, tecnicoId,
                dataInicio, dataFim, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chamado> findById(@PathVariable Long id) {
        return ResponseEntity.ok(chamadoService.findByIdComInteracoes(id));
    }

    @PatchMapping("/{id}/assumir")
    public ResponseEntity<Chamado> assumir(
            @PathVariable Long id,
            @RequestBody AssumirRequest request) {
        return ResponseEntity.ok(chamadoService.assumir(id, request.tecnicoId()));
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<Chamado> resolver(
            @PathVariable Long id,
            @RequestBody ResolverRequest request) {
        return ResponseEntity.ok(chamadoService.resolver(id, request.tecnicoId(), request.solucao()));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Chamado> cancelar(
            @PathVariable Long id,
            @RequestBody CancelarRequest request) {
        return ResponseEntity.ok(chamadoService.cancelar(id, request.solicitanteId()));
    }

    public record AssumirRequest(Long tecnicoId) {
    }

    public record ResolverRequest(Long tecnicoId, String solucao) {
    }

    public record CancelarRequest(Long solicitanteId) {
    }
}
