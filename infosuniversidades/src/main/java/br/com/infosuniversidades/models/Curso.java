package br.com.infosuniversidades.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String nome;
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String descricao;
    @ManyToMany(mappedBy = "cursos")
    private List<Universidade> universidade;
    @OneToMany(mappedBy = "curso")
    private List<ProjetoPedagogico> projetosPedagogicos;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Universidade> getUniversidade() {
        return universidade;
    }

    public void setUniversidade(List<Universidade> universidade) {
        this.universidade = universidade;
    }

    public List<ProjetoPedagogico> getProjetosPedagogicos() {
        return projetosPedagogicos;
    }

    public void setProjetosPedagogicos(List<ProjetoPedagogico> projetosPedagogicos) {
        this.projetosPedagogicos = projetosPedagogicos;
    }
}
