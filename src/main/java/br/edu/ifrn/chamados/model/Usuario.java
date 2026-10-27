package br.edu.ifrn.chamados.model;

import br.edu.ifrn.chamados.enums.TipoUsuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // 3. Apenas o ID define a igualdade
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    @Column(nullable = false, unique = true)
    @NotNull(message = "A matrícula é obrigatória")
    private Integer matricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "O tipo de usuário é obrigatório")
    private TipoUsuario tipo;

    @ManyToOne
    @JoinColumn(name = "setor_id")
    private Setor setor;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "autor")
    private List<Interacao> interacoes = new ArrayList<>();

}
