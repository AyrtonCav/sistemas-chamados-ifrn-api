package br.edu.ifrn.chamados.service;

import br.edu.ifrn.chamados.model.Categoria;
import br.edu.ifrn.chamados.model.Setor;
import br.edu.ifrn.chamados.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import br.edu.ifrn.chamados.exception.RegraNegocioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final SetorService setorService;

    public CategoriaService(CategoriaRepository categoriaRepository, SetorService setorService) {
        this.categoriaRepository = categoriaRepository;
        this.setorService = setorService;
    }

    public Categoria create(Categoria categoria) {
        categoria.setSetor(findSetor(categoria));
        return categoriaRepository.save(categoria);
    }

    public Page<Categoria> findAll(String nome, Long setorId, Pageable pageable) {
        String nomeFiltro = nome == null || nome.isBlank() ? null : nome;
        return categoriaRepository.findByFiltros(nomeFiltro, setorId, pageable);
    }

    public Page<Categoria> findBySetorId(Long setorId, Pageable pageable) {
        setorService.findById(setorId);
        return categoriaRepository.findBySetorId(setorId, pageable);
    }

    public Categoria findById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada: " + id));
    }

    public Categoria update(Long id, Categoria categoriaAtualizada) {
        Categoria categoria = findById(id);
        categoria.setNome(categoriaAtualizada.getNome());
        categoria.setDescricao(categoriaAtualizada.getDescricao());
        categoria.setSetor(findSetor(categoriaAtualizada));
        return categoriaRepository.save(categoria);
    }

    public void delete(Long id) {
        Categoria categoria = findById(id);
        try {
            categoriaRepository.delete(categoria);
        } catch (DataIntegrityViolationException exception) {
            throw new RegraNegocioException(
                    "Não é possível excluir a categoria porque ela possui chamados vinculados.");
        }
    }

    private Setor findSetor(Categoria categoria) {
        if (categoria == null || categoria.getSetor() == null || categoria.getSetor().getId() == null) {
            throw new EntityNotFoundException("Setor não informado");
        }

        return setorService.findById(categoria.getSetor().getId());
    }
}
