package br.edu.ifrn.chamados.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "setor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Setor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false, unique = true)
    private String sigla;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "setor")
    private List<Categoria> categorias = new ArrayList<>();

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "setor")
    private List<Usuario> usuarios = new ArrayList<>();
}


