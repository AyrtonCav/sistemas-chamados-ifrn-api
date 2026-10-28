package br.edu.ifrn.chamados.service;

import br.edu.ifrn.chamados.exception.RegraNegocioException;
import br.edu.ifrn.chamados.model.Setor;
import br.edu.ifrn.chamados.repository.SetorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class SetorService {

    private final SetorRepository setorRepository;

    public SetorService(SetorRepository setorRepository) {
        this.setorRepository = setorRepository;
    }

    public Setor create(Setor setor) {
        return setorRepository.save(setor);
    }

    public Page<Setor> findAll(String nome, Pageable pageable) {
        if (nome == null || nome.isBlank()) {
            return setorRepository.findAll(pageable);
        }

        return setorRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public Setor findById(Long id) {
        return setorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Setor não encontrado: " + id));
    }

    public Setor update(Long id, Setor setorAtualizado) {
        Setor setor = findById(id);
        setor.setNome(setorAtualizado.getNome());
        setor.setSigla(setorAtualizado.getSigla());
        return setorRepository.save(setor);
    }

    public void delete(Long id) {
        try {
            setorRepository.deleteById(id);
        } catch (DataIntegrityViolationException exception) {
            throw new RegraNegocioException(
                    "Não é possível excluir o setor porque ele possui registros associados.");
        }
    }
}
