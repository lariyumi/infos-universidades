package br.com.infosuniversidades.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Universidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Geração automática de IDs únicos e incrementais ao adicionar elemento no banco de dados
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false)
    private String site;
    @Enumerated(EnumType.STRING)
    private TipoUniversidade tipo;
    @ManyToMany
    @JoinTable(name = "universidade_curso", joinColumns = @JoinColumn(name = "universidade_id"), inverseJoinColumns = @JoinColumn(name = "curso_id"))
    private List<Curso> cursos;
    @OneToMany(mappedBy = "universidade")
    private List<Endereco> enderecos;
    @OneToMany(mappedBy = "universidade")
    private List<Comentario> comentarios;
    @OneToMany(mappedBy = "universidade")
    private List<ProjetoPedagogico> projetosPedagogicos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public TipoUniversidade getTipo() {
        return tipo;
    }

    public void setTipo(TipoUniversidade tipo) {
        this.tipo = tipo;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(List<Curso> cursos) {
        this.cursos = cursos;
    }

    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<Endereco> enderecos) {
        this.enderecos = enderecos;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public List<ProjetoPedagogico> getProjetosPedagogicos() {
        return projetosPedagogicos;
    }

    public void setProjetosPedagogicos(List<ProjetoPedagogico> projetosPedagogicos) {
        this.projetosPedagogicos = projetosPedagogicos;
    }
}
