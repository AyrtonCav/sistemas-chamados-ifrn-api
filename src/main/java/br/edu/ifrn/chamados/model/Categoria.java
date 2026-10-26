package br.edu.ifrn.chamados.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descricao;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "setor_id")
    private Setor setor;
}
